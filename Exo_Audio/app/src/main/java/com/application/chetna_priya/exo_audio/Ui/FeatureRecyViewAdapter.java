package com.application.chetna_priya.exo_audio.Ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
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
import com.application.chetna_priya.exo_audio.Utils.PreferenceHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

class FeatureRecyViewAdapter extends RecyclerView.Adapter<FeatureRecyViewAdapter.ViewHolder> {

    private ArrayList<String> categoriesList = new ArrayList<>();
    private Context mContext;

    FeatureRecyViewAdapter(Context context){
        this.mContext = context;
        /*
        In addition to user selected genre categories we also fetch toppodcasts
         */
        categoriesList.add(GenreHelper.TOP_PODCASTS);
        //Now we add all the saved genres
     //   categoriesList.addAll(PreferenceHelper.getSavedGenres(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_category_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String category = categoriesList.get(position);
        holder.titleView.setText(category);
        holder.seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        /*
        Load the albums from the given category here, we do that in the
        async task so as to not block the main thread
         */
        final ArrayList<Podcast> podcastArrayList = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String url = GenreHelper.getGenreUrl(category, mContext);
                podcastArrayList.addAll(new LoadAvailablePodcastChannels().load(url));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                holder.albumRecyclerView.setAdapter(new AlbumAdapter(podcastArrayList));
            }
        }.execute();
    }


    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
        }
    }


    /*
    This albumadapter class inflates individual podcast cardview with albumimage,
    description etc, we would want to display a limited number of albums here with
    see_all link to a list of all albums. A limited number of items meaning adjustible
    according to screen size, example for smartphones 3 for 10inch
    tabs maybe 5.
     */
    private class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

        private final ArrayList<Podcast> podcastList;

        AlbumAdapter(ArrayList<Podcast> podcasts){
            this.podcastList = podcasts;
        }

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
                    .load(podcastList.get(position).getArtwork_uri())
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
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });

       //     holder.cardView.setCardBackgroundColor(R.color.colorAccent);
            holder.album_info.setText(podcastList.get(position).getAlbum_title());
            holder.itemView.setContentDescription(holder.album_info.getText());
        }

        @Override
        public int getItemCount() {
            return  mContext.getResources().getInteger(R.integer.num_albums);
        }

    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.album_img)
        ImageView album_img;

        @BindView(R.id.album_info)
        TextView album_info;

        @BindView(R.id.card_view)
        CardView cardView;

        AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
