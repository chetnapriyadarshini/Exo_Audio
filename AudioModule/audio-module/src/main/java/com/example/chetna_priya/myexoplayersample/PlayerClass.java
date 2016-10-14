package com.example.chetna_priya.myexoplayersample;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;

import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerClass implements DemoPlayer.Listener, SurfaceHolder.Callback {

    private static final String TAG = PlayerClass.class.getSimpleName();
    private static final int FORWARD_DURATION_MS = 30 * 1000;
    private static final int BACKWARD_DURATION_MS = 30 * 1000;
    private DemoPlayer player;
    private EventLogger eventLogger;
    private MediaController mediaController;
    private boolean playerNeedsPrepare;
    private SurfaceView surfaceView;
    private Context mContext;
    private Uri[] mTrackUris;
    private static int playerIndex = 0;
    public CopyOnWriteArrayList<Callbacks> listeners;

    private long playerPosition = 0;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    public interface Callbacks{
        void updateSeekBar(int timeInMs, int totalDur);
        void setFwdBtnEnabled(boolean enabled);
        void setBwdBtnEnabled(boolean enabled);
    }

    public void setCallbacksListener(Callbacks callbacks){
        listeners.add(callbacks);
    }

    public void removeCallbacks(Callbacks callbacks){
        listeners.remove(callbacks);
    }


    public PlayerClass(Context context, SurfaceView surfaceView, Uri[] uri){
        mContext = context;
        this.surfaceView = surfaceView;
        surfaceView.getHolder().addCallback(this);
        mediaController = new MediaController(mContext);
        mediaController.setAnchorView(surfaceView);
        listeners = new CopyOnWriteArrayList<>();
        mTrackUris = uri;
        preparePlayer(false);
        playerIndex = 0;
    }


    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            Log.d(TAG, "Player null intialize and prepare");
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        //    updateButtonVisibilities();
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
        Log.d(TAG, "Player set play when readyyyy");
    }
/*
    private static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        return Util.inferContentType(lastPathSegment);
    }*/

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        Log.d(TAG, "Build Renderer");
        String userAgent = Util.getUserAgent(mContext, "MyExoPlayerSample");
        return new ExtractorRendererBuilder(mContext, userAgent, getTrackUri());
    }


    private @NonNull Uri getTrackUri() {
        return mTrackUris[playerIndex];
    }



    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "Media State Changeddd "+playbackState);

    }

    @Override
    public void onError(Exception e) {

    }

    public boolean isPlaying(){
        return player.getPlayerControl().isPlaying();
    }

    public void play(){
        player.getPlayerControl().start();
    }

    public void pause(){
        player.getPlayerControl().pause();
    }

    public void forward() {
        player.getPlayerControl().forward(FORWARD_DURATION_MS);
        for(Callbacks callbacks : listeners){
            callbacks.updateSeekBar(FORWARD_DURATION_MS, player.getPlayerControl().getDuration());
        }
    }

    public void backward() {
        player.getPlayerControl().backward(BACKWARD_DURATION_MS);
        for(Callbacks callbacks : listeners){
            callbacks.updateSeekBar(-BACKWARD_DURATION_MS, player.getPlayerControl().getDuration());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }
}
