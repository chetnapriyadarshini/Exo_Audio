package com.application.chetna_priya.exo_audio.Ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.Entity.Podcast;
import com.application.chetna_priya.exo_audio.Network.LoadAvailablePodcastChannels;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Utils.GenreHelper;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class AllPodcastsInCategory extends BaseActivity {

    private static final String TAG = AllPodcastsInCategory.class.getSimpleName();
    String selected_category;
    ArrayList<Podcast> podcastArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_all_albums);
        if(getIntent().hasExtra(FeatureRecyViewAdapter.ALBUM_CATEGORY)){
            selected_category = getIntent().getStringExtra(FeatureRecyViewAdapter.ALBUM_CATEGORY);
        }else
            finish();
        Log.d(TAG, "Selected Categoryyyyyyyyy "+selected_category);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String url = GenreHelper.getGenreUrl(selected_category, -1);
                podcastArrayList = new LoadAvailablePodcastChannels().load(url);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                RecyclerView podcastsRecyclerView = (RecyclerView) findViewById(R.id.allPodcast_recycler_view);
                podcastsRecyclerView.setLayoutManager(new LinearLayoutManager(AllPodcastsInCategory.this));
                podcastsRecyclerView.setAdapter(new AllpodAdapter());
            }
        }.execute();
    }

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
                    .load(podcastArrayList.get(position).getArtwork_uri())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(holder.icon_view);

            String text = podcastArrayList.get(position).getAlbum_title();
            int maxLength = getResources().getInteger(R.integer.max_podcast_title_length);
            if(text.length() > maxLength)
                text = text.substring(0, maxLength).concat("...");
            holder.albumTitle.setText(text);

        }

        @Override
        public int getItemCount() {
            return podcastArrayList.size();
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
        }
    }
}
