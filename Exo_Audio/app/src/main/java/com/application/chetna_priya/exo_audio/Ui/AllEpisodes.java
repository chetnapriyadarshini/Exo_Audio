package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.Data.LocalPersistence;
import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AllEpisodes extends BaseActivity {

/*
    This fragment needs both podcast and three episodes, we need to fetch 2 roots
    both of which belong to the browsable category

    Genres -- Health/Comedy -- All Podcasts -- All Episodes
                                                   |
                                                   |
                                                Start here
     */

    private static final String TAG = AllEpisodes.class.getSimpleName();

   // ArrayList<Episode> episodes;

    @BindView(R.id.podcast_title)
    TextView podcastTtile;

    @BindView(R.id.podcast_summary)
    TextView podcastSummary;

    MediaBrowserCompat.MediaItem mMediaItem;

    ArrayList<MediaBrowserCompat.MediaItem> mEpisodeMediaItemList = new ArrayList<>();
    private EpisodesAdapter episodesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra(BaseActivity.EXTRA_MEDIA_ITEM)){
            mMediaItem = getIntent().getParcelableExtra(EXTRA_MEDIA_ITEM);
        }else
            finish();

        setContentView(R.layout.layout_all_episodes);


        ButterKnife.bind(this);
        podcastTtile.setText(mMediaItem.getDescription().getTitle());
        String summary = mMediaItem.getDescription().getExtras().getString(EXTRA_SUMMARY);
        podcastSummary.setText(summary);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");


        RecyclerView episodeRecyclerView = (RecyclerView) findViewById(R.id.episode_recycler_view);
        episodeRecyclerView.setNestedScrollingEnabled(false);
        episodeRecyclerView.setLayoutManager(new LinearLayoutManager(AllEpisodes.this));
        episodesAdapter =  new EpisodesAdapter();
        episodeRecyclerView.setAdapter(episodesAdapter);
    }

    void sortArrayList(){
                      /*
                We attempt to sort the episodes in descending order
                with the newest episodes at top before displaying the list to the users
                 */
        Collections.sort(mEpisodeMediaItemList, new Comparator<MediaBrowserCompat.MediaItem>() {
            @Override
            public int compare(MediaBrowserCompat.MediaItem mediaItem1, MediaBrowserCompat.MediaItem mediaItem2) {
                String releaseDate1 = mediaItem1.getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_DATE);
                String releaseDate2 = mediaItem2.getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_DATE);
                //   Month Day Year
                String[] dateArr1 = releaseDate1.split(" ");
                String[] dateArr2 = releaseDate2.split(" ");
                int relyear1 = Integer.parseInt(dateArr1[2]);
                int relyear2 = Integer.parseInt(dateArr2[2]);
                //if the year is same check for the month
                if(relyear1 == relyear2){
                    int compare = new MonthComparator().compare(dateArr1[1], dateArr2[1]);
                    //If months are equal we check for day
                    if(compare == 0){
                        return new DateComaprator().compare(dateArr1[0], dateArr2[0]);
                    }else
                        return  compare;
                }else
                    return relyear1 < relyear2 ? 1 : -1;
            }
        });
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
                        mEpisodeMediaItemList.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            mEpisodeMediaItemList.add(item);
                        }
                        sortArrayList();
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
            Log.d(TAG, "BUNDLEEEEEEEEEEEE "+mEpisodeMediaItemList.get(position).getDescription()
                    .getExtras().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            String releaseDate = epBundle.getString(MediaMetadataCompat.METADATA_KEY_DATE)/*getEpisode_published_on()*/;
            holder.release_date.setText(formatDate(releaseDate));
            holder.episode_title.setText(epBundle.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            /*if(!isPodcastSummarySet)
            {
                isPodcastSummarySet = true;
                podcastSummary.setText(episodes.get(position).getPodcastSummary());
                Log.d(TAG, "EPISDODE SUMMARRRRYYY SET AS "+episodes.get(position).getPodcastSummary());
            }*/
        }

        private String formatDate(String releaseDate) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String relArr[] = releaseDate.split(" ");
            int release_year = Integer.parseInt(relArr[2]);
            releaseDate = releaseDate.substring(0,releaseDate.length()-6);
            if(release_year == year){
                //5 - 4 for year eg 1999 and 1 for space
                releaseDate = String.format(releaseDate.replace(" ", "%n"));
            }else {
                releaseDate = releaseDate.concat("\n"+release_year);
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

         ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*LocalPersistence.witeObjectToFile(AllEpisodes.this, mEpisodeMediaItemList.get(getAdapterPosition()),
                            getString(R.string.current_episode));
                    Intent audioIntent = new Intent(AllEpisodes.this, AudioActivity.class);
                    audioIntent.putExtra(AudioActivity.CURRENT_EPISODE, mEpisodeMediaItemList.get(getAdapterPosition()));
                    startActivity(audioIntent);*/


                    getSupportMediaController().getTransportControls()
                            .playFromMediaId(mEpisodeMediaItemList.get(getAdapterPosition()).getMediaId(), null);

                    Intent audioIntent = new Intent(AllEpisodes.this, AudioActivity.class);
                 //   audioIntent.putExtra(BaseActivity.EXTRA_MEDIA_ITEM, mEpisodeMediaItemList.get(getAdapterPosition()));
                    startActivity(audioIntent);
                }
            });
        }
    }

    private String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
        "Aug", "Sept", "Oct", "Nov", "Dec"};
    private ArrayList<String> monthArrayList = new ArrayList<>(Arrays.asList(monthList));
    private class MonthComparator implements Comparator<String> {

        @Override
        public int compare(String month1, String month2) {
            int index1 = monthArrayList.indexOf(month1);
            int index2 = monthArrayList.indexOf(month2);
            return index1 < index2 ? 1 : index1 == index2 ? 0 : -1;
        }
    }

    private class DateComaprator implements Comparator<String> {
        @Override
        public int compare(String day1, String day2) {
            int firstDay = Integer.parseInt(day1);
            int secondDay = Integer.parseInt(day2);
            return firstDay < secondDay ? 1 : firstDay == secondDay ? 0 : -1;
        }
    }
}
