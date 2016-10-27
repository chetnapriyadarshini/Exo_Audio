package com.application.chetna_priya.exo_audio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.application.chetna_priya.exo_audio.ExoPlayer.Playlist;
import com.application.chetna_priya.exo_audio.PlaybackControlView.SmallPlaybackControlView;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SmallPlaybackControlView playbackControlView;


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

    @Override
    protected void onResume() {
        super.onResume();
        playbackControlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playbackControlView.onPause();
    }

}
