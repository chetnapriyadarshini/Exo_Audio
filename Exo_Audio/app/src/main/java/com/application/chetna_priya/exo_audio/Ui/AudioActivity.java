package com.application.chetna_priya.exo_audio.Ui;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.chetna_priya.exo_audio.Entity.Episode;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Ui.PlaybackControlView.CustomPlaybackControlView;

public class AudioActivity extends AppCompatActivity implements CustomPlaybackControlView.Listener{

  //  private static final String TAG = AudioActivity.class.getSimpleName();
    public static final String EXTRA_START_FULLSCREEN = "start_full_screen";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "current_media_info";
  CustomPlaybackControlView customPlaybackControlView;
    //  AudioFragment audioFragment;
   // MediaBrowserCompat.MediaItem mediaItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
      customPlaybackControlView = (CustomPlaybackControlView) findViewById(R.id.exo_player_control);
      /*
        if(getIntent().hasExtra(BaseActivity.EXTRA_MEDIA_ITEM))
            mediaItem = getIntent().getParcelableExtra(BaseActivity.EXTRA_MEDIA_ITEM);*/
      //  audioFragment = (AudioFragment) getSupportFragmentManager().findFragmentById(R.id.audio_fragment);

    }

    public void onMediaControllerSet(){
     //   getSupportMediaController().getTransportControls().playFromMediaId(mediaItem.getMediaId(), null);
    }

  @Override
  protected void onPause() {
    super.onPause();
    customPlaybackControlView.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    customPlaybackControlView.onResume();
  }

  //TODO Check launch mode singleTop and onIntent method
        /*@Override
    public void onNewIntent(Intent intent) {
        relaxResources();
        shouldRestorePosition = false;
        setIntent(intent);
    }*/

}
