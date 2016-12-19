package com.application.chetna_priya.exo_audio.entity;


import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * This class represents a single entry (post) in the XML feed.
 *
 * <p>It includes the data members "title," "link," and "summary."
 */
public class Episode implements Serializable {

    private final String TAG = Episode.class.getSimpleName();

    private String episode_title;
    private String episode_summary;
    private String episode_pod_link;
    private String episode_duration;
    private String episode_published_on;

    private Podcast podcast;
    private String podcast_summary;

    public Episode(@NonNull Podcast podcast, @NonNull String episode_title, @NonNull String episode_duration, @NonNull String episode_summary,
                   @NonNull String episode_pod_url, @NonNull String episode_release_date, String podcast_summary) {
        this.episode_title = episode_title;
        this.episode_pod_link = episode_pod_url;
        this.episode_summary = episode_summary;
        this.episode_duration = episode_duration;
        this.episode_published_on = episode_release_date;
        this.podcast = podcast;
        this.podcast_summary = podcast_summary;
    }


    public Podcast getPodcast() {
        return podcast;
    }

    public String getEpisode_title() {
        return episode_title;
    }

    public String getEpisode_summary() {
        return episode_summary;
    }

    public String getEpisode_pod_link() {
        return episode_pod_link;
    }

    public String getEpisode_duration() {
        return episode_duration;
    }

    public String getEpisode_published_on() {
        return episode_published_on;
    }

    @Override
    public String toString() {
        return "NEW EPISODE: \n"+" TITLE: "+episode_title+"\n SUMMARY: "+episode_summary
                +"\n DURATION "+episode_duration
                +"\n PODCAST LINK: "+episode_pod_link
                +"\n RELEASE DATE: "+episode_published_on;
    }

    public String getId() {
        return String.valueOf((podcast.getTrackNumber() + getEpisode_published_on()).hashCode());
    }

    public String getPodcastSummary() {
        return podcast_summary;
    }
}
