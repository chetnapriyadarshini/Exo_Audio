package com.app.chetna_priya.audiomodule.Exoplayer;

import android.os.Handler;
import android.os.Looper;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.util.PlayerControl;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chetna_priya on 9/4/2016.
 */
public class ExoAudioPlayer extends AudioPlayerImpl implements ExoPlayer.Listener{


    // Constants pulled into this class for convenience.
    public static final int STATE_IDLE = ExoPlayer.STATE_IDLE;
    public static final int STATE_PREPARING = ExoPlayer.STATE_PREPARING;
    public static final int STATE_BUFFERING = ExoPlayer.STATE_BUFFERING;
    public static final int STATE_READY = ExoPlayer.STATE_READY;
    public static final int STATE_ENDED = ExoPlayer.STATE_ENDED;
    public static final int TRACK_DISABLED = ExoPlayer.TRACK_DISABLED;
    public static final int TRACK_DEFAULT = ExoPlayer.TRACK_DEFAULT;


    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private int rendererBuildingState;

    public static final int RENDERER_COUNT = 1;
    public static final int TYPE_AUDIO = 0;
    private final PlayerControl playerControl;
    private ExoPlayer exoPlayer;
    private CopyOnWriteArrayList<ExoAudioPlayer.Listener> listeners;
    public Handler mainHandler = new Handler();
    private RendererListener rendererBuilder;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;

    public ExoAudioPlayer(RendererListener rendererBuilder){
        this.rendererBuilder = rendererBuilder;
        exoPlayer = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
        exoPlayer.addListener(this);
        playerControl = new PlayerControl(exoPlayer);
        listeners = new CopyOnWriteArrayList();
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        for (Listener listener : listeners) {
            listener.onError(error);
        }
    }

    /**
     * A listener for core events.
     */
    public interface Listener {
        void onStateChanged(boolean playWhenReady, int playbackState);
        void onError(Exception e);
    }


    public interface RendererListener {
        void buildRenderers(ExoAudioPlayer exoAudioPlayer);
        void cancel();
    }

    public void onSuccess(TrackRenderer[] renderers, DefaultBandwidthMeter bandwidthMeter) {
        exoPlayer.prepare(renderers);
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILT;
    }

    public void prepare() {
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT) {
            exoPlayer.stop();
        }
        rendererBuilder.cancel();
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILDING;
        maybeReportPlayerState();
        rendererBuilder.buildRenderers(this);
    }

    public int getPlaybackState() {
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILDING) {
            return STATE_PREPARING;
        }
        int playerState = exoPlayer.getPlaybackState();
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT && playerState == STATE_IDLE) {
            // This is an edge case where the renderers are built, but are still being passed to the
            // player's playback thread.
            return STATE_PREPARING;
        }
        return playerState;
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = exoPlayer.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
            for (Listener listener : listeners) {
                listener.onStateChanged(playWhenReady, playbackState);
            }
            lastReportedPlayWhenReady = playWhenReady;
            lastReportedPlaybackState = playbackState;
        }
    }

    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }

    public int getBufferedPercentage() {
        return exoPlayer.getBufferedPercentage();
    }

    public boolean getPlayWhenReady() {
        return exoPlayer.getPlayWhenReady();
    }

    Looper getPlaybackLooper() {
        return exoPlayer.getPlaybackLooper();
    }

    public int getTrackCount(int type) {
        return exoPlayer.getTrackCount(type);
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public MediaFormat getTrackFormat(int type, int index) {
        return exoPlayer.getTrackFormat(type, index);
    }
    public int getSelectedTrack(int type) {
        return exoPlayer.getSelectedTrack(type);
    }

    public PlayerControl getPlayerControl(){
        return playerControl;
    }
}
