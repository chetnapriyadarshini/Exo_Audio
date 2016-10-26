package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.PlaybackParams;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by chetna_priya on 10/23/2016.
 */

public abstract class AbstractPlaybackControlView extends FrameLayout implements AudioManager.OnAudioFocusChangeListener {


    public interface OnPlaybackParamsListener {
        void setPlaybackParams(PlaybackParams playbackParams);
    }

    public AbstractPlaybackControlView(Context context) {
        super(context);
    }

    public AbstractPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AbstractPlaybackControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setPlayer(SimpleExoPlayer exoPlayer);
    public abstract void setPlaybackParamsListener(OnPlaybackParamsListener params);
    public abstract boolean isPlaying();
    public abstract void onPause();
    public abstract void onResume();
    public abstract void onDestroy();
}
