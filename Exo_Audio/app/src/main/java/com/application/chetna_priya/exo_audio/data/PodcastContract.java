package com.application.chetna_priya.exo_audio.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by chetna_priya on 11/10/2016.
 */

public class PodcastContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.application.chetna_priya.exo_audio";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_PODCAST_EPISODE = "episode";
    public static final String PATH_PODCAST_ALBUM = "podcast";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class PodcastEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PODCAST_ALBUM).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PODCAST_ALBUM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PODCAST_ALBUM;

        // Table name
        public static final String TABLE_NAME = "podcast_album";

        public static final String COLUMN_PODCAST_TITLE = "podcast_title";
        public static final String COLUMN_PODCAST_TRACK_ID = "podcast_track_id";

        public static final String COLUMN_PODCAST_SUMMARY = "podcast_summary";



        public static Uri buildPodcastUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPodcastUrifromTitle(String title){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_PODCAST_TITLE, title).build();
        }

        public static long getPodcastTrackIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class EpisodeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PODCAST_EPISODE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PODCAST_EPISODE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PODCAST_EPISODE;

        // Table name
        public static final String TABLE_NAME = "podcast_episode";
        public static final String COLUMN_PODCAST_ALBUM_KEY = "podcast_key";
        public static final String COLUMN_PODCAST_EPISODE_TITLE = "podcast_episode_title";
        public static final String COLUMN_PODCAST_EPISODE_LINK = "podcast_episode_link";
        public static final String COLUMN_PODCAST_EPISODE_SUMMARY = "podcast_episode_summary";
        public static final String COLUMN_PODCAST_EPISODE_DURATION = "podcast_episode_duration";
        public static final String COLUMN_PODCAST_EPISODE_RELEASE_DATE = "podcast_episode_release_date";
        public static final String COLUMN_PODCAST_ALBUM_COVER_IMAGE = "podcast_image";
        public static final String COLUMN_PODCAST_EPISODE_MEDIA_ID = "podcast_episode_mediaid";
        public static final String COLUMN_PODCAST_EPISODE_NAME = "podcast_episode_name";
        /*public static final String COLUMN_PODCAST_EPISODE_URI_DEVICE = "podcast_episode_location";*/

        public static final int INDEX_PODCAST_KEY = 0;
        public static final int INDEX_PODCAST_EPISODE_TITLE = 1;
        public static final int INDEX_PODCAST_EPISODE_LINK= 2;
        public static final int INDEX_PODCAST_EPISODE_SUMMARY = 3;
        public static final int INDEX_PODCAST_EPISODE_DURATION = 4;
        public static final int INDEX_PODCAST_EPISODE_RELEASE_DATE = 5;
        public static final Uri FILE_URI = Uri.parse("file://" + CONTENT_AUTHORITY);


        public static Uri buildPodcastEpisodeUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }


        public static Uri buildPodcastEpisodeFromTrackIdAndDate(long trackId, String date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(trackId))
                    .appendQueryParameter(COLUMN_PODCAST_EPISODE_RELEASE_DATE,date).build();
        }

        public static Uri buildPodcastEpisodeFromTrackId(long trackId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(trackId)).build();
        }

        public static String getPodcastIdFromUri(Uri uri){
            return uri.getPathSegments().get(0);
        }

        public static String getPodcastTrackIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }


}
