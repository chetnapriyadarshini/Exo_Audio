package com.example.chetna_priya.myexoplayersample;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;

import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.Util;

public class PlayerClass implements DemoPlayer.Listener, SurfaceHolder.Callback {

    private static final String TAG = PlayerClass.class.getSimpleName();
    private DemoPlayer player;
    private EventLogger eventLogger;
    private MediaController mediaController;
    private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsPrepare;
    private SurfaceView surfaceView;
    private Context mContext;

    private long playerPosition = 0;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View root = findViewById(R.id.root);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(surfaceView);
        preparePlayer(true);
    }*/

    public PlayerClass(Context context, SurfaceView surfaceView){
        mContext = context;
        this.surfaceView = surfaceView;
        surfaceView.getHolder().addCallback(this);
        mediaController = new MediaController(mContext);
        mediaController.setAnchorView(surfaceView);
        preparePlayer(false);
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
         /*   debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugViewHelper.start();*/
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

    private static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        return Util.inferContentType(lastPathSegment);
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        Log.d(TAG, "Build Renderer");
        String userAgent = Util.getUserAgent(mContext, "MyExoPlayerSample");
        Uri contentUri = Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3");
        return new ExtractorRendererBuilder(mContext, userAgent, contentUri);
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
        Log.d(TAG, "Surface createdddddd");
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
        Log.d(TAG, "Surface destroyeddddd");
    }
}
