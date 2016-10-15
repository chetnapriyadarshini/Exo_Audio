package com.application.chetna_priya.exo_audio;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class AudioActivity extends AppCompatActivity implements AudioFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        //TODO read Uri's
        Uri[] contentUri = {
                Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3"),
                Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3")
        };
        Bundle bundle = new Bundle();
        bundle.putSerializable("uris", contentUri);
        AudioFragment audioFragment = (AudioFragment) getSupportFragmentManager().findFragmentById(R.id.audio_fragment);
      //  audioFragment.setArguments(bundle);
        audioFragment.setPlayer(contentUri);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //TODO Check launch mode singleTop and onIntent method
        /*@Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        shouldRestorePosition = false;
        setIntent(intent);
    }*/

}
