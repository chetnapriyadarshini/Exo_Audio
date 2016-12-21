package com.application.chetna_priya.exo_audio.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.data.PodcastContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadFragment extends Fragment
{

    private Cursor cursor;
    private DownloadEpisodesAdapter adapter;
    String TAG = DownloadFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "viewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");

        View rootView = inflater.inflate(R.layout.fragment_download, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.download_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DownloadEpisodesAdapter();
        recyclerView.setAdapter(adapter);
        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        if(cursor != null)
            cursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        cursor = getActivity().getContentResolver().query(
                PodcastContract.EpisodeEntry.CONTENT_URI,
                null, null, null, null);
        if(cursor!= null && cursor.moveToFirst()) {
            Log.d(TAG, "resumeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            //  cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    private class DownloadEpisodesAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_podcasts_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(cursor == null)
                return;
            cursor.moveToPosition(position);
            byte[] imgByte = cursor.getBlob(cursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_COVER_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            holder.icon_view.setImageBitmap(bitmap);
            holder.albumTitle.setText(cursor.getString(cursor.getColumnIndex
                    (PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE)));
           // cursor.close();
        }

        @Override
        public int getItemCount()
        {
            if(cursor == null)
                return  0;
            return cursor.getCount();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.album_icon)
        ImageView icon_view;

        @BindView(R.id.album_title)
        TextView albumTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(getAdapterPosition());
                    String mediaId = cursor.getString(
                            (cursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID)));

                    getActivity().getSupportMediaController().getTransportControls()
                            .playFromMediaId(mediaId, null);

                    Intent audioIntent = new Intent(getActivity(), AudioActivity.class);
                    startActivity(audioIntent);
                }
            });
            itemView.setContentDescription(albumTitle.getText());
        }
    }
}
