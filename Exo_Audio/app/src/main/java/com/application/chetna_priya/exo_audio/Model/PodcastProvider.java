package com.application.chetna_priya.exo_audio.Model;

import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;

/**
 * Created by chetna_priya on 10/27/2016.
 */
//TODO Complete implementation
public class PodcastProvider {
    public boolean isFavorite(String podcastId) {
        return false;
    }

    public void setFavorite(String podcastId, boolean isFavorite) {

    }

    public MediaMetadataCompat getPodcast(String currentPlayingId) {
        //TODO Create a proper implementation here, dummy implementation for now
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "1234")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Comedy Bang Bang")
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, "Comedy")
                .build();
    }

    public Iterable<MediaMetadataCompat> getPodcastsByGenre(String categoryValue) {
        return null;
    }

    public void updatePodcastArt(String podcastId, Bitmap bitmap, Bitmap icon) {

    }


    public Iterable<MediaMetadataCompat> searchPodcastByPodcastTitle(String categoryValue) {
        return null;
    }
}
