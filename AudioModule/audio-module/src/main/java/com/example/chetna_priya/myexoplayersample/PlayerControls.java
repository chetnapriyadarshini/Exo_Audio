package com.example.chetna_priya.myexoplayersample;

import android.widget.MediaController;

import com.google.android.exoplayer.ExoPlayer;

/**
 * Created by chetna_priya on 10/12/2016.
 */

public class PlayerControls implements MediaController.MediaPlayerControl {

    private final ExoPlayer exoPlayer;

    public PlayerControls(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }
    @Override
    public int getCurrentPosition() {
        return exoPlayer.getDuration() == ExoPlayer.UNKNOWN_TIME ? 0
                : (int) exoPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return exoPlayer.getDuration() == ExoPlayer.UNKNOWN_TIME ? 0
                : (int) exoPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return exoPlayer.getPlayWhenReady();
    }

    @Override
    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void seekTo(int timeMillis) {
        long seekPosition = exoPlayer.getDuration() == ExoPlayer.UNKNOWN_TIME ? 0
                : Math.min(Math.max(0, timeMillis), getDuration());
        exoPlayer.seekTo(seekPosition);
    }

    public void forward(int timeMillis){
        long forwardPos = exoPlayer.getCurrentPosition()+timeMillis;
        if(forwardPos >= exoPlayer.getDuration())
            return;
        exoPlayer.seekTo(forwardPos);
    }


    public void backward(int timeMillis){
        long backwardpos = exoPlayer.getCurrentPosition()-timeMillis;
        if(backwardpos <= 0l)
            return;
        exoPlayer.seekTo(backwardpos);
    }

    @Override
    public int getBufferPercentage() {
        return exoPlayer.getBufferedPercentage();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    /**
     * This is an unsupported operation.
     * <p>
     * Application of audio effects is dependent on the audio renderer used. When using
     * {@link com.google.android.exoplayer.MediaCodecAudioTrackRenderer}, the recommended approach is
     * to extend the class and override
     * {@link com.google.android.exoplayer.MediaCodecAudioTrackRenderer#onAudioSessionId}.
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public int getAudioSessionId() {
        throw new UnsupportedOperationException();
    }
}
