package com.application.chetna_priya.exo_audio.Ui;

import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

public class AudioActivity extends AppCompatActivity  implements AbstractPlaybackControlView.ActivityCallbacks{

    private static final String TAG = AudioActivity.class.getSimpleName();
    public static final String EXTRA_START_FULLSCREEN = "start_full_screen";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "current_media_info";
    AudioFragment audioFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        audioFragment = (AudioFragment) getSupportFragmentManager().findFragmentById(R.id.audio_fragment);

    }

    @Override
    public void finishActivity() {
        //finish();
    }

    @Override
    public void setSupportMediaControllerForActivity(MediaControllerCompat mediaController) {
        setSupportMediaController(mediaController);
    }


    //TODO Check launch mode singleTop and onIntent method
        /*@Override
    public void onNewIntent(Intent intent) {
        relaxResources();
        shouldRestorePosition = false;
        setIntent(intent);
    }*/

}
