package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.application.chetna_priya.exo_audio.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chetna_priya on 10/23/2016.
 */

public class SmallPlaybackControlView extends AbstractPlaybackControlView {

    private static final String TAG = SmallPlaybackControlView.class.getSimpleName();

    private final SmallPlaybackControlView.ComponentListener componentListener;
    private SimpleExoPlayer player;
    private PlayerImpl playerImpl;
    @BindView(R.id.imgbtn_play_pause) ImageButton playButton;

    public SmallPlaybackControlView(Context context) {
        this(context, null);
    }

    public SmallPlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        componentListener = new SmallPlaybackControlView.ComponentListener();

        View view  = LayoutInflater.from(context).inflate(R.layout.layout_current_audio, this);
        ButterKnife.bind(this,view);

        playButton.setOnClickListener(componentListener);

        playerImpl = new PlayerImpl(context, SmallPlaybackControlView.this);

        updateAll();
    }

    /**
     * Sets the {@link ExoPlayer} to control.
     *
     * @param player the {@code ExoPlayer} to control.
     */
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

    @Override
    public void setPlaybackParamsListener(OnPlaybackParamsListener params) {}

    @Override
    public boolean isPlaying() {
        return player != null && player.getPlayWhenReady();
    }

    @Override
    public void onPause() {
        if(playerImpl != null)
              playerImpl.onPause();
    }

    @Override
    public void onResume() {
        if(playerImpl != null)
             playerImpl.onResume();
    }

    @Override
    public void onDestroy() {
        playerImpl = null;
    }


    private void updateAll() {
        updatePlayPauseButton();
    }


    private void updatePlayPauseButton() {
        boolean playing = player != null && player.getPlayWhenReady();
        Log.d(TAG, "IN UPDATE PLAY PAUSE BUTTON, PLAYER IS PLAYING "+playing+" IS PLAYER NULL "+player);
        String contentDescription = getResources().getString(
                playing ? R.string.exo_controls_pause_description : R.string.exo_controls_play_description);
        playButton.setContentDescription(contentDescription);
        playButton.setImageResource(
                playing ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player == null || event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                player.setPlayWhenReady(!player.getPlayWhenReady());
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                player.setPlayWhenReady(true);
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                player.setPlayWhenReady(false);
                break;
            default:
                return false;
        }
        // show();
        return true;
    }


    final class ComponentListener implements ExoPlayer.EventListener,
             OnClickListener {


        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updatePlayPauseButton();
        }

        @Override
        public void onPositionDiscontinuity() {
            /*
            This view only has a play/pause button so we do nothing here
             */
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            /*
            This view only has a play/pause button so we do nothing here
             */
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
            if (playButton == view) {
                player.setPlayWhenReady(!player.getPlayWhenReady());
            }
            boolean playing =  player.getPlayWhenReady();
            Log.d(TAG, "ON CLICK ON PLAY BUTTON isPlaying "+playing);
        }

    }

}
