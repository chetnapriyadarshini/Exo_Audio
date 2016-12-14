package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Utils.MediaIDHelper;

import java.util.ArrayList;
import java.util.List;

public class FeaturedFragment extends Fragment {

    public static final int REQUEST_CODE_ADD_GENRES = 2;
    private static final String ARG_MEDIA_ID = "media_id";
    private TestAdapter testAdapter;
    private final String TAG = FeaturedFragment.class.getSimpleName();
    private String mMediaId;
    private RecyclerView recyclerView;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_featured_fragment, container, false);
        /*
        This is the view consisting of many recycler view diff category podcast and a cardview of add
        new categories all enclosed in a scroll view
         */
        CardView add = (CardView) rootView.findViewById(R.id.add_new_categories_cardview);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent genreIntent = new Intent(getActivity(), GenreActivity.class);
                genreIntent.putExtra(GenreActivity.IS_FIRST_TIME, false);
                getActivity().startActivityForResult(genreIntent, REQUEST_CODE_ADD_GENRES);
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.album_category_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        testAdapter = new TestAdapter(getActivity());
        Log.d(TAG, "createeeeeeeeeeeeeee viewwwwwwwwwwwwwwwww");
        //commenting now we want to inflate the recycler view once the items are loaded
        //recyclerView.setAdapter(testAdapter);
        return rootView;
    }


    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    if (metadata == null) {
                        return;
                    }
                    Log.d(TAG, "Received metadata change to media "+metadata.getDescription().getMediaId());
                    testAdapter.notifyDataSetChanged();
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    Log.d(TAG, "Received state change: "+ state);

                    testAdapter.notifyDataSetChanged();
                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        ArrayList<MediaBrowserCompat.MediaItem> itemArrayList = new ArrayList<>();
                        //itemArrayList.clear();
                     //   testAdapter.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                      //      testAdapter.add(item);
                            itemArrayList.add(item);
                        }
                        testAdapter.setData(itemArrayList);
                        recyclerView.setAdapter(testAdapter);
                     //   testAdapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        Log.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(getActivity(), R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };


    @Override
    public void onStart() {
        super.onStart();

        // fetch browsing information to fill the listview:
        MediaBrowserCompat mediaBrowser = testAdapter.mMediaFragmentListener.getMediaBrowser();

        Log.d(TAG, "fragment.onStart, mediaId=" + mMediaId +
                "  onConnected=" + mediaBrowser.isConnected());

        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        MediaBrowserCompat mediaBrowser = testAdapter.mMediaFragmentListener.getMediaBrowser();
        if (mediaBrowser != null && mediaBrowser.isConnected() && mMediaId != null) {
            mediaBrowser.unsubscribe(mMediaId);
        }
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        testAdapter.mMediaFragmentListener = null;
    }

    public String getMediaId() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getString(ARG_MEDIA_ID);
        }
        return null;
    }

    public void setMediaId(String mediaId) {
        Bundle args = new Bundle(1);
        args.putString(FeaturedFragment.ARG_MEDIA_ID, mediaId);
        setArguments(args);
    }


    // Called when the MediaBrowser is connected. This method is either called by the
    // fragment.onStart() or explicitly by the activity in the case where the connection
    // completes after the onStart()
    public void onConnected() {
        if (isDetached()) {
            return;
        }
        mMediaId = getMediaId();
        Log.d(TAG, "ON CONNNECCCTEDDDDDDDDDDD "+mMediaId);
        if (mMediaId == null) {
            mMediaId = testAdapter.mMediaFragmentListener.getMediaBrowser().getRoot();
        }
        updateTitle();

        // Unsubscribing before subscribing is required if this mediaId already has a subscriber
        // on this MediaBrowser instance. Subscribing to an already subscribed mediaId will replace
        // the callback, but won't trigger the initial callback.onChildrenLoaded.
        //
        // This is temporary: A bug is being fixed that will make subscribe
        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
        // subscriber or not. Currently this only happens if the mediaID has no previous
        // subscriber or if the media content changes on the service side, so we need to
        // unsubscribe first.
        testAdapter.mMediaFragmentListener.getMediaBrowser().unsubscribe(mMediaId);

        testAdapter.mMediaFragmentListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    private void updateTitle() {
        if (MediaIDHelper.MEDIA_ID_ROOT.equals(mMediaId)) {
            testAdapter.mMediaFragmentListener.setToolbarTitle(null);
            return;
        }

        MediaBrowserCompat mediaBrowser = testAdapter.mMediaFragmentListener.getMediaBrowser();
        mediaBrowser.getItem(mMediaId, new MediaBrowserCompat.ItemCallback() {
            @Override
            public void onItemLoaded(MediaBrowserCompat.MediaItem item) {
                testAdapter.mMediaFragmentListener.setToolbarTitle(
                        item.getDescription().getTitle());
            }
        });
    }

}
