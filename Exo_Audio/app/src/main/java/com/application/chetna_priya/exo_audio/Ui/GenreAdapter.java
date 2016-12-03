package com.application.chetna_priya.exo_audio.Ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.Entity.Genre;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.Utils.GenreHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    /*
    Item viewType approach not working for the following reasons:
    GridLayoutManager will not do as we need to set the span count
    We will need to use a LinearLayoutManager which stacks the children
    vertically or horizontally. What we need is HEADER - BODY - FOOTER
    that is vertical however we want the list items in body to be stacked
    horizontally.
     */

    private ArrayList<Genre> genreArrayList;

    public GenreAdapter(){
        GenreHelper helper = new GenreHelper();
        this.genreArrayList = helper.getGenreList();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.genre_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
            holder.genreImage.setImageResource(genreArrayList.get(position).getGenre_icon());
            holder.genreInfo.setText(genreArrayList.get(position).getGenre_desc());
    }

    @Override
    public int getItemCount() {
        return Genre.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView genreImage;
        TextView genreInfo;


        public ViewHolder(View itemView) {
            super(itemView);
            genreImage = (ImageView) itemView.findViewById(R.id.genre_img);
            genreInfo = (TextView) itemView.findViewById(R.id.genre_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
