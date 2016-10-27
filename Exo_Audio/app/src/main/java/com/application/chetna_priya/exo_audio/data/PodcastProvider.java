package com.application.chetna_priya.exo_audio.data;

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
        return null;
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
