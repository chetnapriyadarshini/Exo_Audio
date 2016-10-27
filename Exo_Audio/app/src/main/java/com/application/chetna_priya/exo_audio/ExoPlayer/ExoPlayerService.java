package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

/**
 * Created by chetna_priya on 10/20/2016.
 */

public class ExoPlayerService extends Service {

    private static final String TAG = ExoPlayerService.class.getSimpleName();
    private final IBinder mIBinder = new LocalBinder();

    private Handler mHandler = new Handler();
    private SimpleExoPlayer exoPlayer = null;
    //MyPlayerListener playerListener;
    PlayerListener playerListener;
    private boolean isPlayerInstantiated = false;
    private RemoteViews views;
    private RemoteViews bigViews;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaSessionManager mManager;
    private MediaSessionCompat mSession;
    private MediaControllerCompat mController;

    public ExoPlayerService() {
        super();
    }

    public void setListener(PlayerListener listener) {
        this.playerListener = listener;
        if(!isPlayerInstantiated){
            isPlayerInstantiated = true;
            listener.onPlayerInstatiated(exoPlayer);
        }
    }


    public interface PlayerListener{
        void releasePlayer(SimpleExoPlayer exoPlayer);
        void onPlayerInstatiated(SimpleExoPlayer exoPlayer);
    }

    public class LocalBinder extends Binder {
        public ExoPlayerService getInstance() {
            // Return this instance of LocalService so clients can call public methods
            return ExoPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            createNotification();
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            playPause();
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Next");
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;*/

        if( mManager == null ) {
            initMediaSessions();
        }

        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() {
        mSession = new MediaSessionCompat(getApplicationContext(), "simple player session");
        try {
            mController =new MediaControllerCompat(getApplicationContext(), mSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mSession.setCallback(new ComponentListener());
    }

    private class ComponentListener extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            super.onPlay();
            Log.e( "MediaPlayerService", "onPlay");
         //   buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.e( "MediaPlayerService", "onPause");
        //    buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            Log.e( "MediaPlayerService", "onSkipToNext");
            //Change media here
        //    buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            Log.e( "MediaPlayerService", "onSkipToPrevious");
            //Change media here
          //  buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            Log.e( "MediaPlayerService", "onFastForward");
            //Manipulate current media here
        }

        @Override
        public void onRewind() {
            super.onRewind();
            Log.e( "MediaPlayerService", "onRewind");
            //Manipulate current media here
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e( "MediaPlayerService", "onStop");
            //Stop media player here
           /* NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel( 1 );
            Intent intent = new Intent( getApplicationContext(), ExoPlayerService.class );
            stopService( intent );*/
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service on create calledddd");
        super.onCreate();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(mHandler, videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the exoPlayer
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector, loadControl);
        Log.d(TAG, "EXO PLAYER CREATED IN SERVICE ");
        if(playerListener != null){
            isPlayerInstantiated = true;
            playerListener.onPlayerInstatiated(exoPlayer);
        }else{
            isPlayerInstantiated = false;
        }
    }

    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            mController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            mController.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
            mController.getTransportControls().fastForward();
        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
            mController.getTransportControls().rewind();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            mController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            mController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            mController.getTransportControls().stop();
        }
    }

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), ExoPlayerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void buildNotification( NotificationCompat.Action action ) {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), ExoPlayerService.class );
        intent.setAction( ACTION_STOP );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle( "Media Title" );
        builder.setContentText( "Media Artist" );
        builder.setDeleteIntent(pendingIntent);
        builder.setStyle(style);


        builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ) );
        builder.addAction( action );
        builder.addAction( generateAction( android.R.drawable.ic_media_ff, "Fast Foward", ACTION_FAST_FORWARD ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
        style.setShowActionsInCompactView(0,1,2,3,4);

        startForeground(1, builder.build());

    //    NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
     //   notificationManager.notify( 1, builder.build() );
    }


    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "Service on destroy called !!!!!!!!!!!!");
        /*Set this to false as the player unbinds from the service on being destroyed
        this allows for a new instance of the player to be instantiated again */

        isPlayerInstantiated = false;

        if(mHandler != null)
        {
            mHandler = null;
        }

        if(playerListener != null)
            playerListener.releasePlayer(exoPlayer);
        exoPlayer = null;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        /* Set isPlayerInstantiated = false, as this service does not get destroyed on unbinding, we want all the clients
         * binding to it to go ahead and use already create exoplayer instance */
        isPlayerInstantiated = false;
        return super.onUnbind(intent);
    }
}
