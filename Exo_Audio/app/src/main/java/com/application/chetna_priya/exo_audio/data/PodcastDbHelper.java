package com.application.chetna_priya.exo_audio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.chetna_priya.exo_audio.data.PodcastContract.PodcastEntry;
import com.application.chetna_priya.exo_audio.data.PodcastContract.EpisodeEntry;


public class PodcastDbHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "podcast.db";

    public PodcastDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_PODCAST_ALBUM_TABLE = "CREATE TABLE " + PodcastEntry.TABLE_NAME + " (" +
                PodcastEntry._ID + " INTEGER PRIMARY KEY," +
                PodcastEntry.COLUMN_PODCAST_TRACK_ID + " REAL NOT NULL, " +
                PodcastEntry.COLUMN_PODCAST_TITLE + " TEXT NOT NULL, " +
                PodcastEntry.COLUMN_PODCAST_SUMMARY + " TEXT " +
                " );";

        final String SQL_CREATE_ALBUM_EPISODE_TABLE = "CREATE TABLE " + EpisodeEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                EpisodeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the album entry associated with this podcast data
                EpisodeEntry.COLUMN_PODCAST_ALBUM_KEY + " INTEGER NOT NULL, " +
                EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE + " TEXT NOT NULL, " +
                EpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE + " TEXT NOT NULL," +
                EpisodeEntry.COLUMN_PODCAST_EPISODE_SUMMARY + " TEXT, " +

                EpisodeEntry.COLUMN_PODCAST_EPISODE_DURATION + " REAL NOT NULL, " +
                EpisodeEntry.COLUMN_PODCAST_EPISODE_LINK + " TEXT NOT NULL, " +
                EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID + " TEXT NOT NULL, " +
                EpisodeEntry.COLUMN_PODCAST_EPISODE_NAME + " TEXT NOT NULL, " +
                EpisodeEntry.COLUMN_PODCAST_ALBUM_COVER_IMAGE + " BLOB NOT NULL, " +/*
                EpisodeEntry.COLUMN_PODCAST_EPISODE_URI_DEVICE + " TEXT NOT NULL, " +*/

                // Set up the album column as a foreign key to episode table.
                " FOREIGN KEY (" + EpisodeEntry.COLUMN_PODCAST_ALBUM_KEY + ") REFERENCES " +
                PodcastEntry.TABLE_NAME + " (" + PodcastEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + EpisodeEntry.COLUMN_PODCAST_EPISODE_RELEASE_DATE + ", " +
                EpisodeEntry.COLUMN_PODCAST_ALBUM_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_ALBUM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ALBUM_EPISODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PodcastEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EpisodeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
