package com.application.chetna_priya.exo_audio.ui;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
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

import com.application.chetna_priya.exo_audio.model.MediaProviderSource;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.utils.DBHelper;
import com.application.chetna_priya.exo_audio.utils.PathHelper;
import com.application.chetna_priya.exo_audio.utils.PermissionHelper;
import com.application.chetna_priya.exo_audio.utils.SortingHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AllEpisodes extends BaseActivity {

    private static final String TAG = AllEpisodes.class.getSimpleName();

    @BindView(R.id.podcast_title)
    TextView podcastTtile;

    @BindView(R.id.podcast_summary)
    TextView podcastSummary;

    MediaBrowserCompat.MediaItem mMediaItem;

    static MediaBrowserCompat.MediaItem selMediaItem;


    ArrayList<MediaBrowserCompat.MediaItem> mEpisodeMediaItemList = new ArrayList<>();
    private EpisodesAdapter episodesAdapter;
    private DownloadManager dm;
    private static long enqueue;
    private DownloadManager.Request request;
    static Bitmap iconBitmap = null;
    private LinearLayout linlaHeaderProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(BaseActivity.EXTRA_MEDIA_ITEM)) {
            mMediaItem = getIntent().getParcelableExtra(EXTRA_MEDIA_ITEM);
        } else
            finish();

        setContentView(R.layout.layout_all_episodes);

        //   setDownloadBroadcastReceiver();
        ButterKnife.bind(this);
        podcastTtile.setText(mMediaItem.getDescription().getTitle());
        podcastTtile.setContentDescription(podcastTtile.getText());
        int sum_length = getResources().getInteger(R.integer.summary_length);
        String summary = mMediaItem.getDescription().getExtras().getString(EXTRA_SUMMARY);
        if (summary.length() > sum_length)
            summary = summary.substring(0, sum_length).concat("...");
        podcastSummary.setText(summary);
        podcastSummary.setContentDescription(podcastSummary.getText());

        if (getIntent().hasExtra(BaseActivity.EXTRA_BITMAP_POSTER)) {
            iconBitmap = getIntent().getParcelableExtra(EXTRA_BITMAP_POSTER);
        }

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");


        RecyclerView episodeRecyclerView = (RecyclerView) findViewById(R.id.episode_recycler_view);
        episodeRecyclerView.setNestedScrollingEnabled(false);
        episodeRecyclerView.setLayoutManager(new LinearLayoutManager(AllEpisodes.this));
        episodesAdapter = new EpisodesAdapter();
        episodeRecyclerView.setAdapter(episodesAdapter);

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
                        linlaHeaderProgress.setVisibility(View.GONE);
                        Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        mEpisodeMediaItemList.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            mEpisodeMediaItemList.add(item);
                        }
                        new SortingHelper().sortArrayList(mEpisodeMediaItemList);
                        episodesAdapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        Log.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(AllEpisodes.this, R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };


    private class EpisodesAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.episode_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Bundle epBundle = mEpisodeMediaItemList.get(position).getDescription().getExtras();
           /* Log.d(TAG, "BUNDLEEEEEEEEEEEE "+mEpisodeMediaItemList.get(position).getDescription()
                    .getExtras().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            */
            String releaseDate = epBundle.getString(MediaMetadataCompat.METADATA_KEY_DATE)/*getEpisode_published_on()*/;
            holder.release_date.setText(formatDate(releaseDate));
            holder.release_date.setContentDescription(holder.release_date.getText());
            holder.episode_title.setText(epBundle.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            holder.episode_title.setContentDescription(holder.episode_title.getText());
        }

        private String formatDate(String releaseDate) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String relArr[] = releaseDate.split(" ");
            int release_year = Integer.parseInt(relArr[2]);
            releaseDate = releaseDate.substring(0, releaseDate.length() - 6);
            if (release_year == year) {
                //5 - 4 for year eg 1999 and 1 for space
                releaseDate = String.format(releaseDate.replace(" ", "%n"));
            } else {
                releaseDate = releaseDate.concat("\n" + release_year);
            }

            return releaseDate;
        }

        @Override
        public int getItemCount() {
            return mEpisodeMediaItemList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.episode_release_date)
        TextView release_date;

        @BindView(R.id.episode_title)
        TextView episode_title;

        @BindView(R.id.img_download)
        ImageView download_episode;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSupportMediaController().getTransportControls()
                            .playFromMediaId(mEpisodeMediaItemList.get(getAdapterPosition()).getMediaId(), null);

                    Intent audioIntent = new Intent(AllEpisodes.this, AudioActivity.class);
                    audioIntent.putExtra(BaseActivity.EXTRA_MEDIA_ITEM, mEpisodeMediaItemList.get(getAdapterPosition()));
                    startActivity(audioIntent);
                }
            });

            download_episode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean shouldstart = true;
                    if (!PermissionHelper.requestForPermission(AllEpisodes.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        shouldstart = false;
                    String uri = mEpisodeMediaItemList.get(getAdapterPosition()).getDescription().
                            getExtras().getString(MediaProviderSource.CUSTOM_METADATA_TRACK_SOURCE);
                    selMediaItem = mEpisodeMediaItemList.get(getAdapterPosition());
                    if (dm == null)
                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    request = new DownloadManager.Request(Uri.parse(uri));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setMimeType("audio/MP3");
                    if (shouldstart)
                        startDownload();
                    //  request.setDestinationInExternalFilesDir(AllEpisodes.this,null,uri/*
                             /*getString(R.string.app_name) + "/" + podcastTtile.getText() + "/" + episode_title.getText());*/

                }
            });
            download_episode.setContentDescription(getString(R.string.download_episode));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_USAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                    //  notifyUser();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startDownload() {
        String name = selMediaItem.getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        int maxfileNamelength = 10;
        if (name.length() > maxfileNamelength)
            name = name.substring(0, maxfileNamelength);
        //Remove all spaces from the name
        name = name.replaceAll("\\s+", "").concat(".m4v");
        request.setTitle(name);

        request.setDestinationInExternalPublicDir(getString(R.string.app_name)
                /*PathHelper.getDownloadPodcastPath(AllEpisodes.this)*/, name);
        enqueue = dm.enqueue(request);
    }


    public static class DownloadListener extends BroadcastReceiver {

        public DownloadListener() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if (enqueue != downloadId)
                    return;
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = dm.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                    String title = cursor.getString(columnIndex);
                    //  Log.d(TAG, "COLUMMMMMMMNNNNNNNNN "+title);
                    String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    //Log.d(TAG, "COLUMMMMMMMNNNNNNNNN "+localUri);

                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    //Log.d(TAG, "COLUMMMMMMMNNNNNNNNN "+status);

                    int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                    //Log.d(TAG, "COLUMMMMMMMNNNNNNNNN "+reason);

                    if (status == DownloadManager.STATUS_SUCCESSFUL)
                        DBHelper.insertInDb(context, selMediaItem, iconBitmap, title);
                    cursor.close();

                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                /*if(cursor == null){
                    Intent resultIntent = new Intent(context, AllEpisodes.class);
                    context.startActivity(resultIntent);
                }else*/
                {
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.putExtra(DrawerActivity.OPEN_DOWNLOAD, true);
                    context.startActivity(resultIntent);
                }
            }
        }
    }
}
