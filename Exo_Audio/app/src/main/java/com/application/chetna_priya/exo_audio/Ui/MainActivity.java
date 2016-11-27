package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.application.chetna_priya.exo_audio.ExoPlayer.Playlist;
import com.application.chetna_priya.exo_audio.Ui.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.Ui.PlaybackControlView.SmallPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

public class MainActivity extends AppCompatActivity implements AbstractPlaybackControlView.ActivityCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SmallPlaybackControlView mPlaybackControlView;


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
        mPlaybackControlView = (SmallPlaybackControlView) findViewById(R.id.current_audio_view);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mPlaybackControlView.disconnectSession();
    }

    @Override
    public void finishActivity() {
        Log.d(TAG, "FINISHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "@@@@@@@@@  ACTIVITY ON DESTROY CALLLLLEDDDDDDDDDDDDD @@@@@@@@@@@");
    }

    @Override
    public void setSupportMediaControllerForActivity(MediaControllerCompat mediaController) {
        setSupportMediaController(mediaController);
    }
}
