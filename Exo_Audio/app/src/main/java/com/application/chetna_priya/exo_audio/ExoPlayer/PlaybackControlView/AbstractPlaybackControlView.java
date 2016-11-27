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


    //private Context mContext;
    ActivityCallbacks activityCallbacks;
    public static final String EVENT_POSITION_DISCONTINUITY = "event_position_discontinuity";
    public static final String EVENT_SPEED_CHANGE = "event_speed_change";
    public static final String EVENT_TIME_LINE_CHANGED = "event_timeline_changed";
    public static final String EVENT_PLAYER_CHANGED = "event_player_changed";
    public static final String CUSTOM_ACTION_SPEED_CHANGE = "custom_action_speed_changed";
    public static final String SPEED = "speed";

    public abstract void disconnectSession();

    public interface ActivityCallbacks {
        void finishActivity();
        void setSupportMediaControllerForActivity(MediaControllerCompat mediaController);
        //void setMediaBrowser(MediaBrowserCompat mediaBrowser);
    }


    public AbstractPlaybackControlView(Context context) {
        super(context);activityCallbacks = (ActivityCallbacks) context;
    }

    public AbstractPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activityCallbacks = (ActivityCallbacks) context;
    }


    public AbstractPlaybackControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activityCallbacks = (ActivityCallbacks) context;
    }
}
