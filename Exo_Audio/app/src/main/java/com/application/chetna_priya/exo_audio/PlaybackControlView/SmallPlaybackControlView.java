package com.application.chetna_priya.exo_audio.PlaybackControlView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlayerService.PodcastService;
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
    private final MediaBrowserCompat mMediaBrowser;
    private SimpleExoPlayer player;
    @BindView(R.id.imgbtn_play_pause) ImageButton playButton;
    private Context mContext;

    public SmallPlaybackControlView(Context context) {
        this(context, null);
    }

    public SmallPlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        componentListener = new SmallPlaybackControlView.ComponentListener();

        View view  = LayoutInflater.from(context).inflate(R.layout.layout_current_audio, this);
        ButterKnife.bind(this,view);

        playButton.setOnClickListener(componentListener);

        mMediaBrowser = new MediaBrowserCompat(mContext,
                new ComponentName(mContext, PodcastService.class), mConnectionCallback, null);
        activityCallbacks.setMediaBrowser(mMediaBrowser);
/*
        playerImpl = new PlayerImpl(context, SmallPlaybackControlView.this);*/

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
        this.player = player;
        updatePlayPauseButton();
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

    /*@Override
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
    }*/

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
               // updateMediaDescription(metadata.getDescription());
               // updateDuration(metadata);
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            updatePlayPauseButton();
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            switch (event){
                case EVENT_PLAYER_CHANGED:
                    updatePlayPauseButton();
                    break;
            }
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        Log.e(TAG, e+ "could not connect media controller");
                    }
                }
            };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                mContext, token);
        if (mediaController.getMetadata() == null) {
            activityCallbacks.finishActivity();
            return;
        }
        activityCallbacks.setSupportMediaControllerForActivity(mediaController);
        mediaController.registerCallback(mCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
         //   updateMediaDescription(metadata.getDescription());
           // updateDuration(metadata);
        }/*
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }*/
    }


    final class ComponentListener implements OnClickListener {

        @Override
        public void onClick(View view) {
         /*   if (playButton == view) {
                player.setPlayWhenReady(!player.getPlayWhenReady());
            }
            boolean playing =  player.getPlayWhenReady();
            Log.d(TAG, "ON CLICK ON PLAY BUTTON isPlaying "+playing);*/
            MediaControllerCompat.TransportControls controls =
                    ((FragmentActivity)mContext).getSupportMediaController().getTransportControls();
            boolean playing = player != null && player.getPlayWhenReady();
            if(playing)
                controls.pause();
            else
                controls.play();
        }

    }

}
