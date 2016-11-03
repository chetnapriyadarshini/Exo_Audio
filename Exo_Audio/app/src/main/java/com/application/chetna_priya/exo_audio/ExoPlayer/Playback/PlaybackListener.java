package com.application.chetna_priya.exo_audio.ExoPlayer.Playback;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.Model.PodcastProvider;
import com.application.chetna_priya.exo_audio.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;


/**
 * Created by chetna_priya on 10/27/2016.
 */

public class PlaybackListener implements Playback.Callback {

    //TODO handle queue implementation
    private static final String TAG = PlaybackListener.class.getSimpleName();
    Context mContext;
    private PlaybackServiceCallback mServiceCallback;
    Playback mPlayback;
    private MediaSessionCompat.Callback mMediaSessionCallback;

    public PlaybackListener(PlaybackServiceCallback playbackServiceCallback,
                            Context context, PodcastProvider podcastProvider,
                            Playback playback){
        mContext = context;
        mServiceCallback = playbackServiceCallback;
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
        //TODO Implement queue manager functionality
      //  LogHelper.d(TAG, "handlePlayRequest: mState=" + mPlayback.getState());
      //  MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
      //  if (currentMusic != null)
        {
            mServiceCallback.onPlaybackStart();
            mPlayback.play(/*currentMusic*/);
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
        Log.d(TAG, "handleStopRequest: mState=" + mPlayback.getState() + " error="+ withError);
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
     /*   MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
        }
*/
        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired();
        }
    }

    private void setCustomAction(PlaybackStateCompat.Builder stateBuilder) {
/*
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic == null) {
            return;
        }
        // Set appropriate "Favorite" icon on Custom action:
        String mediaId = currentMusic.getDescription().getMediaId();
        if (mediaId == null) {
            return;
        }*/
     //   String musicId = MediaIDHelper.extractMusicIDFromMediaID(mediaId);
      //  int favoriteIcon = mMusicProvider.isFavorite(musicId) ?
         //       R.drawable.ic_star_on : R.drawable.ic_star_off;
    /*    LogHelper.d(TAG, "updatePlaybackState, setting Favorite custom action of music ",
                musicId, " current favorite=", mMusicProvider.isFavorite(musicId));
    */    Bundle customActionExtras = new Bundle();
      //  WearHelper.setShowCustomActionOnWear(customActionExtras, true);
        stateBuilder.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
                AbstractPlaybackControlView.CUSTOM_ACTION_SPEED_CHANGE,
                "speed", -1)
                .setExtras(customActionExtras)
                .build());
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
    @Override
    public void onCompletion() {
        // The media player finished playing the current song, so we go ahead
        // and start the next.
        //TODO handle queue implementation
        /*if (mQueueManager.skipQueuePosition(1)) {
            handlePlayRequest();
            mQueueManager.updateMetadata();
        } else*/
        {
            // If skipping was not possible, we stop and release the resources:
            handleStopRequest(null);
        }
    }


    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            Log.d(TAG, "play");
          /*  if (mQueueManager.getCurrentMusic() == null) {
                mQueueManager.setRandomQueue();
            }*/
            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            /*LogHelper.d(TAG, "OnSkipToQueueItem:" + queueId);
            mQueueManager.setCurrentQueueItem(queueId);
            mQueueManager.updateMetadata();*/
            handleStopRequest("Not for now");
        }

        @Override
        public void onSeekTo(long position) {
           // LogHelper.d(TAG, "onSeekTo:", position);
           mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "playFromMediaId mediaId:"+ mediaId+ "  extras="+ extras);
         //   mQueueManager.setQueueFromMusic(mediaId);
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
           /* LogHelper.d(TAG, "skipToNext");
            if (mQueueManager.skipQueuePosition(1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }*/
         //   mQueueManager.updateMetadata();
            handleStopRequest("Not for now");
        }

        @Override
        public void onSkipToPrevious() {
           /* if (mQueueManager.skipQueuePosition(-1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }*/
        //    mQueueManager.updateMetadata();

            handleStopRequest("Not for now");
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {
            if(action.equals(AbstractPlaybackControlView.CUSTOM_ACTION_SPEED_CHANGE)){
                mPlayback.changeSpeed();
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
