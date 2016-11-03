package com.application.chetna_priya.exo_audio.ExoPlayer.Playback;

/**
 * Created by chetna_priya on 11/1/2016.
 */

public interface Playback  {

    void play();

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
}
