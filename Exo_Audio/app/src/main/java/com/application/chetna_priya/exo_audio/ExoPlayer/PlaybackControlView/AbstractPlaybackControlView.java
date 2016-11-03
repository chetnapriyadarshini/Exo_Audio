package com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by chetna_priya on 10/23/2016.
 */

public abstract class AbstractPlaybackControlView extends FrameLayout /*implements AudioManager.OnAudioFocusChangeListener */{


    private Context mContext;
    ActivityCallbacks activityCallbacks;
    public static final String EVENT_POSITION_DISCONTINUITY = "event_position_discontinuity";
    public static final String EVENT_SPEED_CHANGE = "event_speed_change";
    public static final String EVENT_TIME_LINE_CHANGED = "event_timeline_changed";
    public static final String EVENT_PLAYER_CHANGED = "event_player_changed";
    public static final String CUSTOM_ACTION_SPEED_CHANGE = "custom_action_speed_changed";

    public interface ActivityCallbacks {
        void finishActivity();
        void setSupportMediaControllerForActivity(MediaControllerCompat mediaController);
        void setMediaBrowser(MediaBrowserCompat mediaBrowser);
    }


    public AbstractPlaybackControlView(Context context) {
        super(context);
    }

    public AbstractPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        activityCallbacks = (ActivityCallbacks) mContext;/*
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);*/
    }


    public AbstractPlaybackControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
/*
    protected boolean needCheckAudioFocus(SimpleExoPlayer exoPlayer){
        if(exoPlayer.getPlayWhenReady())//Player is in playing state and needs to pause we do not need to check for audio focus
            return false;
        //We do not need to check for focus as we already have a media source playing
        //meanign focus has been requested already
        if(!playerNeedsMediaSourceAndFocus(exoPlayer))
            return false;

        return true;
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            audioManager.abandonAudioFocus(this);
            // Stop playback
        }
    }

    protected boolean hasAudioFocus(SimpleExoPlayer exoPlayer) {
        //TODO think of a better way to do this dumbasssss
        if(!needCheckAudioFocus(exoPlayer))
            return true;
        int result = audioManager.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
          *//*  MediaSessionCompat mSession = new MediaSessionCompat(mContext, mContext.getPackageName());
            Intent intent = new Intent(mContext, RemoteControlReceiver.class);
            PendingIntent pintent = PendingIntent.getBroadcast(mContext, AUDIO_FOCUS_REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mSession.setMediaButtonReceiver(pintent);
            mSession.setCallback(this);
            mSession.setActive(true);*//*
            return true;
        }
        return false;
    }*/
/*
    public boolean playerNeedsMediaSourceAndFocus(SimpleExoPlayer exoPlayer) {
        *//* If the player has finished playing a media and there is another media in the
         * playlist waiting to be played *//*
        if(exoPlayer.getPlaybackState() == ExoPlayer.STATE_ENDED &&
                !Playlist.getPlaylistInstance().isPlaylistEmpty())
            return true;
        *//* If the player does not have any media source set up and, is initialized and ready for its
         * first media source *//*
        if(exoPlayer.getCurrentTimeline() == null)
            return true;
        return false;
    }*/

    public abstract void setPlayer(SimpleExoPlayer exoPlayer);
   // public abstract void setPlaybackParamsListener(OnPlaybackParamsListener params);
   // public abstract boolean isPlaying();
  //  public abstract void onPause();
  //  public abstract void onResume();
  //  public abstract void onDestroy();
}
