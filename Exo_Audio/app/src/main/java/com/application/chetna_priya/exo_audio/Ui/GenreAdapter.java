package com.application.chetna_priya.exo_audio.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.entity.Genre;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.utils.GenreHelper;
import com.application.chetna_priya.exo_audio.utils.PreferenceHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    /*
    Item viewType approach not working for the following reasons:
    GridLayoutManager will not do as we need to set the span count
    We will need to use a LinearLayoutManager which stacks the children
    vertically or horizontally. What we need is HEADER - BODY - FOOTER
    that is vertical however we want the list items in body to be stacked
    horizontally.
     */

    private Listener listener;
    private ArrayList<String> savedGenres;

    interface Listener {
        void onGenreSaved(int totalGenreSaved);
    }

    private ArrayList<Genre> genreArrayList;
    private Context mContext;
    private static final String TAG = GenreAdapter.class.getSimpleName();

    GenreAdapter(Context context) {
        listener = (Listener) context;
        mContext = context;
        GenreHelper helper = new GenreHelper();
        this.genreArrayList = helper.getGenreList();
        savedGenres = PreferenceHelper.getSavedGenres(context);
        if (savedGenres == null)
            savedGenres = new ArrayList<>();
        else
            Log.d(TAG, "GENREEEEEEEEE SIZEEEEEE " + savedGenres.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.genre_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.genreImage.setImageResource(genreArrayList.get(position).getGenre_icon());
        String genreDesc = genreArrayList.get(position).getGenre_desc();
        holder.genreInfo.setText(genreDesc);
            /*
            Set color for selected genre in OnBindViewHolder
             */
        if (holder.isGenreSelected(genreDesc)) {
            holder.itemView.setBackgroundColor(R.color.colorAccent);
        }

        if (holder.isGenreSelected(genreArrayList.get(position).getGenre_desc()))
            holder.itemView.setContentDescription(mContext.getString(R.string.remove) + genreArrayList.get(position).getGenre_desc()
                    + mContext.getString(R.string.genre));
        else
            holder.itemView.setContentDescription(mContext.getString(R.string.select) + genreArrayList.get(position).getGenre_desc()
                    + mContext.getString(R.string.genre));
    }

    @Override
    public int getItemCount() {
        return GenreHelper.TOTAL_ITEM_COUNT;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.genre_img)
        ImageView genreImage;

        @BindView(R.id.genre_info)
        TextView genreInfo;

        @BindView(R.id.card_view)
        CardView cardView;

        ViewHolder(final View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String desc = (genreArrayList.get(getAdapterPosition()).getGenre_desc());
                    if (savedGenres.contains(desc))
                        savedGenres.remove(desc);
                    else {
                        savedGenres.add(desc);
                    }
                    listener.onGenreSaved(savedGenres.size());
                    notifyItemChanged(getAdapterPosition());
                    /*TODO set the color according to the outline which will be set
                       accroding to the pallette from the icon of the genre
                      */
                }
            });
        }

        boolean isGenreSelected(String genreDesc) {
            return savedGenres.contains(genreDesc);
        }
    }


    ArrayList<String> getSavedGenres() {
        return savedGenres;
    }
}
