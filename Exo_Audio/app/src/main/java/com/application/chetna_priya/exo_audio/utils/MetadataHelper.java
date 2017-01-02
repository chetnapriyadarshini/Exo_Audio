package com.application.chetna_priya.exo_audio.utils;

import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;

import com.application.chetna_priya.exo_audio.entity.MetadataEntity;
import com.application.chetna_priya.exo_audio.model.MediaProviderSource;

/**
 * Created by chetna_priya on 1/1/2017.
 */

public class MetadataHelper {

    public static MediaMetadataCompat buildMetadataFromEntity(MetadataEntity metadataEntity, Bitmap metadataBitmap) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, metadataEntity.getMetadataMediaId())
                .putString(MediaProviderSource.CUSTOM_METADATA_TRACK_SOURCE, metadataEntity.getLink())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, metadataEntity.getMetadataDuration())
                .putString(MediaMetadataCompat.METADATA_KEY_DATE, metadataEntity.getReleaseDate())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, metadataBitmap)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, metadataEntity.getMetadataTitle())
                .build();
    }
}
