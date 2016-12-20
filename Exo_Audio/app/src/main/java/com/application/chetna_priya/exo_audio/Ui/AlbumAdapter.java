package com.application.chetna_priya.exo_audio.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

/*
This albumadapter class inflates individual podcast cardview with albumimage,
description etc, we would want to display a limited number of albums here with
see_all link to a list of all albums. A limited number of items meaning adjustible
according to screen size, example for smartphones 3 for 10inch
tabs maybe 5.
 */
class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> implements FeatureRecyViewAdapter.Listener {

    private static final String TAG = AlbumAdapter.class.getSimpleName();
    private int NUM_ALBUMS = 3;
    private Context mContext;
    private FeaturedFragment.MediaFragmentListener mMediaFragmentListener;
    private ArrayList<MediaBrowserCompat.MediaItem> itemArrayList = new ArrayList<>();
    private MediaBrowserCompat.MediaItem mMediaItem;

    AlbumAdapter(MediaBrowserCompat.MediaItem mediaItem, Context context, int num_albums,
                 FeaturedFragment.MediaFragmentListener mediaFragmentListener){
        this.mContext = context;
        this.mMediaFragmentListener = mediaFragmentListener;
        this.mMediaItem = mediaItem;
        NUM_ALBUMS = /*mContext.getResources().getInteger(R.integer.num_albums)*/ num_albums;
        Log.d(TAG, "Subscribeddd to "+mediaItem.getMediaId());
        mMediaFragmentListener.getMediaBrowser().unsubscribe(mediaItem.getMediaId());
        mMediaFragmentListener.getMediaBrowser().subscribe(mediaItem.getMediaId(), mSubscriptionCallback);
        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = ((FragmentActivity) mContext)
                .getSupportMediaController();
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
            controller.registerCallback(mMediaControllerCallback);
        }
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
                    notifyDataSetChanged();
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    Log.d(TAG, "Received state change: "+ state);

                    notifyDataSetChanged();
                }
            };


    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                    "  count=" + children.size());
            itemArrayList.clear();
            //We only want to load first three for the main page
            for (int i = 0; i < NUM_ALBUMS; i++) {
                //      testAdapter.add(item);
                itemArrayList.add(children.get(i));
            }
            notifyDataSetChanged();
        }

        @Override
        public void onError(@NonNull String parentId) {
            super.onError(parentId);
        }
    };

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_list_item, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, int position) {
        holder.album_img.setScaleType(CENTER_CROP);
            /*
            We load the required podcasts and load there image and
            title in the card view in this method
             */
        Picasso.with(mContext)
                .load(itemArrayList.get(position).getDescription().getIconUri())
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(holder.album_img, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.album_img.getDrawable();
                        Palette.from(bitmapDrawable.getBitmap()).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int color = palette.getDarkVibrantColor(Color.BLACK);
                                int textColor = palette.getLightVibrantColor(Color.WHITE);
                                holder.cardView.setCardBackgroundColor(color);
                                holder.album_info.setTextColor(textColor);
                                holder.album_artist.setTextColor(textColor);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    }
                });


        holder.album_info.setText(itemArrayList.get(position).getDescription().getTitle());
        holder.album_artist.setText(itemArrayList.get(position).getDescription().getSubtitle());
        holder.itemView.setContentDescription(holder.album_info.getText());
    }

    @Override
    public int getItemCount() {
        return  itemArrayList.size();
    }

    @Override
    public void onStop() {
        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();
        if (mediaBrowser != null && mediaBrowser.isConnected() && mMediaItem != null) {
            mediaBrowser.unsubscribe(mMediaItem.getMediaId());
        }
        MediaControllerCompat controller = ((FragmentActivity) mContext)
                .getSupportMediaController();
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }
    }


    class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.album_img)
        ImageView album_img;

        @BindView(R.id.album_info)
        TextView album_info;

        @BindView(R.id.album_artist)
        TextView album_artist;

        @BindView(R.id.card_view)
        CardView cardView;

        AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AllEpisodes.class);
                    intent.putExtra(BaseActivity.EXTRA_MEDIA_ITEM, itemArrayList.get(getAdapterPosition()));
                    //We send the bitmap so that it can be saved in database in case user
                    //decides to download the episode and view it offline
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) album_img.getDrawable();
                    intent.putExtra(BaseActivity.EXTRA_BITMAP_POSTER, bitmapDrawable.getBitmap());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}


