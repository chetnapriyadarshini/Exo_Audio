package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.application.chetna_priya.exo_audio.R;

public class AudioActivity extends AppCompatActivity {

    private static final String TAG = AudioActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
      //  audioFragment.setPlayerUrisAndPrepare(contentUri, false);

    }

    //TODO Check launch mode singleTop and onIntent method
        /*@Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        shouldRestorePosition = false;
        setIntent(intent);
    }*/

}
