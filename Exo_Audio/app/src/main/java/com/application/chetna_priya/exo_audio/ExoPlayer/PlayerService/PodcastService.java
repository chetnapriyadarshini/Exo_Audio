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
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.ExoPlayer.Playback.PlayerImpl;
import com.application.chetna_priya.exo_audio.ExoPlayer.Playback.QueueManager;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.CustomPlaybackControlView;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.SmallPlaybackControlView;
import com.application.chetna_priya.exo_audio.Ui.AudioActivity;
import com.application.chetna_priya.exo_audio.ExoPlayer.Playback.PlaybackListener;
import com.application.chetna_priya.exo_audio.Model.PodcastProvider;
import com.application.chetna_priya.exo_audio.R;

import java.util.List;

import static com.application.chetna_priya.exo_audio.Utils.MediaIDHelper.MEDIA_ID_ROOT;


/**
 * Created by chetna_priya on 10/26/2016.
 */
public class PodcastService extends MediaBrowserServiceCompat implements PlaybackListener.PlaybackServiceCallback{

    private static final String TAG = PodcastService.class.getSimpleName();
    private MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private PlaybackListener mPlaybackListener;
    private PodcastProvider mPodcastProvider;

    @Override
    public void onCreate() {

        super.onCreate();
        mPodcastProvider = new PodcastProvider();
        mPodcastProvider.retrieveMediaAsync(null);

        QueueManager queueManager = new QueueManager(mPodcastProvider, getResources(),
                new QueueManager.MetadataUpdateListener() {
                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        mSession.setMetadata(metadata);
                    }

                    @Override
                    public void onMetadataRetrieveError() {
                        mPlaybackListener.updatePlaybackState(
                                getString(R.string.error_no_metadata));
                    }

                    @Override
                    public void onCurrentQueueIndexUpdated(int queueIndex) {
                        mPlaybackListener.handlePlayRequest();
                    }

                    @Override
                    public void onQueueUpdated(String title,
                                               List<MediaSessionCompat.QueueItem> newQueue) {
                        mSession.setQueue(newQueue);
                        mSession.setQueueTitle(title);
                    }
                });

        PlayerImpl player = new PlayerImpl(getApplicationContext());//TODO proper implementation
        mPlaybackListener = new PlaybackListener(this, getApplicationContext(), mPodcastProvider, player);
        mSession = new MediaSessionCompat(this, "PodcastService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(mPlaybackListener.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, AudioActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);
        mPlaybackListener.updatePlaybackState(null);
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
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        mPlaybackListener.handleStopRequest(null);
        mMediaNotificationManager.stopNotification();
/*
        mDelayedStopHandler.removeCallbacksAndMessages(null);*/
        mSession.release();
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
                                 Bundle rootHints) {
        Log.d(TAG, "OnGetRoot: clientPackageName=" + clientPackageName+
                "; clientUid=" + clientUid + " ; rootHints="+ rootHints);
        // To ensure you are not allowing any arbitrary app to browse your app's contents, you
        // need to check the origin:
        /*if (!mPackageValidator.isCallerAllowed(this, clientPackageName, clientUid)) {
            // If the request comes from an untrusted package, return null. No further calls will
            // be made to other media browsing methods.
            Log.w(TAG, "OnGetRoot: IGNORING request from untrusted package "
                    + clientPackageName);
            return null;
        }*/

        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }


    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "OnLoadChildren: parentMediaId="+ parentMediaId);
        if (mPodcastProvider.isInitialized()) {
            // if music library is ready, return immediately
            result.sendResult(mPodcastProvider.getChildren(parentMediaId, getResources()));
        } else {
            // otherwise, only return results when the music library is retrieved
            result.detach();
            mPodcastProvider.retrieveMediaAsync(new PodcastProvider.Callback() {
                @Override
                public void onMusicCatalogReady(boolean success) {
                    result.sendResult(mPodcastProvider.getChildren(parentMediaId, getResources()));
                }
            });
        }
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
        stopForeground(true);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
    }
}
