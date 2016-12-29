package com.application.chetna_priya.exo_audio.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.data.PodcastContract;
import com.application.chetna_priya.exo_audio.utils.BitmapHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;

import static com.application.chetna_priya.exo_audio.ui.FeaturedFragment.REQUEST_CODE_ADD_GENRES;

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
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    boolean needReload = false;
    private LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "IN ON CREATEEEEEEEEEEEEEEEE");
        initializeToolbar();
        /*if(!PreferenceHelper.isInitialGenrePreferenceSet(this)){
            Intent genreIntent = new Intent(this,GenreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(GenreActivity.IS_FIRST_TIME, true);
            startActivityForResult(genreIntent, REQUEST_CODE_GENRE_ACTIVITY, bundle);
        }*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
/*
        Cursor cursor = getContentResolver().query(PodcastContract.EpisodeEntry.CONTENT_URI,
                new String[]{PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID}, null, null,null);
        if(cursor != null && cursor.moveToFirst()) {
            Intent intentBrod = new Intent();
            intentBrod.setAction(getString(R.string.action_db_update));
            sendBroadcast(intentBrod);
            cursor.close();
        }*/
        startSignInActivity();
    }

    private void startSignInActivity() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                final ImageView profileImage = (ImageView) findViewById(R.id.profile_image);

                Picasso.with(this)
                        .load(acct.getPhotoUrl())
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) profileImage.getDrawable();
                                profileImage.setImageBitmap(BitmapHelper.getCircleBitmap(bitmapDrawable.getBitmap()));
                            }

                            @Override
                            public void onError() {
                                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
                            }
                        });
                TextView nameView = (TextView) findViewById(R.id.username);
                nameView.setText(acct.getDisplayName());
                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startSignInActivity();
                    }
                });
            }
        }
        if(requestCode == REQUEST_CODE_ADD_GENRES){
            Log.d(TAG, "SHOULD RELOADDDDDDDDDDDDDD HEREEEEEEEEEEEEEEE");
            needReload = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MEDIA_ID, mMediaId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getString(MEDIA_ID) != null) {
            mMediaId = savedInstanceState.getString(MEDIA_ID);
        }
    }


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
        if(fragment == null || needReload)
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
                        linlaHeaderProgress.setVisibility(View.GONE);
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
        Log.d(TAG, "MEDIA CONTROLLLLERRRRRRRRR CONNNECTEDDDDDDDDDDDD");
        mMediaId = getMediaBrowser().getRoot();
        getMediaBrowser().unsubscribe(mMediaId);
        getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);//We load the children of root that is genre
    }

    @Override
    public void setToolbarTitle(CharSequence title) {

    }
}
