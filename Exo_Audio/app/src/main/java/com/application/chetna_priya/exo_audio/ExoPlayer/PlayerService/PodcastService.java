package com.application.chetna_priya.exo_audio.ExoPlayer.PlayerService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.ExoPlayer.Playback.PlayerImpl;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.CustomPlaybackControlView;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.SmallPlaybackControlView;
import com.application.chetna_priya.exo_audio.Ui.AudioActivity;
import com.application.chetna_priya.exo_audio.ExoPlayer.Playback.PlaybackListener;
import com.application.chetna_priya.exo_audio.Model.PodcastProvider;
import com.application.chetna_priya.exo_audio.R;

import java.util.List;


/**
 * Created by chetna_priya on 10/26/2016.
 */
public class PodcastService extends MediaBrowserServiceCompat implements PlaybackListener.PlaybackServiceCallback {

    private static final String TAG = PodcastService.class.getSimpleName();
    public static boolean isServiceRunning = false;
    private MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private PlaybackListener mPlaybackListener;
    private IBinder mBinder = new LocalBinder();
    private PlayerImpl player = null;

    @Override
    public IBinder onBind(Intent intent) {
     //   Log.d(TAG, "ON BIND RETURNINGGGGG    "+super.onBind(intent));
        return mBinder;

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
/*
    public class LocalBinder extends Binder {
        public PodcastService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PodcastService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
*/


    public class LocalBinder extends Binder {
        public PodcastService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PodcastService.this;
        }
    }


    @Override
    public void onCreate() {

        super.onCreate();
        player = new PlayerImpl(getApplicationContext());
        mSession = new MediaSessionCompat(this, "PodcastService");
        setSessionToken(mSession.getSessionToken());
        //TODO put forth a proper implementation for metadata
        mSession.setMetadata(new PodcastProvider().getPodcast("1234"));

        mPlaybackListener = new PlaybackListener(this, getApplicationContext(), new PodcastProvider(), player);
        mSession.setCallback(mPlaybackListener.getMediaSessionCallback());

        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, AudioActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);
        try {
            mMediaNotificationManager = new MediaNotificationManager(this);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create a MediaNotificationManager", e);
        }
    }


    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {

        if (startIntent != null) {
            // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
            MediaButtonReceiver.handleIntent(mSession, startIntent);
        }
        isServiceRunning = true;
        return START_STICKY;
    }


    @Override
    public void onPlaybackStart() {
        if (!mSession.isActive()) {
            mSession.setActive(true);
        }
        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        startService(new Intent(getApplicationContext(), PodcastService.class));
    }


    @Override
    public void onNotificationRequired() {
        mMediaNotificationManager.startNotification();
    }

    @Override
    public void onPlaybackStop() {
        mSession.setActive(false);
        stopForeground(true);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // Returning null == no one can connect
        // so we’ll return something, so that BrowserCompat can be implemented by UI and used to connect
        return new BrowserRoot(getString(R.string.app_name), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //TODO send sensible result
        /*
        If sending a huge result, push this result to another thread and then load it asynchronously and
        return result from there
        You’ll find that you’ll get an IllegalStateException if you fail to call detach() or sendResult() before returning.
        This is 100% expected. Make sure every code path calls one or the other.
         */
        result.sendResult(null);
    }


    public void setViewForPlayer(SmallPlaybackControlView playbackControlView) {
        player.attachView(playbackControlView);
    }

    public void setViewForPlayer(CustomPlaybackControlView playbackControlView) {
        player.attachView(playbackControlView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        mPlaybackListener.handleStopRequest(null);
        mMediaNotificationManager.stopNotification();
        mSession.release();
    }
}
