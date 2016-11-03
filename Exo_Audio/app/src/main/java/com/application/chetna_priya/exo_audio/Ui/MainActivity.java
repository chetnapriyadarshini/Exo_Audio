package com.application.chetna_priya.exo_audio.Ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlayerService.PodcastService;
import com.application.chetna_priya.exo_audio.ExoPlayer.Playlist;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.SmallPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

public class MainActivity extends AppCompatActivity implements AbstractPlaybackControlView.ActivityCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SmallPlaybackControlView playbackControlView;
    private PodcastService mService;
    private boolean mBound;
    private MediaBrowserCompat mMediaBrowser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchBtn = (Button) findViewById(R.id.btn_launch_audio);
        launchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AudioActivity.class);
                startActivity(intent);
            }
        });
        Button initQueue = (Button) findViewById(R.id.btn_initQueue);
        initQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Playlist.getPlaylistInstance().addAlbumToList();
            }
        });
        playbackControlView = (SmallPlaybackControlView) findViewById(R.id.current_audio_view);

    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PodcastService.LocalBinder binder = (PodcastService.LocalBinder) service;
            mService = binder.getService();
            mService.setViewForPlayer(playbackControlView);
            mBound = true;
            mMediaBrowser.connect();
            /* move mMediaBrowser.connect here because
            we want the view class to have the player reference before we start
            avoiding the null pointer exception*/
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, PodcastService.class);
        if(PodcastService.isServiceRunning)
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaBrowser.disconnect();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void setSupportMediaControllerForActivity(MediaControllerCompat mediaController) {
        setSupportMediaController(mediaController);
    }

    @Override
    public void setMediaBrowser(MediaBrowserCompat mediaBrowser) {
        mMediaBrowser = mediaBrowser;
    }
}
