package com.application.chetna_priya.exo_audio.ExoPlayer.Playback;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;


/**
 * Created by chetna_priya on 10/27/2016.
 */

public class PlaybackListener implements ExoPlayer.EventListener, View.OnClickListener {

    private static final String TAG = PlaybackListener.class.getSimpleName();
    Context mContext;
    private PlaybackServiceCallback mServiceCallback;
    Playback mPlayback;

    public PlaybackListener(PlaybackServiceCallback playbackServiceCallback, Context context, Playback playback){
        mContext = context;
        mServiceCallback = playbackServiceCallback;
        mPlayback = playback;

    }
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = mContext.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = mContext.getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = mContext.getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = mContext.getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
    }

    private void showToast(int messageId) {
        showToast(mContext.getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onClick(View view) {

    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return new MediaSessionCallback();
    }

    public void handleStopRequest(Object o) {

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
        }

        @Override
        public void onSeekTo(long position) {
           // LogHelper.d(TAG, "onSeekTo:", position);
         //   mPlayback.seekTo((int) position);
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
            //handlePauseRequest();
        }

        @Override
        public void onStop() {
            //LogHelper.d(TAG, "stop. current state=" + mPlayback.getState());
            //handleStopRequest(null);
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
        }

        @Override
        public void onSkipToPrevious() {
           /* if (mQueueManager.skipQueuePosition(-1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }*/
        //    mQueueManager.updateMetadata();
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {

        }

        /**
         * Handle free and contextual searches.
         * <p/>
         * All voice searches on Android Auto are sent to this method through a connected
         * {@link android.support.v4.media.session.MediaControllerCompat}.
         * <p/>
         * Threads and async handling:
         * Search, as a potentially slow operation, should run in another thread.
         * <p/>
         * Since this method runs on the main thread, most apps with non-trivial metadata
         * should defer the actual search to another thread (for example, by using
         * an {@link AsyncTask} as we do here).
         **/
        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {}
    }

    private void handlePlayRequest() {

    }

    private void updatePlaybackState(String message) {

    }


    public interface PlaybackServiceCallback {
        void onPlaybackStart() throws Exception;

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }
}
