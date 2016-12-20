package com.application.chetna_priya.exo_audio.model;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;

import com.application.chetna_priya.exo_audio.entity.Podcast;

import java.util.Iterator;

/**
 * Created by chetna_priya on 11/1/2016.
 */

public interface MediaProviderSource {

    String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    String CUSTOM_METADATA_EPISODE_TRACK_SUMMARY = "__SUMMARY__";
    String CUSTOM_METADATA_PODCAST_SUMMARY = "__PODSUMMARY__";
    String CUSTOM_METADATA_PODCASTID = "__PODID__";
    Iterator<MediaMetadataCompat> iterator(Context context);
    Iterator<Podcast> albumsIterator();
}
