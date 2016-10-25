package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
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
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            createNotification();
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
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
        return START_STICKY;
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


    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }


    private NotificationCompat.Builder createNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art, Constants.getDefaultAlbumArt(this));
        views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);

        Intent previousIntent = new Intent(this, ExoPlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, ExoPlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, ExoPlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, ExoPlayerService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);


        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);

        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Song Title");

        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");

        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

        Intent intent = new Intent(this, AudioActivity.class);
   //     intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AudioActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder status =
                new NotificationCompat.Builder(getApplicationContext());
        status.setContent(views);
        status.setContentIntent(resultPendingIntent);
        status.setCustomBigContentView(bigViews);
        status.setSmallIcon(R.drawable.ic_launcher);
      //  status.flags |= Notification.FLAG_NO_CLEAR;
        /*status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_launcher;
        status.contentIntent = pendIntent;

        status.flags |= Notification.FLAG_NO_CLEAR;*/
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status.build());
        return status;
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
