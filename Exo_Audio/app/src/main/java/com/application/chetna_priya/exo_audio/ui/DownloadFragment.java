package com.application.chetna_priya.exo_audio.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.application.chetna_priya.exo_audio.entity.Episode;
import com.application.chetna_priya.exo_audio.entity.MetadataEntity;
import com.application.chetna_priya.exo_audio.entity.Podcast;
import com.application.chetna_priya.exo_audio.utils.PathHelper;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.application.chetna_priya.exo_audio.ui.PlaybackControlsFragment.EXTRA_IMAGE_URI;
import static com.application.chetna_priya.exo_audio.ui.PlaybackControlsFragment.EXTRA_TITLE;

public class DownloadFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DOWNLOAD_LOADER = 1;
    public static final String PLAY_FORM_CURSOR_BUNDLE = "play_from_cursor_bundle";
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_METADATA_OBJ = "extra_metadata_obj";
    private Cursor mCursor;
    private DownloadEpisodesAdapter adapter;
    String TAG = DownloadFragment.class.getSimpleName();
    private TextView mEmptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "viewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");

        View rootView = inflater.inflate(R.layout.fragment_download, container, false);
        mEmptyView = (TextView) rootView.findViewById(R.id.recyclerview_empty);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.download_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DownloadEpisodesAdapter();
        recyclerView.setAdapter(adapter);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                PodcastContract.EpisodeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DOWNLOAD_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.mCursor = data;

        if (mCursor != null && mCursor.moveToFirst()) {
            //  mCursor.close();
            adapter.notifyDataSetChanged();
            mEmptyView.setVisibility(View.GONE);
        } else
            mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(DOWNLOAD_LOADER, null, this);
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        mCursor = getActivity().getContentResolver().query(
                PodcastContract.EpisodeEntry.CONTENT_URI,
                null, null, null, null);
        if(mCursor!= null && mCursor.moveToFirst()) {
            //  mCursor.close();
            adapter.notifyDataSetChanged();
            mEmptyView.setVisibility(View.GONE);
        }else
            mEmptyView.setVisibility(View.VISIBLE);
    }*/

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
            if (mCursor == null)
                return;
            mCursor.moveToPosition(position);
            byte[] imgByte = mCursor.getBlob(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_COVER_IMAGE));
            final Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            holder.icon_view.setImageBitmap(bitmap);
            holder.albumTitle.setText(mCursor.getString(mCursor.getColumnIndex
                    (PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE)));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_NAME));
                    String path = PathHelper.getDownloadPodcastPath(getActivity());
                    String source = path + "/" + name;
                    Log.d(TAG, "SAVED IN PATH " + source + " MEDIA ID " +
                            mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID)));
                    /*
                    Set up bundle with extra information required to set up the queue
                    for the downloaded episode
                     */
                    Bundle playBundle = new Bundle();
                    playBundle.putParcelable(EXTRA_IMAGE, bitmap);
                    playBundle.putSerializable(EXTRA_METADATA_OBJ, createMetadataObjFromCursor());

                    getActivity().getSupportMediaController().getTransportControls()
                            .playFromUri(Uri.parse(source), playBundle);

                    Intent audioIntent = new Intent(getActivity(), AudioActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(EXTRA_IMAGE,
                            mCursor.getBlob(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_COVER_IMAGE)));
                    bundle.putString(EXTRA_TITLE, mCursor.getString(mCursor.getColumnIndex
                            (PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE)));
                    audioIntent.putExtra(PLAY_FORM_CURSOR_BUNDLE, bundle);
                    startActivity(audioIntent);
                }
            });
            // mCursor.close();
        }

        private MetadataEntity createMetadataObjFromCursor() {

            return new MetadataEntity(mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE)),
                    mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_LINK)),
                    mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_SUMMARY)),
                    mCursor.getLong(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_DURATION)),
                    mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE)),
                    mCursor.getString(mCursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID)));
        }

        @Override
        public int getItemCount() {
            if (mCursor == null)
                return 0;
            return mCursor.getCount();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.album_icon)
        ImageView icon_view;

        @BindView(R.id.album_title)
        TextView albumTitle;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setContentDescription(albumTitle.getText());
        }
    }
}
