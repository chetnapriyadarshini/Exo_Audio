package com.application.chetna_priya.exo_audio.Ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;

import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Utils.PreferenceHelper;

public class MainActivity extends BaseActivity implements TestAdapter.MediaFragmentListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_GENRE_ACTIVITY = 1;
    private static final String FRAGMENT_TAG = "fragment_container";

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
   // private PodcastPagerAdapter mPodcastPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
   // private ViewPager mViewPager;

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
            navigateToBrowser(null);
         //performIntialization();
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "NOTIFYYYYYYYYYYYYYYYYYYYYYYYY "+requestCode);

        switch (requestCode){
            case REQUEST_CODE_GENRE_ACTIVITY:
                performIntialization();
                break;
            case FeaturedFragment.REQUEST_CODE_ADD_GENRES:
                //mPodcastPagerAdapter.getItem(0).onActivityResult(requestCode, resultCode,data);
                break;
        }
    }*/
/*
    private void performIntialization() {
       // initializeToolbar();
        mPodcastPagerAdapter = new PodcastPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager_container);
        mViewPager.setAdapter(mPodcastPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.pager_tab);
        tabLayout.setupWithViewPager(mViewPager);
*//*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*//*
    }*/

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onMediaItemSelected, mediaId=" + item.getMediaId());
        if (item.isPlayable()) {
            getSupportMediaController().getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        } else if (item.isBrowsable()) {
            navigateToBrowser(item.getMediaId());
        } else {
            Log.w(TAG, "Ignoring MediaItem that is neither browsable nor playable: "+
                    "mediaId="+ item.getMediaId());
        }
    }

    @Override
    public void setToolbarTitle(CharSequence title) {
        Log.d(TAG, "Setting toolbar title to "+ title);
        if (title == null) {
            title = getString(R.string.app_name);
        }
        setTitle(title);
    }

    private void navigateToBrowser(String mediaId) {
        Log.d(TAG, "navigateToBrowser, mediaId=" + mediaId);
        FeaturedFragment fragment = getBrowseFragment();

        if (fragment == null || !TextUtils.equals(fragment.getMediaId(), mediaId)) {
            fragment = new FeaturedFragment();
            fragment.setMediaId(mediaId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            /*transaction.setCustomAnimations(
                    R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                    R.animator.slide_in_from_left, R.animator.slide_out_to_right);
           */ transaction.replace(R.id.container, fragment, FRAGMENT_TAG);
            // If this is not the top level media (root), we add it to the fragment back stack,
            // so that actionbar toggle and Back will work appropriately:
            if (mediaId != null) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    private FeaturedFragment getBrowseFragment() {
        return (FeaturedFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }


    @Override
    protected void onMediaControllerConnected() {
        getBrowseFragment().onConnected();
    }
}
