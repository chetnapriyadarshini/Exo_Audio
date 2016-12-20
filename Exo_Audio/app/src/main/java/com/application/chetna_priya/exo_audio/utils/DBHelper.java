package com.application.chetna_priya.exo_audio.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.application.chetna_priya.exo_audio.data.PodcastContract;
import com.application.chetna_priya.exo_audio.model.MediaProviderSource;

public class DBHelper {

    public static Uri insertInDb(Context context, MediaBrowserCompat.MediaItem mediaItem, Bitmap iconBitmap, String epName){
        Bundle epBundle = mediaItem.getDescription().getExtras();
        if(epBundle == null)
            throw new IllegalArgumentException("Bundle sent is nulll");
        String podcastTitle = epBundle.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
        String podcastSummary = epBundle.getString(MediaProviderSource.CUSTOM_METADATA_PODCAST_SUMMARY);
        long podcastTrack = epBundle.getLong(MediaProviderSource.CUSTOM_METADATA_PODCASTID);

        //First create podcast table if not present and insert podcast
        ContentValues contentValues = new ContentValues();
        contentValues.put(PodcastContract.PodcastEntry.COLUMN_PODCAST_SUMMARY, podcastSummary);
        contentValues.put(PodcastContract.PodcastEntry.COLUMN_PODCAST_TITLE, podcastTitle);
        contentValues.put(PodcastContract.PodcastEntry.COLUMN_PODCAST_TRACK_ID, podcastTrack);

        Uri insertedUri = context.getContentResolver().insert(
                PodcastContract.PodcastEntry.CONTENT_URI,
                contentValues
        );

        long podcastId = ContentUris.parseId(insertedUri);
        /*
        We now use the above podcast id to insert an episode for this podcast
         */
        String title = epBundle.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        ContentValues episodeValues = new ContentValues();

        byte[] data = new byte[0];
        if(iconBitmap != null)
            data = BitmapHelper.getBitmapAsByteArray(iconBitmap);
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_COVER_IMAGE, data);
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE,
            title == null ? "" : title);
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE,
                epBundle.getString(MediaMetadataCompat.METADATA_KEY_DATE));
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_DURATION,
                epBundle.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_LINK,
                epBundle.getString(MediaProviderSource.CUSTOM_METADATA_TRACK_SOURCE));
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_SUMMARY,
                epBundle.getString(MediaProviderSource.CUSTOM_METADATA_EPISODE_TRACK_SUMMARY));
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_KEY, podcastId);
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID, mediaItem.getMediaId());
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_NAME, epName);
        /*
        episodeValues.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_URI_DEVICE, localUri);*/

        return context.getContentResolver().insert(
                PodcastContract.EpisodeEntry.CONTENT_URI,
                episodeValues);

    }
}
