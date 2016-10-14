package com.app.chetna_priya.audiomodule.Exoplayer;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.chetna_priya.audiomodule.R;
import com.google.android.exoplayer.ExoPlayer;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by chetna_priya on 10/13/2016.
 */

public class PlaybackControlView extends FrameLayout {

    public interface VisibilityListener {
        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility.
         */
        void onVisibilityChange(int visibility);
    }


    public static final int DEFAULT_REWIND_MS = 5000;
    public static final int DEFAULT_SHOW_DURATION_MS = 5000;

    private static final int PROGRESS_BAR_MAX = 1000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

    private final ComponentListener componentListener;
    private final View previousButton;
    private final View nextButton;
    private final ImageButton playButton;
    private final TextView time;
    private final TextView timeCurrent;
    private final AppCompatSeekBar progressBar;
    private final View fastForwardButton;
    private final View rewindButton;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
  //  private final Timeline.Window currentWindow;

    private VisibilityListener visibilityListener;

    private ExoPlayer player;

    private boolean dragging;
    private int rewindMs = DEFAULT_REWIND_MS;
   // private int fastForwardMs = DEFAULT_FAST_FORWARD_MS;
    private int showDurationMs = DEFAULT_SHOW_DURATION_MS;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public PlaybackControlView(Context context) {
        super(context);
    }

    public PlaybackControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    //    currentWindow = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();

        LayoutInflater.from(context).inflate(R.layout.playback_control_view, this);
        time = (TextView) findViewById(R.id.time);
        timeCurrent = (TextView) findViewById(R.id.time_current);
        progressBar = (AppCompatSeekBar) findViewById(R.id.seek_audio);
        progressBar.setOnSeekBarChangeListener(componentListener);
        progressBar.setMax(PROGRESS_BAR_MAX);
        playButton = (ImageButton) findViewById(R.id.play_audio);
        playButton.setOnClickListener(componentListener);
        previousButton = findViewById(R.id.prev_audio);
        previousButton.setOnClickListener(componentListener);
        nextButton = findViewById(R.id.next_audio);
        nextButton.setOnClickListener(componentListener);
        rewindButton = findViewById(R.id.rewind_audio);
        rewindButton.setOnClickListener(componentListener);
        updateAll();
        /*
        fastForwardButton = findViewById(R.id.ffwd);
        fastForwardButton.setOnClickListener(componentListener);*/
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


    private void updateProgress() {
    }


    private void hide() {
    }

    private void updateAll() {

    }

    private class ComponentListener implements SeekBar.OnSeekBarChangeListener, OnClickListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onClick(View view) {

        }

    }
}
