package com.application.chetna_priya.exo_audio.Model;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by chetna_priya on 10/27/2016.
 */
//TODO Complete implementation
public class PodcastProvider {

    private static final String TAG = PodcastProvider.class.getSimpleName();

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile State mCurrentState = State.NON_INITIALIZED;

    // Categorized caches for music track data:
    private ConcurrentMap<String, List<MediaMetadataCompat>> mPodcastListByGenre;
    private final ConcurrentMap<String, MutableMediaMetadata> mPodcastListById;
    private final Set<String> mFavoriteTracks;
    private MediaProviderSource mSource;

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }
    public PodcastProvider() {
        this(new RemoteJsonSource());
    }

    public PodcastProvider(MediaProviderSource source) {

        mSource = source;
        mPodcastListByGenre = new ConcurrentHashMap<>();
        mPodcastListById = new ConcurrentHashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    public Iterable<String> getGenres() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        return mPodcastListByGenre.keySet();
    }

    public MediaMetadataCompat getPodcast(String currentPlayingId) {
        //TODO Create a proper implementation here, dummy implementation for now
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "1234")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Comedy Bang Bang")
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, "Comedy")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 3600000 )
                .build();
    }

    public Iterable<MediaMetadataCompat> getPodcastsByGenre(String categoryValue) {
        return null;
    }

    public void updatePodcastArt(String podcastId, Bitmap bitmap, Bitmap icon) {

    }


    /**
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     */
    public void retrieveMediaAsync(final Callback callback) {
        Log.d(TAG, "retrieveMediaAsync called");
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately
                callback.onMusicCatalogReady(true);
            }
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, State>() {
            @Override
            protected State doInBackground(Void... params) {
                retrieveMedia();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(State current) {
                if (callback != null) {
                    callback.onMusicCatalogReady(current == State.INITIALIZED);
                }
            }
        }.execute();
    }

    private synchronized void buildListsByGenre() {
        ConcurrentMap<String, List<MediaMetadataCompat>> newMusicListByGenre = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mPodcastListById.values()) {
            String genre = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
            List<MediaMetadataCompat> list = newMusicListByGenre.get(genre);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByGenre.put(genre, list);
            }
            list.add(m.metadata);
        }
        mPodcastListByGenre = newMusicListByGenre;
    }

    private synchronized void retrieveMedia() {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;

                Iterator<MediaMetadataCompat> tracks = mSource.iterator();
                while (tracks.hasNext()) {
                    MediaMetadataCompat item = tracks.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mPodcastListById.put(musicId, new MutableMediaMetadata(musicId, item));
                }
                buildListsByGenre();
                mCurrentState = State.INITIALIZED;
            }
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                // Something bad happened, so we reset state to NON_INITIALIZED to allow
                // retries (eg if the network connection is temporary unavailable)
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }
}
