package com.application.chetna_priya.exo_audio.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Podcast implements Serializable {

    final String TAG = Podcast.class.getSimpleName();

   // private String id;
    private String album_title;
 //   private String album_cover_link;
    private long trackNumber;
    private String trackUri;
    private String artist;
    private String genre;
    private String artwork_uri;
    private long totalTrackCount;
    private String podcast_summary;

    public Podcast(long trackNumber, @NonNull String album_title, /*@NonNull String album_cover_link,*/ @NonNull String track_uri,
                   @NonNull String artist, @NonNull String genre, long totalTrackCount, @NonNull String artwork_uri){
        this.trackNumber = trackNumber;
        this.album_title = album_title;
       // this.album_cover_link = album_cover_link;
        this.trackUri = track_uri;
        this.artist = artist;
        this.artwork_uri = artwork_uri;
        this.genre = genre;
        this.totalTrackCount = totalTrackCount;
        //this.id = id;
    }


/*
    public String getId() {
        return id;
    }*/

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getArtwork_uri() {
        return artwork_uri;
    }

    public long getTotalTrackCount() {
        return totalTrackCount;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public String getTrackUri() {
        return trackUri;
    }

    public long getTrackNumber() {
        return trackNumber;
    }

   /* public String getAlbum_cover_link() {
        return album_cover_link;
    }*/

    @Override
    public String toString() {
        return "PODCAST : \n"+" TITLE: "+ album_title;
    }

    public void setSummary(String summary) {
        this.podcast_summary = summary;
    }

    public String getSummary(){
        return podcast_summary;
    }
}
