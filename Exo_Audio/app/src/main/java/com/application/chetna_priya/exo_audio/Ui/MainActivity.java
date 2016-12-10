package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

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


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PodcastPagerAdapter mPodcastPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!PreferenceHelper.isInitialGenrePreferenceSet(this)){
            Intent genreIntent = new Intent(this,GenreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(GenreActivity.IS_FIRST_TIME, true);
            startActivityForResult(genreIntent, REQUEST_CODE_GENRE_ACTIVITY, bundle);
        }else {
         performIntialization();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "NOTIFYYYYYYYYYYYYYYYYYYYYYYYY "+requestCode);

        switch (requestCode){
            case REQUEST_CODE_GENRE_ACTIVITY:
                performIntialization();
                break;
            case FeaturedFragment.REQUEST_CODE_ADD_GENRES:
                mPodcastPagerAdapter.getItem(0).onActivityResult(requestCode, resultCode,data);
                break;
        }
    }

    private void performIntialization() {
       // initializeToolbar();
        mPodcastPagerAdapter = new PodcastPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager_container);
        mViewPager.setAdapter(mPodcastPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.pager_tab);
        tabLayout.setupWithViewPager(mViewPager);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}
