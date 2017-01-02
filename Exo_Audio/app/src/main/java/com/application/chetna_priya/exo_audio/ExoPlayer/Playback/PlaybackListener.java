package com.application.chetna_priya.exo_audio.exoplayer.playback;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.entity.MetadataEntity;
import com.application.chetna_priya.exo_audio.model.MutableMediaMetadata;
import com.application.chetna_priya.exo_audio.model.PodcastProvider;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.ui.DownloadFragment;
import com.application.chetna_priya.exo_audio.ui.playbackcontrolview.SmallPlaybackControlView;
import com.application.chetna_priya.exo_audio.utils.DBHelper;
import com.application.chetna_priya.exo_audio.utils.MediaIDHelper;
import com.application.chetna_priya.exo_audio.utils.MetadataHelper;


/**
 * Created by chetna_priya on 10/27/2016.
 */

public class PlaybackListener implements Playback.Callback {

    private static final String TAG = PlaybackListener.class.getSimpleName();

    // Action to thumbs up a media item
    private static final String CUSTOM_ACTION_THUMBS_UP = "com.example.android.uamp.THUMBS_UP";

    private PodcastProvider mPodcastProvider;//TODO check in the thumbs up custom action
    private QueueManager mQueueManager;
    private Resources mResources;
    Playback mPlayback;
    private MediaSessionCompat.Callback mMediaSessionCallback;
    private PlaybackServiceCallback mServiceCallback;

    public PlaybackListener(PlaybackServiceCallback playbackServiceCallback,
                            Resources resources,
                            PodcastProvider podcastProvider,
                            QueueManager queueManager,
                            Playback playback) {
        mPodcastProvider = podcastProvider;
        mServiceCallback = playbackServiceCallback;
        mResources = resources;
        mQueueManager = queueManager;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayback = playback;
        mPlayback.setCallback(this);
    }

    public Playback getPlayback() {
        return mPlayback;
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * Handle a request to play music
     */
    public void handlePlayRequest() {
        Log.d(TAG, "handlePlayRequest: mState=" + mPlayback.getState());
        MediaSessionCompat.QueueItem currentPodcast = mQueueManager.getCurrentPodcast();
        if (currentPodcast != null) {
            mServiceCallback.onPlaybackStart();
            mPlayback.play(currentPodcast);
        }
    }

    /**
     * Handle a request to pause music
     */
    public void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + mPlayback.getState());
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }


    /**
     * Handle a request to stop music
     *
     * @param withError Error message in case the stop has an unexpected cause. The error
     *                  message will be set in the PlaybackState and will be visible to
     *                  MediaController clients.
     */
    public void handleStopRequest(String withError) {
        Log.d(TAG, "handleStopRequest: mState=" + mPlayback.getState() + " error=" + withError);
        mPlayback.stop(true);
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(withError);
    }


    /**
     * Update the current media player state, optionally showing an error message.
     *
     * @param error if not null, error message to present to the user.
     */
    public void updatePlaybackState(String error) {

        Log.d(TAG, "updatePlaybackState, playback state=" + mPlayback.getState());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        setCustomAction(stateBuilder);
        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        //noinspection ResourceType(state, position, speed, update time)
        stateBuilder.setState(state, position, mPlayback.getPlaybackSpeed(), SystemClock.elapsedRealtime());

        // Set the activeQueueItemId if the current index is valid.
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentPodcast();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
        }
        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            // Log.d(TAG, "DISPLAY NOTIFICATIONNNNNNNNNNNNNNNNNNNNNNNN");
            mServiceCallback.onNotificationRequired();
        }
    }

    private void setCustomAction(PlaybackStateCompat.Builder stateBuilder) {
        Bundle customActionExtras = new Bundle();
        //  WearHelper.setShowCustomActionOnWear(customActionExtras, true);
        stateBuilder.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
                SmallPlaybackControlView.CUSTOM_ACTION_SPEED_CHANGE,
                mResources.getString(R.string.custom_action_speed), -1)
                .setExtras(customActionExtras)
                .build());

      /*  MediaSessionCompat.QueueItem currentPodcast = mQueueManager.getCurrentPodcastID();
        if (currentPodcast == null) {
            return;
        }
        // Set appropriate "Favorite" icon on Custom action:
        String mediaId = currentPodcast.getDescription().getMediaId();
        if (mediaId == null) {
            return;
        }
        String musicId = MediaIDHelper.extractPodcastIDFromMediaID(mediaId);
        int favoriteIcon = mPodcastProvider.isFavorite(musicId) ?
                R.drawable.ic_star_on : R.drawable.ic_star_off;
        Log.d(TAG, "updatePlaybackState, setting Favorite custom action of music "+
                musicId+ " current favorite="+ mPodcastProvider.isFavorite(musicId));

        stateBuilder.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
                CUSTOM_ACTION_THUMBS_UP, mResources.getString(R.string.favorite), favoriteIcon)
                .setExtras(customActionExtras)
                .build());*/
    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_SEEK_TO |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    /**
     * Implementation of the Playback.Callback interface
     */
    /**
     * Implementation of the Playback.Callback interface
     */
    @Override
    public void onCompletion() {
        // The media player finished playing the current song, so we go ahead
        // and start the next.
        if (mQueueManager.skipQueuePosition(1)) {
            handlePlayRequest();
            mQueueManager.updateMetadata();
        } else {
            // If skipping was not possible, we stop and release the resources:
            handleStopRequest(null);
        }
    }

    /**
     * Switch to a different Playback instance, maintaining all playback state, if possible.
     *
     * @param playback switch to this playback
     */
    public void switchToPlayback(Playback playback, boolean resumePlaying) {
        if (playback == null) {
            throw new IllegalArgumentException("Playback cannot be null");
        }
        // suspend the current one.
        int oldState = mPlayback.getState();
        int pos = (int) mPlayback.getCurrentStreamPosition();
        String currentMediaId = mPlayback.getCurrentMediaId();
        mPlayback.stop(false);
        playback.setCallback(this);
        playback.setCurrentStreamPosition(pos < 0 ? 0 : pos);
        playback.setCurrentMediaId(currentMediaId);
        playback.start();
        // finally swap the instance
        mPlayback = playback;
        switch (oldState) {
            case PlaybackStateCompat.STATE_BUFFERING:
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_PAUSED:
                mPlayback.pause();
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                MediaSessionCompat.QueueItem currentPodcast = mQueueManager.getCurrentPodcast();
                if (resumePlaying && currentPodcast != null) {
                    mPlayback.play(currentPodcast);
                } else if (!resumePlaying) {
                    mPlayback.pause();
                } else {
                    mPlayback.stop(true);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            default:
                Log.d(TAG, "Default called. Old state is " + oldState);
        }
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        //  Log.d(TAG, "ON PLAYBACK STATUS CHANGEDDDDDDDDDDDDDDDDDDDDDD");
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        Log.d(TAG, "setCurrentMediaId" + mediaId);
        mQueueManager.setQueueFromPodcast(mediaId);
    }

    @Override
    public String getCurrentPodcastID() {
        return MediaIDHelper.extractPodcastIDFromMediaID(
                mQueueManager.getCurrentPodcast().getDescription().getMediaId());
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            Log.d(TAG, "play");
            if (mQueueManager.getCurrentPodcast() == null) {
                mQueueManager.setRandomQueue();
            }
            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            Log.d(TAG, "OnSkipToQueueItem:" + queueId);
            mQueueManager.setCurrentQueueItem(queueId);
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSeekTo(long position) {
            // LogHelper.d(TAG, "onSeekTo:", position);
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "playFromMediaId mediaId:" + mediaId + "  extras=" + extras);
            mQueueManager.setQueueFromPodcast(mediaId);
            handlePlayRequest();
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
            Bitmap bitmap = extras.getParcelable(DownloadFragment.EXTRA_IMAGE);
            MetadataEntity metadataEntity = (MetadataEntity) extras.getSerializable(DownloadFragment.EXTRA_METADATA_OBJ);
            MediaMetadataCompat mediaMetadataCompat = MetadataHelper.buildMetadataFromEntity(metadataEntity, bitmap);
            mPodcastProvider.createNewPodcastListById(mediaMetadataCompat);
            mQueueManager.setQueueFromPodcast(metadataEntity.getMetadataMediaId(), mediaMetadataCompat);
            handlePlayRequest();
        }

        @Override
        public void onPause() {
            Log.d(TAG, "pause. current state=" + mPlayback.getState());
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            //LogHelper.d(TAG, "stop. current state=" + mPlayback.getState());
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            Log.d(TAG, "skipToNext");
            if (mQueueManager.skipQueuePosition(1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSkipToPrevious() {
            if (mQueueManager.skipQueuePosition(-1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onRewind() {
            mPlayback.rewind();
        }

        @Override
        public void onFastForward() {
            mPlayback.fastForward();
        }


        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {
            if (action.equals(SmallPlaybackControlView.CUSTOM_ACTION_SPEED_CHANGE)) {
                mPlayback.changeSpeed((Float) extras.get(SmallPlaybackControlView.SPEED));
            }
        }

    }


    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }
}
