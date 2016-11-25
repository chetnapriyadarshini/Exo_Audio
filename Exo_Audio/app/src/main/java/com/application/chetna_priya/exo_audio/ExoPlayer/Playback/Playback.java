package com.application.chetna_priya.exo_audio.ExoPlayer.Playback;

import android.support.v4.media.session.MediaSessionCompat;

/**
 * Created by chetna_priya on 11/1/2016.
 */

public interface Playback  {

    void play(MediaSessionCompat.QueueItem item );

    /**
     * Pause the current playing item
     */
    void pause();

    /**
     * Set the latest playback state as determined by the caller.
     */
    void setState(int state);

    /**
     * Get the current {@link android.media.session.PlaybackState#getState()}
     */
    int getState();

    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    void stop(boolean notifyListeners);

    void rewind();

    void fastForward();

    boolean isPlaying();

    boolean isConnected();

    long getCurrentStreamPosition();

    float getPlaybackSpeed();

    void seekTo(long position);

    void changeSpeed(float speed);

    void updateLastKnownStreamPosition();

    void setCurrentStreamPosition(int pos);

    /**
     * Set the current mediaId. This is only used when switching from one
     * playback to another.
     *
     * @param mediaId to be set as the current.
     */
    void setCurrentMediaId(String mediaId);

    /**
     *
     * @return the current media Id being processed in any state or null.
     */
    String getCurrentMediaId();

    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();
        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         */
        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);
}
