package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.Data.LocalPersistence;
import com.application.chetna_priya.exo_audio.Entity.Episode;
import com.application.chetna_priya.exo_audio.Entity.Podcast;
import com.application.chetna_priya.exo_audio.Network.FetchIndividualPodcastEpisodes;
import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

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
    Podcast mPodcast;
    ArrayList<Episode> episodes;

    @BindView(R.id.podcast_title)
    TextView podcastTtile;

    @BindView(R.id.podcast_summary)
    TextView podcastSummary;


    private boolean isPodcastSummarySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Get the podcast album for which we will fetch all the albums
         */
        if(getIntent().hasExtra(AllPodcastsInCategory.PODCAST_OBJ)){
            mPodcast = (Podcast) getIntent().getSerializableExtra(AllPodcastsInCategory.PODCAST_OBJ);
        }else
            finish();
        setContentView(R.layout.layout_all_episodes);

        ButterKnife.bind(this);
        podcastTtile.setText(mPodcast.getAlbum_title());

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
             //   episodes = new FetchIndividualPodcastEpisodes().load(mPodcast);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                /*
                We attempt to sort the episodes in descending order
                with the newest episodes at top before displaying the list to the users
                 */
                Collections.sort(episodes, new Comparator<Episode>() {
                    @Override
                    public int compare(Episode episode1, Episode episode2) {
                        String releaseDate1 = episode1.getEpisode_published_on();
                        String releaseDate2 = episode2.getEpisode_published_on();
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

                isPodcastSummarySet = false;
                RecyclerView episodeRecyclerView = (RecyclerView) findViewById(R.id.episode_recycler_view);
                episodeRecyclerView.setNestedScrollingEnabled(false);
                episodeRecyclerView.setLayoutManager(new LinearLayoutManager(AllEpisodes.this));
                episodeRecyclerView.setAdapter(new EpisodesAdapter());
            }
        }.execute();
    }

    private class EpisodesAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.episode_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String releaseDate = episodes.get(position).getEpisode_published_on();
            holder.release_date.setText(formatDate(releaseDate));
            holder.episode_title.setText(episodes.get(position).getEpisode_title());
            if(!isPodcastSummarySet)
            {
                isPodcastSummarySet = true;
                podcastSummary.setText(episodes.get(position).getPodcastSummary());
                Log.d(TAG, "EPISDODE SUMMARRRRYYY SET AS "+episodes.get(position).getPodcastSummary());
            }
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
            return episodes.size();
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
                    LocalPersistence.witeObjectToFile(AllEpisodes.this, episodes.get(getAdapterPosition()),
                            getString(R.string.current_episode));
                    Intent audioIntent = new Intent(AllEpisodes.this, AudioActivity.class);
                    audioIntent.putExtra(AudioActivity.CURRENT_EPISODE, episodes.get(getAdapterPosition()));
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
