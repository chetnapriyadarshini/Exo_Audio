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
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
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
    private static final int NOTIFICATION_ID = 1;
    private final IBinder mIBinder = new LocalBinder();

    private Handler mHandler = new Handler();
    private SimpleExoPlayer exoPlayer = null;
    PlayerListener playerListener;
    public static boolean isServiceRunning = false;
    private boolean isPlayerInstantiated = false;

    public ExoPlayerService() {
        super();
    }

    public void setListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
        if(!isPlayerInstantiated)
        {
            isPlayerInstantiated = true;
            playerListener.onPlayerInstatiated(exoPlayer);
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
        Log.d(TAG, "ON START COMMAND CALLEDDDDDDD");
        if(isServiceRunning){
            /* Service creation is done and the listener should proceed
            * with rest of initializations on there part*/
            notifyListener();
        }
        foreground();
        return super.onStartCommand(intent, flags, startId);
    }

    public SimpleExoPlayer getPlayerInstance(){
        return exoPlayer;
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
        notifyListener();
        isServiceRunning = true;
        Log.d(TAG, "EXO PLAYER CREATED IN SERVICE "+playerListener);

    }

    private void notifyListener() {
        if(playerListener != null) {
            isPlayerInstantiated = true;
            playerListener.onPlayerInstatiated(exoPlayer);
        }else
            isPlayerInstantiated = false;
    }

    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }

    public void background(boolean removeNotification){
        stopForeground(removeNotification);
    }

    public void foreground(){
      //  if(!isforeground)
        {
            Log.d(TAG, "!!!!!!!!!!!!!!!!!Put Service in foreground!!!!!!!!!!!!!!!!!!");
       //     startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, AudioActivity.class);
        intent.putExtra("from_notification", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("Ticker Title")
                        .setContentText("Ticker Content")
                        .setContentIntent(pendIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        return notification;
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "Service on destroy called !!!!!!!!!!!!");
        /*Set this to false as the player unbinds from the service on being destroyed
        this allows for a new instance of the player to be instantiated again */
        isPlayerInstantiated = false;

       /* if(mHandler != null)
        {
            mHandler = null;
        }

        if(playerListener != null)
            playerListener.releasePlayer(exoPlayer);
        exoPlayer = null;
*/
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mIBinder;
    }
}
