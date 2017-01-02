package com.application.chetna_priya.exo_audio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.utils.MediaIDHelper;

public class FeaturedFragment extends Fragment {

    public static final int REQUEST_CODE_ADD_GENRES = 2;
    private static final String ARG_MEDIA_ID = "media_id";
    private static final String FRAGMENT_MEDIA_ID = "featured_fragment_media_id";
    // private TestAdapter testAdapter;
    private final String TAG = FeaturedFragment.class.getSimpleName();
    private MediaBrowserCompat.MediaItem mMediaId;
    private FeatureRecyViewAdapter mRecyViewAdapter;


    interface MediaFragmentListener extends MediaBrowserProvider {
        void setToolbarTitle(CharSequence title);
    }

    public MediaFragmentListener mMediaFragmentListener;

    /*
    This fragment needs both podcast and three episodes, we need to fetch 2 roots
    both of which belong to the browsable category

    Genres -- Health/Comedy -- 3 Podcasts
                    |
                    |
               Start here
     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mMediaFragmentListener = (MediaFragmentListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_featured_fragment, container, false);
        /*
        This is the view consisting of many recycler view diff category podcast and a cardview of add
        new categories all enclosed in a scroll view
         */
        if (savedInstanceState != null && savedInstanceState.getParcelable(FRAGMENT_MEDIA_ID) != null) {
            mMediaId = savedInstanceState.getParcelable(FRAGMENT_MEDIA_ID);
            setMediaId(mMediaId);
        }

        CardView add = (CardView) rootView.findViewById(R.id.add_new_categories_cardview);
        add.setContentDescription(getActivity().getString(R.string.add_new_categories));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent genreIntent = new Intent(getActivity(), GenreActivity.class);
                genreIntent.putExtra(GenreActivity.IS_FIRST_TIME, false);
                getActivity().startActivityForResult(genreIntent, REQUEST_CODE_ADD_GENRES);
            }
        });
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.album_category_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyViewAdapter = new FeatureRecyViewAdapter(getActivity(), getMediaId(), mMediaFragmentListener);
        recyclerView.setAdapter(mRecyViewAdapter);
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(FRAGMENT_MEDIA_ID, mMediaId);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();

        // fetch browsing information to fill the listview:
        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();

        Log.d(TAG, "fragment.onStart, mediaId=" + mMediaId +
                "  onConnected=" + mediaBrowser.isConnected());

        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mRecyViewAdapter.onStop();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mMediaFragmentListener = null;
    }

    public MediaBrowserCompat.MediaItem getMediaId() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getParcelable(ARG_MEDIA_ID);
        }
        return null;
    }

    public void setMediaId(MediaBrowserCompat.MediaItem mediaId) {
        Bundle args = new Bundle(1);
        args.putParcelable(FeaturedFragment.ARG_MEDIA_ID, mediaId);
        setArguments(args);
    }

    public void onConnected() {
        mMediaId = getMediaId();
        updateTitle();
        mRecyViewAdapter.onConnected();
    }

    private void updateTitle() {
        if (MediaIDHelper.MEDIA_ID_ROOT.equals(mMediaId.getMediaId())) {
            mMediaFragmentListener.setToolbarTitle(null);
            return;
        }

        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();
        mediaBrowser.getItem(mMediaId.getMediaId(), new MediaBrowserCompat.ItemCallback() {
            @Override
            public void onItemLoaded(MediaBrowserCompat.MediaItem item) {
                mMediaFragmentListener.setToolbarTitle(
                        item.getDescription().getTitle());
            }
        });
    }

}
