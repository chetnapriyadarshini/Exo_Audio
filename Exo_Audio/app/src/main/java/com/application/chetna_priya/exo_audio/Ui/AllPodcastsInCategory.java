package com.application.chetna_priya.exo_audio.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllPodcastsInCategory extends BaseActivity {

    private static final String TAG = AllPodcastsInCategory.class.getSimpleName();

    MediaBrowserCompat.MediaItem mMediaItem;
    ArrayList<MediaBrowserCompat.MediaItem> mPodcastMediaItemList = new ArrayList<>();
    private AllpodAdapter allpodAdapter;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_all_albums);
        if (getIntent().hasExtra(BaseActivity.EXTRA_MEDIA_ITEM)) {
            mMediaItem = getIntent().getParcelableExtra(BaseActivity.EXTRA_MEDIA_ITEM);
        } else
            finish();


        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        RecyclerView podcastsRecyclerView = (RecyclerView) findViewById(R.id.allPodcast_recycler_view);
        podcastsRecyclerView.setLayoutManager(new LinearLayoutManager(AllPodcastsInCategory.this));
        allpodAdapter = new AllpodAdapter();
        podcastsRecyclerView.setAdapter(allpodAdapter);
    }


    @Override
    protected void onMediaControllerConnected() {
        super.onMediaControllerConnected();
        getMediaBrowser().unsubscribe(mMediaItem.getMediaId());
        getMediaBrowser().subscribe(mMediaItem.getMediaId(), mSubscriptionCallback);//We load the children of root that is album
    }


    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        linlaHeaderProgress.setVisibility(View.GONE);
                        mPodcastMediaItemList.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            mPodcastMediaItemList.add(item);
                        }
                        //    sortArrayList();
                        allpodAdapter.notifyDataSetChanged();

                    } catch (Throwable t) {
                        Log.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(AllPodcastsInCategory.this, R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };


    private class AllpodAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_podcasts_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(AllPodcastsInCategory.this)
                    .load(mPodcastMediaItemList.get(position).getDescription().getIconUri())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(holder.icon_view);

            String text = mPodcastMediaItemList.get(position).getDescription().getTitle().toString();
            int maxLength = getResources().getInteger(R.integer.max_podcast_title_length);
            if (text.length() > maxLength)
                text = text.substring(0, maxLength).concat("...");
            holder.albumTitle.setText(text);

        }

        @Override
        public int getItemCount() {
            return mPodcastMediaItemList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.album_icon)
        ImageView icon_view;

        @BindView(R.id.album_title)
        TextView albumTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent episodesIntent = new Intent(AllPodcastsInCategory.this, AllEpisodes.class);
                    episodesIntent.putExtra(BaseActivity.EXTRA_MEDIA_ITEM, mPodcastMediaItemList.get(getAdapterPosition()));
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) icon_view.getDrawable();
                    episodesIntent.putExtra(BaseActivity.EXTRA_BITMAP_POSTER, bitmapDrawable.getBitmap());
                    startActivity(episodesIntent);
                }
            });
            itemView.setContentDescription(albumTitle.getText());
        }
    }
}
