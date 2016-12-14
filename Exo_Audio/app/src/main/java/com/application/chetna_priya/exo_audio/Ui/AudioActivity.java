package com.application.chetna_priya.exo_audio.Ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.chetna_priya.exo_audio.Entity.Episode;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Ui.PlaybackControlView.CustomPlaybackControlView;

public class AudioActivity extends AppCompatActivity implements CustomPlaybackControlView.Listener{

  //  private static final String TAG = AudioActivity.class.getSimpleName();
    public static final String EXTRA_START_FULLSCREEN = "start_full_screen";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "current_media_info";
    public static final String CURRENT_EPISODE = "current_episode";
    //  AudioFragment audioFragment;
    Episode episode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        if(getIntent().hasExtra(CURRENT_EPISODE))
            episode = (Episode) getIntent().getSerializableExtra(CURRENT_EPISODE);
      //  audioFragment = (AudioFragment) getSupportFragmentManager().findFragmentById(R.id.audio_fragment);

    }

    public void onMediaControllerSet(){
        getSupportMediaController().getTransportControls().playFromMediaId(episode.getId(), null);
    }



    //TODO Check launch mode singleTop and onIntent method
        /*@Override
    public void onNewIntent(Intent intent) {
        relaxResources();
        shouldRestorePosition = false;
        setIntent(intent);
    }*/

}
