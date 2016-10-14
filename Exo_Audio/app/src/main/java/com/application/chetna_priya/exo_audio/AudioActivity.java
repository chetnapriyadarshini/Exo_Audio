package com.application.chetna_priya.exo_audio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class AudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        SimpleExoPlayerView exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        Player player = new Player(this, exoPlayerView);
        player.preparePlayer();
    }
}
