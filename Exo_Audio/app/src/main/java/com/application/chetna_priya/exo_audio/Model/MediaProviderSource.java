package com.application.chetna_priya.exo_audio.Model;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;

import com.application.chetna_priya.exo_audio.Entity.Podcast;

import java.util.Iterator;

/**
 * Created by chetna_priya on 11/1/2016.
 */

public interface MediaProviderSource {

    String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    Iterator<MediaMetadataCompat> iterator(Context context);
    Iterator<Podcast> albumsIterator();
}
