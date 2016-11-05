package com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView;

import android.content.ComponentName;
import android.content.Context;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlayerService.PodcastService;
import com.application.chetna_priya.exo_audio.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Util;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A view to control video playback of an {@link ExoPlayer}.
 */
public class CustomPlaybackControlView extends AbstractPlaybackControlView{

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    /*public interface VisibilityListener {
        *//**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         *//*
        void onVisibilityChange(int visibility);
    }*/

    private static final int PROGRESS_BAR_MAX = 1000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private static final String TAG = CustomPlaybackControlView.class.getSimpleName();

    private final ComponentListener componentListener;
    private final MediaBrowserCompat mMediaBrowser;

    @BindView(R.id.tv_time) TextView time;
    @BindView(R.id.tv_time_current) TextView timeCurrent;
    @BindView(R.id.tv_speed) TextView speed;
    @BindView(R.id.btn_play) ImageButton playButton;
    @BindView(R.id.btn_prev) View previousButton;
    @BindView(R.id.btn_next) View nextButton;
    @BindView(R.id.btn_rew) View rewindButton;
    @BindView(R.id.btn_ffwd) View fastForwardButton;
    @BindView(R.id.seek_mediacontroller_progress) SeekBar progressBar;


    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Timeline.Window currentWindow;
    private final boolean speedViewVisible;
    private final float[] SPEED_ARR = {0.50f,0.60f,0.70f,0.80f,0.90f,1.00f,1.10f,1.20f,1.30f,1.40f,1.50f,
                                        1.60f,1.70f,1.80f,1.90f,2.00f};
    private final int STANDARD_INDEX = 5;//index of 1.00f
    private int CURRENT_INDEX = STANDARD_INDEX;

   // private SimpleExoPlayer player;
    //private VisibilityListener visibilityListener;
   // private OnPlaybackParamsListener paramsListener;


    private boolean dragging;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };
    private Context mContext;
    private SimpleExoPlayer player;

    public CustomPlaybackControlView(Context context) {
        this(context, null);
    }

    public CustomPlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        currentWindow = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();

        View view  = LayoutInflater.from(context).inflate(R.layout.custom_exo_playback_control_view, this);
        ButterKnife.bind(this, view);
        if(Util.SDK_INT < 23){
            speedViewVisible = false;
            speed.setVisibility(View.GONE);
        }else {
            CURRENT_INDEX = STANDARD_INDEX;
            updateSpeedText();
            speed.setOnClickListener(componentListener);
            speedViewVisible = true;
        }

        playButton.setOnClickListener(componentListener);
        previousButton.setOnClickListener(componentListener);
        nextButton.setOnClickListener(componentListener);
        rewindButton.setOnClickListener(componentListener);
        fastForwardButton.setOnClickListener(componentListener);
        progressBar.setOnSeekBarChangeListener(componentListener);
        progressBar.setMax(PROGRESS_BAR_MAX);

       //   stateClass = new StateClass();

        mMediaBrowser = new MediaBrowserCompat(mContext,
                new ComponentName(mContext, PodcastService.class), mConnectionCallback, null);
        activityCallbacks.setMediaBrowser(mMediaBrowser);
        updateAll();
    }


    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                // updateMediaDescription(metadata.getDescription());
                // updateDuration(metadata);
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            switch (event){
                case EVENT_POSITION_DISCONTINUITY:
                    updateNavigation();
                    updateProgress();
                    break;
                case EVENT_SPEED_CHANGE:
                    updateNavigation();
                    updateProgress();
                    break;
                case EVENT_TIME_LINE_CHANGED:
                    updateNavigation();
                    updateProgress();
                    break;
                case EVENT_PLAYER_CHANGED:
                    updatePlayPauseButton();
                    updateProgress();
                    break;
            }
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        Log.e(TAG, e+ "could not connect media controller");
                    }
                }
            };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                mContext, token);
        if (mediaController.getMetadata() == null) {
            activityCallbacks.finishActivity();
            return;
        }
        activityCallbacks.setSupportMediaControllerForActivity(mediaController);
        mediaController.registerCallback(mCallback);
        updateProgress();
        PlaybackStateCompat state = mediaController.getPlaybackState();
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            //   updateMediaDescription(metadata.getDescription());
            // updateDuration(metadata);
        }/*
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }*/
    }

    /**
     * Sets the {@link ExoPlayer} to control.
     *
     * @param player the {@code ExoPlayer} to control.
     */
    @Override
    public void setPlayer(SimpleExoPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        updateAll();
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateProgress();
    }

    private void updatePlayPauseButton() {
       /* if (!isVisible()) {
            return;
        }*/
        boolean playing = player != null && player.getPlayWhenReady();
        Log.d(TAG, "IN UPDATE PLAY PAUSE BUTTON, PLAYER IS PLAYING "+playing+" IS PLAYER NULL "+player);
        String contentDescription = getResources().getString(
                playing ? R.string.exo_controls_pause_description : R.string.exo_controls_play_description);
        playButton.setContentDescription(contentDescription);
        playButton.setImageResource(
                playing ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play);

    }


    private void updateNavigation() {
      /*  if (!isVisible()) {
            return;
        }*/

        MediaControllerCompat.TransportControls controls =
                ((FragmentActivity)mContext).getSupportMediaController().getTransportControls();
        Timeline currentTimeline = player != null ? player.getCurrentTimeline() : null;
        boolean haveTimeline = currentTimeline != null;
        boolean isSeekable = false;
        boolean enablePrevious = false;
        boolean enableNext = false;
        if (haveTimeline) {
            int currentWindowIndex = player.getCurrentWindowIndex();
            currentTimeline.getWindow(currentWindowIndex, currentWindow);
            isSeekable = currentWindow.isSeekable;
            enablePrevious = currentWindowIndex > 0 || isSeekable || !currentWindow.isDynamic;
            enableNext = (currentWindowIndex < currentTimeline.getWindowCount() - 1)
                    || currentWindow.isDynamic;
        }
        setButtonEnabled(enablePrevious , previousButton);
        setButtonEnabled(enableNext, nextButton);
        setButtonEnabled(isSeekable, fastForwardButton);
        setButtonEnabled(isSeekable, rewindButton);
        if(speedViewVisible)
            setButtonEnabled(isSeekable,speed);
    //    Log.d(TAG, "Previous Button "+enablePrevious);
     //   Log.d(TAG, "Next Button "+enablePrevious);
      //  Log.d(TAG, "Forward Button "+enablePrevious);
       // Log.d(TAG, "Rewind Button "+enablePrevious);
        progressBar.setEnabled(isSeekable);
    }

    private void updateProgress() {
       /* if (!isVisible()) {
            return;
        }*/
        long duration = player == null ? 0 : player.getDuration();
        long position = player == null ? 0 : player.getCurrentPosition();
        time.setText(stringForTime(duration));
        if (!dragging) {
            timeCurrent.setText(stringForTime(position));
        }
        if (!dragging) {
            progressBar.setProgress(progressBarValue(position));
        }
        long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
        progressBar.setSecondaryProgress(progressBarValue(bufferedPosition));
        // Remove scheduled updates.
        removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        view.setEnabled(enabled);
        if (Util.SDK_INT >= 11) {
            view.setAlpha(enabled ? 1f : 0.3f);
            view.setVisibility(VISIBLE);
        } else {
            view.setVisibility(enabled ? VISIBLE : INVISIBLE);
        }
    }

    private String stringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    private int progressBarValue(long position) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET || duration == 0 ? 0
                : (int) ((position * PROGRESS_BAR_MAX) / duration);
    }

    private long positionValue(int progress) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET ? 0 : ((duration * progress) / PROGRESS_BAR_MAX);
    }

    private void previous() {
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline == null) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        currentTimeline.getWindow(currentWindowIndex, currentWindow);
        if (currentWindowIndex > 0 && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
                || (currentWindow.isDynamic && !currentWindow.isSeekable))) {
            player.seekToDefaultPosition(currentWindowIndex - 1);
        } else {
            player.seekTo(0);

        }
    }

    private void next() {
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline == null) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex < currentTimeline.getWindowCount() - 1) {
            player.seekToDefaultPosition(currentWindowIndex + 1);
        } else if (currentTimeline.getWindow(currentWindowIndex, currentWindow, false).isDynamic) {
            player.seekToDefaultPosition();
        }
    }

    private void updateSpeedText() {
        speed.setText(SPEED_ARR[CURRENT_INDEX]+"x");
    }
   /* @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player == null || event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                fastForward();
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                rewind();
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if(hasAudioFocus(player))
                    player.setPlayWhenReady(!player.getPlayWhenReady());
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if(hasAudioFocus(player))
                    player.setPlayWhenReady(true);
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if(hasAudioFocus(player))
                    player.setPlayWhenReady(false);
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                next();
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                previous();
                break;
            default:
                return false;
        }
       // show();
        return true;
    }
*/

    final class ComponentListener implements  SeekBar.OnSeekBarChangeListener, OnClickListener,
            ExoPlayer.EventListener{

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            dragging = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                timeCurrent.setText(stringForTime(positionValue(progress)));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;

            MediaControllerCompat.TransportControls controls =
                    ((FragmentActivity)mContext).getSupportMediaController().getTransportControls();
            controls.seekTo(positionValue(seekBar.getProgress()));
           // player.seekTo(positionValue(seekBar.getProgress()));
            //hideDeferred();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onPositionDiscontinuity() {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }
        @Override
        public void onClick(View view) {
            Timeline currentTimeline = null;
            MediaControllerCompat.TransportControls controls =
                    ((FragmentActivity)mContext).getSupportMediaController().getTransportControls();
            if(player != null)
            currentTimeline = player.getCurrentTimeline();
            if (nextButton == view) {
                controls.skipToNext();
               // next();
            } else if (previousButton == view) {
                controls.skipToPrevious();
              //  previous();
            } else if (fastForwardButton == view) {
                controls.fastForward();
             //   fastForward();
            } else if (rewindButton == view && currentTimeline != null) {
                controls.rewind();
               // rewind();
            } else if (playButton == view) {
                PlaybackStateCompat state = ((FragmentActivity)mContext).getSupportMediaController().getPlaybackState();
                if(state.getState() == PlaybackStateCompat.STATE_PAUSED)
                    controls.play();
                else
                    controls.pause();

            }else if(speed == view){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(CURRENT_INDEX+1 == SPEED_ARR.length)
                        CURRENT_INDEX = 0;
                    else
                        CURRENT_INDEX++;
                    updateSpeedText();
                    Bundle speedBundle = new Bundle();
                    speedBundle.putFloat(SPEED, SPEED_ARR[CURRENT_INDEX]);
                    controls.sendCustomAction(CUSTOM_ACTION_SPEED_CHANGE, null);
                  }
            }
            //hideDeferred();
        }

    }

}
