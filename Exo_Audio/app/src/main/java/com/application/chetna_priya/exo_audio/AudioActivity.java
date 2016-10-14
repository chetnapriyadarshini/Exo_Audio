package com.application.chetna_priya.exo_audio;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class AudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        CustomPlaybackControlView exoplayerView = (CustomPlaybackControlView) findViewById(R.id.exo_player_control);
        Player player = new Player(this,exoplayerView);
        Uri[] contentUri = {
                Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3"),
                Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3")
            };
        player.preparePlayer(contentUri);
    }
}
