package com.application.chetna_priya.exo_audio;

import android.content.Context;
import android.media.PlaybackParams;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Util;
import java.util.Formatter;
import java.util.Locale;

/**
 * A view to control video playback of an {@link ExoPlayer}.
 */
public class CustomPlaybackControlView extends FrameLayout {

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

    public static final int DEFAULT_FAST_FORWARD_MS = 30000;
    public static final int DEFAULT_REWIND_MS = 30000;
  //  public static final int DEFAULT_SHOW_DURATION_MS = 5000;

    private static final int PROGRESS_BAR_MAX = 1000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private static final String TAG = CustomPlaybackControlView.class.getSimpleName();

    private final ComponentListener componentListener;
    private final View previousButton;
    private final View nextButton;
    private final ImageButton playButton;
    private final TextView time;
    private final TextView timeCurrent;
    private final TextView speed;
    private final SeekBar progressBar;
    private final View fastForwardButton;
    private final View rewindButton;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Timeline.Window currentWindow;
    private final boolean speedViewVisible;
    private final float[] SPEED_ARR = {0.50f,0.60f,0.70f,0.80f,0.90f,1.00f,1.10f,1.20f,1.30f,1.40f,1.50f,
                                        1.60f,1.70f,1.80f,1.90f,2.00f};
    private final int STANDARD_INDEX = 5;//index of 1.00f
    private int CURRENT_INDEX = STANDARD_INDEX;

    private ExoPlayer player;
    //private VisibilityListener visibilityListener;
    private OnPlaybackParamsListener paramsListener;

    private boolean dragging;
    private int rewindMs = DEFAULT_REWIND_MS;
    private int fastForwardMs = DEFAULT_FAST_FORWARD_MS;
  //  private int showDurationMs = DEFAULT_SHOW_DURATION_MS;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    public CustomPlaybackControlView(Context context) {
        this(context, null);
    }

    public CustomPlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        currentWindow = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();

        LayoutInflater.from(context).inflate(R.layout.custom_exo_playback_control_view, this);
        time = (TextView) findViewById(R.id.tv_time);
        timeCurrent = (TextView) findViewById(R.id.tv_time_current);
        speed = (TextView) findViewById(R.id.tv_speed);
        if(Util.SDK_INT < 23){
            speedViewVisible = false;
           // speedArr = context.getResources().getStringArray(R.array.speed_arr);
            speed.setVisibility(View.GONE);
        }else {
            //speedArr = null;
            CURRENT_INDEX = STANDARD_INDEX;
            updateSpeedText();
            speed.setOnClickListener(componentListener);
            speedViewVisible = true;
        }

        progressBar = (SeekBar) findViewById(R.id.seek_mediacontroller_progress);
        progressBar.setOnSeekBarChangeListener(componentListener);
        progressBar.setMax(PROGRESS_BAR_MAX);
        playButton = (ImageButton) findViewById(R.id.btn_play);
        playButton.setOnClickListener(componentListener);
        previousButton = findViewById(R.id.btn_prev);
        previousButton.setOnClickListener(componentListener);
        nextButton = findViewById(R.id.btn_next);
        nextButton.setOnClickListener(componentListener);
        rewindButton = findViewById(R.id.btn_rew);
        rewindButton.setOnClickListener(componentListener);
        fastForwardButton = findViewById(R.id.btn_ffwd);
        fastForwardButton.setOnClickListener(componentListener);
        updateAll();
    }

    /**
     * Returns the player currently being controlled by this view, or null if no player is set.
     */
    public ExoPlayer getPlayer() {
        return player;
    }

    /**
     * Sets the {@link ExoPlayer} to control.
     *
     * @param player the {@code ExoPlayer} to control.
     */
    public void setPlayer(ExoPlayer player) {
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

    /**
     * Sets the {@link VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    /*public void setVisibilityListener(VisibilityListener listener) {
        this.visibilityListener = listener;
    }
*/
    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds.
     */
    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
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
        Log.d(TAG, "Previous Button "+enablePrevious);
        Log.d(TAG, "Next Button "+enablePrevious);
        Log.d(TAG, "Forward Button "+enablePrevious);
        Log.d(TAG, "Rewind Button "+enablePrevious);
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

    private void rewind() {
        player.seekTo(Math.max(player.getCurrentPosition() - rewindMs, 0));
    }

    private void fastForward() {
        player.seekTo(Math.min(player.getCurrentPosition() + fastForwardMs, player.getDuration()));
    }


    private void updateSpeedText() {
        speed.setText(SPEED_ARR[CURRENT_INDEX]+"x");
    }

    @Override
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
                player.setPlayWhenReady(!player.getPlayWhenReady());
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                player.setPlayWhenReady(true);
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
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

    public void setPlaybackParamsListener(OnPlaybackParamsListener params){
        this.paramsListener = params;

    }

    public interface OnPlaybackParamsListener {
        void setPlaybackParams(PlaybackParams playbackParams);
    }

    final class ComponentListener implements ExoPlayer.EventListener,
            SeekBar.OnSeekBarChangeListener, OnClickListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
         //   removeCallbacks(hideAction);
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
            player.seekTo(positionValue(seekBar.getProgress()));
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
            Timeline currentTimeline = player.getCurrentTimeline();
            if (nextButton == view) {
                next();
            } else if (previousButton == view) {
                previous();
            } else if (fastForwardButton == view) {
                fastForward();
            } else if (rewindButton == view && currentTimeline != null) {
                rewind();
            } else if (playButton == view) {
                player.setPlayWhenReady(!player.getPlayWhenReady());
            }else if(speed == view){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PlaybackParams playbackParams = new PlaybackParams();
                    if(CURRENT_INDEX+1 == SPEED_ARR.length)
                        CURRENT_INDEX = 0;
                    else
                        CURRENT_INDEX++;
                    updateSpeedText();
                    playbackParams.setSpeed(SPEED_ARR[CURRENT_INDEX]);
                    if(paramsListener != null)
                        paramsListener.setPlaybackParams(playbackParams);
                }
            }
            //hideDeferred();
        }

    }

}
