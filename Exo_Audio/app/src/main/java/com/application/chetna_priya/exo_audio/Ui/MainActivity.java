package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.Bundle;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Utils.PreferenceHelper;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_GENRE_ACTIVITY = 1;

    public static final String EXTRA_START_FULLSCREEN =
            "com.application.chetna_priya.exo_audio.EXTRA_START_FULLSCREEN";
    /**
     * Optionally used with {@link #EXTRA_START_FULLSCREEN} to carry a MediaDescription to
     * the {@link AudioActivity}, speeding up the screen rendering
     * while the {@link android.support.v4.media.session.MediaControllerCompat} is connecting.
     */
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.application.chetna_priya.exo_audio.CURRENT_MEDIA_DESCRIPTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!PreferenceHelper.isInitialGenrePreferenceSet(this)){
            Intent genreIntent = new Intent(this,GenreActivity.class);
            startActivityForResult(genreIntent, REQUEST_CODE_GENRE_ACTIVITY, null);
        }
    }

}
