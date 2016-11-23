package com.application.chetna_priya.exo_audio.Ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.media.MediaBrowserService;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlayerService.PodcastService;
import com.application.chetna_priya.exo_audio.ExoPlayer.Playlist;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.SmallPlaybackControlView;
import com.application.chetna_priya.exo_audio.Model.RemoteJsonSource;
import com.application.chetna_priya.exo_audio.R;

public class MainActivity extends AppCompatActivity implements AbstractPlaybackControlView.ActivityCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int PODCAST_LOADER = 1;
    private MediaBrowserCompat mMediaBrowser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(PODCAST_LOADER, null, RemoteJsonSource.getInstance()).forceLoad();
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mMediaBrowser!= null && !mMediaBrowser.isConnected())
            mMediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mMediaBrowser!= null && mMediaBrowser.isConnected())
            mMediaBrowser.disconnect();
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
