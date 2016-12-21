package com.application.chetna_priya.exo_audio.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class FeatureRecyViewAdapter extends RecyclerView.Adapter<FeatureRecyViewAdapter.ViewHolder> {

    static final String ALBUM_CATEGORY = "album_category";
    private int numAlbums = 3;
    private ArrayList<MediaBrowserCompat.MediaItem> categoriesList = new ArrayList<>();
    private Context mContext;
    private MediaBrowserCompat.MediaItem mediaItem;
    private FeaturedFragment.MediaFragmentListener mMediaFragmentListener;
    private final String TAG = FeatureRecyViewAdapter.class.getSimpleName();
    private CopyOnWriteArrayList<Listener> listeners;

    FeatureRecyViewAdapter(Context context, MediaBrowserCompat.MediaItem mediaItem,
                           FeaturedFragment.MediaFragmentListener mediaFragmentListener){
        //We expect to be sent genre root over here
        //The above root will be expanded to load the list of the genres
        this.mContext = context;
        this.mediaItem = mediaItem;
        numAlbums = mContext.getResources().getInteger(R.integer.num_albums);
        mMediaFragmentListener = mediaFragmentListener;
        listeners = new CopyOnWriteArrayList<>();
    }

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        categoriesList.clear();
                        //   testAdapter.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            //      testAdapter.add(item);
                            categoriesList.add(item);
                        }
                        notifyDataSetChanged();
                        //   testAdapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        Log.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(mContext, R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_category_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.titleView.setText(categoriesList.get(position).getDescription().getTitle());
        holder.seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allpodcasts = new Intent(mContext, AllPodcastsInCategory.class);
                allpodcasts.putExtra(BaseActivity.EXTRA_MEDIA_ITEM, categoriesList.get(holder.getAdapterPosition()));
                mContext.startActivity(allpodcasts);
            }
        });
        Log.d(TAG, "Initializing with media item "+categoriesList.get(position).getMediaId());
        holder.albumAdapter = new AlbumAdapter(categoriesList.get(position),
                mContext, numAlbums, mMediaFragmentListener);
        holder.albumRecyclerView.setAdapter(holder.albumAdapter);
        listeners.add(holder.albumAdapter);

    }


    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

     void onStop() {
        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();
        if (mediaBrowser != null && mediaBrowser.isConnected() && mediaItem != null) {
            mediaBrowser.unsubscribe(mediaItem.getMediaId());
        }
         for(Listener listener : listeners){
             listener.onStop();
         }
    }

      void onConnected() {
        mMediaFragmentListener.getMediaBrowser().unsubscribe(mediaItem.getMediaId());
        Log.d(TAG, "Subscribeeeeeee to "+mediaItem.getMediaId());
        mMediaFragmentListener.getMediaBrowser().subscribe(mediaItem.getMediaId(), mSubscriptionCallback);
    }

    interface Listener {
        void onStop();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private AlbumAdapter albumAdapter;
        // Title, see_all

        @BindView(R.id.list_title)
        TextView titleView;

        @BindView(R.id.see_all_link)
        TextView seeAll;

        @BindView(R.id.album_recycler_view)
        RecyclerView albumRecyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            /*
             Below is the recycler view inside the parent recycler view(with the add new catgroies card view)
            title, see_all and then this child recycler view containing a list of cardviews of individual albums
             */
            albumRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            albumRecyclerView.setNestedScrollingEnabled(false);
            seeAll.setContentDescription(mContext.getString(R.string.see_all_pod)+titleView.getText());
            titleView.setContentDescription(titleView.getText());
        }
    }


}
