package com.application.chetna_priya.exo_audio.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;

import java.util.List;

public class MainActivity extends BaseActivity implements FeaturedFragment.MediaFragmentListener {

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
    private static final String MEDIA_ID = "media_id";
    private String mMediaId;


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

        /*if(!PreferenceHelper.isInitialGenrePreferenceSet(this)){
            Intent genreIntent = new Intent(this,GenreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(GenreActivity.IS_FIRST_TIME, true);
            startActivityForResult(genreIntent, REQUEST_CODE_GENRE_ACTIVITY, bundle);
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MEDIA_ID, mMediaId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getString(MEDIA_ID) != null)
            mMediaId = savedInstanceState.getString(MEDIA_ID);
    }

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

    /*@Override
    public void setToolbarTitle(CharSequence title) {
        Log.d(TAG, "Setting toolbar title to "+ title);
        if (title == null) {
            title = getString(R.string.app_name);
        }
        setTitle(title);
    }*/

    private void navigateToBrowser(MediaBrowserCompat.MediaItem mediaItem) {
        Log.d(TAG, "navigateToBrowser, mediaId=" + mediaItem);

        FeaturedFragment fragment = getBrowseFragment();

        //if (fragment == null || !TextUtils.equals(fragment.getMediaId(), mediaItem))
        if(fragment == null)
        {
            fragment = new FeaturedFragment();
            fragment.setMediaId(mediaItem);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            /*transaction.setCustomAnimations(
                    R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                    R.animator.slide_in_from_left, R.animator.slide_out_to_right);
           */ transaction.replace(R.id.container, fragment, FRAGMENT_TAG);
            // If this is not the top level media (root), we add it to the fragment back stack,
            // so that actionbar toggle and Back will work appropriately:
            if (mediaItem != null) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    private FeaturedFragment getBrowseFragment() {
        return (FeaturedFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }


    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        /*
                        We just have the genre here, we load genre as root and pass it to fragment
                         */
                        MediaBrowserCompat.MediaItem genreRoot  = children.get(0);
                        navigateToBrowser(genreRoot);;
                    } catch (Throwable t) {
                        Log.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(MainActivity.this, R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };




    @Override
    protected void onMediaControllerConnected() {
        mMediaId = getMediaBrowser().getRoot();
        getMediaBrowser().unsubscribe(mMediaId);
        getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);//We load the children of root that is genre
    }

    @Override
    public void setToolbarTitle(CharSequence title) {

    }
}
