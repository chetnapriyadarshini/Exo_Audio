package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.application.chetna_priya.exo_audio.Utils.PreferenceHelper;

/**
 * Created by chetna_priya on 12/12/2016.
 */

public class LauncherActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GENRE_ACTIVITY = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!PreferenceHelper.isInitialGenrePreferenceSet(this)){
            Intent genreIntent = new Intent(this,GenreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(GenreActivity.IS_FIRST_TIME, true);
            startActivity(genreIntent,bundle);
        }else {
            Intent genreIntent = new Intent(this,MainActivity.class);
            startActivity(genreIntent);
            //performIntialization();
        }
    }
}
