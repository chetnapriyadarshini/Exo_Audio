package com.example.chetna_priya.audiolib;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String ACTION_PLAY = "PLAY";
    private static String mUrl;
    private int mBufferPosition;
    private static AudioService mInstance = null;

    private MediaPlayer mMediaPlayer = null;    // The Media Player*

    // indicates the state our service:
    enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped, // media player is stopped and not prepared to play
        Preparing, // media player is preparing...
        Playing, // playback active (media player ready!). (but the media player may actually be
        // paused in this state if we don't have audio focus. But we stay in this state
        // so that we know we have to resume playback once we get focus back)
        Paused
        // playback paused (media player ready!)
    };
    State mState = State.Retrieving;

    public AudioService(String url) {
        mUrl = url;
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mUrl);
        } catch (IllegalArgumentException e) {
            // ...
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // ...
            e.printStackTrace();
        } catch (IOException e) {
            // ...
            e.printStackTrace();
        }

        try {
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        } catch (IllegalStateException e) {
            // ...
        }
        mState = State.Preparing;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer = new MediaPlayer(); // initialize it here
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initMediaPlayer();
        }
        return START_STICKY;
    }

    public void restartAudio() {
        mMediaPlayer.seekTo(0);
        // Restart music
        startMusic();
    }

    protected void setBufferPosition(int progress) {
        mBufferPosition = progress;
    }

    /** Called when MediaPlayer is ready */
    @Override
    public void onPrepared(MediaPlayer player) {
        // Begin playing music
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mState = State.Retrieving;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void pauseMusic() {
        if (mState.equals(State.Playing)) {
            mMediaPlayer.pause();
            mState = State.Paused;
        }
    }

    public void startMusic() {
        if (!mState.equals(State.Preparing) &&!mState.equals(State.Retrieving)) {
            mMediaPlayer.start();
            mState = State.Playing;
        }
    }

    public boolean isPlaying() {
        if (mState.equals(State.Playing)) {
            return true;
        }
        return false;
    }

    public int getAudioDuration() {
        // Return current music duration
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        // Return current position
        return mMediaPlayer.getCurrentPosition();
    }

    public int getBufferPercentage() {
        return mBufferPosition;
    }

    public void seekAudioTo(int pos) {
        // Seek music to pos
        mMediaPlayer.seekTo(pos);
    }

    public static AudioService getInstance() {
        return mInstance;
    }

    public static void setAudio(String url) {
        mUrl = url;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPosition(percent * getAudioDuration() / 100);
    }

}
