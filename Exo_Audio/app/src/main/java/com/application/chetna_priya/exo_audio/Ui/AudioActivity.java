package com.application.chetna_priya.exo_audio.Ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.chetna_priya.exo_audio.R;

public class AudioActivity extends AppCompatActivity {

    private static final String TAG = AudioActivity.class.getSimpleName();
    public static final String EXTRA_START_FULLSCREEN = "start_full_screen";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "current_media_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
      //  audioFragment.setPlayerUrisAndPrepare(contentUri, false);

    }

    //TODO Check launch mode singleTop and onIntent method
        /*@Override
    public void onNewIntent(Intent intent) {
        relaxResources();
        shouldRestorePosition = false;
        setIntent(intent);
    }*/

}
