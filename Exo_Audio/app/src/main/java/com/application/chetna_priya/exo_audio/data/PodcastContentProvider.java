package com.application.chetna_priya.exo_audio.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by chetna_priya on 11/10/2016.
 */

public class PodcastContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int PODCAST_ALBUM = 100;
    private static final int PODCAST_EPISODE = 200;
    public static final int PODCAST_EPISODES_WITH_ALBUM_TRACK_ID = 201;
    private static final int PODCAST_EPISODE_WITH_ALBUM_KEY_AND_DATE = 202;
    private static final String TAG = PodcastContentProvider.class.getSimpleName();
    private PodcastDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sEpisodebyPodcastAlbumQueryBuilder;

    static{
        sEpisodebyPodcastAlbumQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sEpisodebyPodcastAlbumQueryBuilder.setTables(
                PodcastContract.EpisodeEntry.TABLE_NAME + " INNER JOIN " +
                        PodcastContract.PodcastEntry.TABLE_NAME +
                        " ON " + PodcastContract.EpisodeEntry.TABLE_NAME +
                        "." + PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_KEY +
                        " = " + PodcastContract.PodcastEntry.TABLE_NAME +
                        "." + PodcastContract.PodcastEntry._ID);
    }

    private static final String sPodcastEpisodeByTrackIdselection =
            PodcastContract.PodcastEntry.TABLE_NAME+
                    "." + PodcastContract.PodcastEntry.COLUMN_PODCAST_TRACK_ID + " = ? ";

    private static final String sPodcastAlbumKeyAndReleaseDateSelection =
            PodcastContract.EpisodeEntry.TABLE_NAME +
                    "." + PodcastContract.EpisodeEntry.COLUMN_PODCAST_ALBUM_KEY + " = ? AND " +
                    PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE + " = ? ";


    private Cursor getEpisodesByAlbumTrackIdSetting(Uri uri, String[] projection, String sortOrder) {
        String trackId = PodcastContract.EpisodeEntry.getPodcastTrackIdFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sPodcastEpisodeByTrackIdselection;
        selectionArgs = new String[]{trackId};

        return sEpisodebyPodcastAlbumQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEpisodeByAlbumIDAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String albumId = PodcastContract.EpisodeEntry.getPodcastIdFromUri(uri);
        String date = PodcastContract.EpisodeEntry.getDateFromUri(uri);
       // String title = PodcastContract.EpisodeEntry.getTitleFromUri(uri);

        return sEpisodebyPodcastAlbumQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sPodcastAlbumKeyAndReleaseDateSelection,
                new String[]{albumId, date},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PodcastDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "episode/*/*"
            case PODCAST_EPISODE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PodcastContract.EpisodeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // "episode/*"
            case PODCAST_EPISODES_WITH_ALBUM_TRACK_ID: {
                retCursor = getEpisodesByAlbumTrackIdSetting(uri, projection, sortOrder);
                break;
            }

            case PODCAST_EPISODE_WITH_ALBUM_KEY_AND_DATE: {
                retCursor = getEpisodeByAlbumIDAndDate(uri, projection,sortOrder);
                break;
            }
            // "podacst"
            case PODCAST_ALBUM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PodcastContract.PodcastEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case PODCAST_EPISODE_WITH_ALBUM_KEY_AND_DATE:
                return PodcastContract.EpisodeEntry.CONTENT_ITEM_TYPE;
            case PODCAST_EPISODES_WITH_ALBUM_TRACK_ID:
                return PodcastContract.EpisodeEntry.CONTENT_TYPE;
            case PODCAST_ALBUM:
                return PodcastContract.PodcastEntry.CONTENT_TYPE;
            case PODCAST_EPISODE:
                return PodcastContract.EpisodeEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "INSERTINNNNNNNNNNNNNNNNNNNNNNNNNNN ");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case PODCAST_ALBUM: {
             //   normalizeDate(values);
                long _id = db.insert(PodcastContract.PodcastEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PodcastContract.PodcastEntry.buildPodcastUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PODCAST_EPISODE: {
                long _id = db.insert(PodcastContract.EpisodeEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PodcastContract.EpisodeEntry.buildPodcastEpisodeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case PODCAST_EPISODE:
                rowsDeleted = db.delete(
                        PodcastContract.EpisodeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PODCAST_ALBUM:
                rowsDeleted = db.delete(
                        PodcastContract.PodcastEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PODCAST_EPISODE:
                normalizeDate(values);
                rowsUpdated = db.update(PodcastContract.EpisodeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PODCAST_ALBUM:
                rowsUpdated = db.update(PodcastContract.PodcastEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PODCAST_EPISODE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(PodcastContract.EpisodeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PodcastContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PodcastContract.PATH_PODCAST_ALBUM, PODCAST_ALBUM);
        matcher.addURI(authority, PodcastContract.PATH_PODCAST_EPISODE, PODCAST_EPISODE);
        matcher.addURI(authority, PodcastContract.PATH_PODCAST_EPISODE + "/*", PODCAST_EPISODES_WITH_ALBUM_TRACK_ID);
        matcher.addURI(authority, PodcastContract.PATH_PODCAST_EPISODE + "/*/*", PODCAST_EPISODE_WITH_ALBUM_KEY_AND_DATE);
        return matcher;
    }


    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE)) {
            long dateValue = values.getAsLong(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE);
            values.put(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE,
                    PodcastContract.normalizeDate(dateValue));
        }
    }


    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
