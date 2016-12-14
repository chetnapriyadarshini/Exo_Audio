package com.application.chetna_priya.exo_audio.Ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;

/**
 * Created by chetna_priya on 12/11/2016.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    ArrayList<MediaBrowserCompat.MediaItem> itemArrayList;

    final String TAG = TestAdapter.class.getSimpleName();

    public void setData(ArrayList<MediaBrowserCompat.MediaItem> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    interface MediaFragmentListener extends MediaBrowserProvider {
        void onMediaItemSelected(MediaBrowserCompat.MediaItem item);
        void setToolbarTitle(CharSequence title);
    }
    public MediaFragmentListener mMediaFragmentListener;

    public TestAdapter(Context context) {
        mMediaFragmentListener = (MediaFragmentListener) context;
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new TestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        holder.titleView.setText(itemArrayList.get(position).getDescription().getTitle());
        holder.descriptionView.setText(itemArrayList.get(position).getDescription().getSubtitle());
    }


    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView descriptionView;

        public TestViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.title);
            descriptionView = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMediaFragmentListener.onMediaItemSelected(itemArrayList.get(getAdapterPosition()));
                }
            });
        }
    }
}
