package com.application.chetna_priya.exo_audio.Ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.Entity.Genre;
import com.application.chetna_priya.exo_audio.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chetna_priya on 12/1/2016.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private Genre genre;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        genre = new Genre();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //TODO add genre img resources
        holder.genreImage.setImageResource(R.drawable.ic_launcher);
        holder.genreInfo.setText(genre.getGenre(position));
    }

    @Override
    public int getItemCount() {
        return genre.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.genre_img)
        ImageView genreImage;
        @BindView(R.id.genre_info)
        TextView genreInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
