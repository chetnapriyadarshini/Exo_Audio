package com.application.chetna_priya.exo_audio.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.entity.Podcast;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.ui.BaseActivity;
import com.application.chetna_priya.exo_audio.utils.MediaIDHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.application.chetna_priya.exo_audio.utils.MediaIDHelper.MEDIA_ID_PODCASTS_BY_GENRE;
import static com.application.chetna_priya.exo_audio.utils.MediaIDHelper.MEDIA_ID_PODCASTS_BY_GENRE_AND_CHANNEL_NAME;
import static com.application.chetna_priya.exo_audio.utils.MediaIDHelper.MEDIA_ID_ROOT;
import static com.application.chetna_priya.exo_audio.utils.MediaIDHelper.createMediaID;

public class PodcastProvider {

    private static final String TAG = PodcastProvider.class.getSimpleName();

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile State mCurrentState = State.NON_INITIALIZED;
    // Categorized caches for music track data:
    private ConcurrentMap<String, List<Podcast>> mPodcastListByGenre;
    private ConcurrentMap<String, List<MediaMetadataCompat>> mPodcastListByChannelName;
    private final ConcurrentMap<String, MutableMediaMetadata> mPodcastListById;
    private final Set<String> mFavoriteTracks;
    private MediaProviderSource mSource;

    public interface Callback {
        void onPodcastCatalogReady(boolean success);
    }
    public PodcastProvider() {
        this(new RemoteJsonSource());
    }

    public PodcastProvider(MediaProviderSource source) {
        mSource = source;
        mPodcastListByGenre = new ConcurrentHashMap<>();
        mPodcastListById = new ConcurrentHashMap<>();
        mPodcastListByChannelName = new ConcurrentHashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    public Iterable<String> getGenres() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        return mPodcastListByGenre.keySet();
    }
/*
    public Iterable<String> getAlbumsByGenre(String genre) {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        Log.d(TAG, "SIZEEEE RETURNINGGGGGGGG" +mPodcastListByChannelName.size());
        return mPodcastListByChannelName.keySet();
    }*/

    public Iterable<Podcast> getPodcastByGenre(String genre) {
        if (mCurrentState != State.INITIALIZED || !mPodcastListByGenre.containsKey(genre)) {
            return Collections.emptyList();
        }
        return mPodcastListByGenre.get(genre);
    }

    public Iterable<MediaMetadataCompat> getEpisodeByChannelName(String podcastName) {
        if (mCurrentState != State.INITIALIZED || !mPodcastListByChannelName.containsKey(podcastName)) {
            return Collections.emptyList();
        }
        return mPodcastListByChannelName.get(podcastName);
    }
/*
    *//**
     * Very basic implementation of a search that filter music tracks with title containing
     * the given query.
     *
     */
    public Iterable<MediaMetadataCompat> searchPodcastByEpisodeTitle(String query) {
        return searchPodcast(MediaMetadataCompat.METADATA_KEY_TITLE, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with album containing
     * the given query.
     *
     */
    public Iterable<MediaMetadataCompat> searchPodcastByAlbum(String query) {
        return searchPodcast(MediaMetadataCompat.METADATA_KEY_ALBUM, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with artist containing
     * the given query.
     *
     */
    public Iterable<MediaMetadataCompat> searchPodcastByArtist(String query) {
        return searchPodcast(MediaMetadataCompat.METADATA_KEY_ARTIST, query);
    }

    Iterable<MediaMetadataCompat> searchPodcast(String metadataField, String query) {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        ArrayList<MediaMetadataCompat> result = new ArrayList<>();
        query = query.toLowerCase(Locale.US);
        for (MutableMediaMetadata track : mPodcastListById.values()) {
            if (track.metadata.getString(metadataField).toLowerCase(Locale.US)
                    .contains(query)) {
                result.add(track.metadata);
            }
        }
        return result;
    }

    /**
     * Return the MediaMetadataCompat for the given musicID.
     *
     * @param podcastId The unique, non-hierarchical music ID.
     */
    public MediaMetadataCompat getPodcast(String podcastId) {
        return mPodcastListById.containsKey(podcastId) ? mPodcastListById.get(podcastId).metadata : null;
    }

    /**
     * Get an iterator over a shuffled collection of all songs
     */
    public Iterable<MediaMetadataCompat> getShuffledPodcast() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mPodcastListById.size());
        for (MutableMediaMetadata mutableMetadata: mPodcastListById.values()) {
            shuffled.add(mutableMetadata.metadata);
        }
        Collections.shuffle(shuffled);
        return shuffled;
    }

    public synchronized void updatePodcastArt(String podcastId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getPodcast(podcastId);
        metadata = new MediaMetadataCompat.Builder(metadata)

                // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                // example, on the lockscreen background when the media session is active.
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)

                // set small version of the album art in the DISPLAY_ICON. This is used on
                // the MediaDescription and thus it should be small to be serialized if
                // necessary
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)

                .build();

        MutableMediaMetadata mutableMetadata = mPodcastListById.get(podcastId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata = metadata;
    }

    public synchronized void updatePodcastDuration(String podcastId, long duration){
        MediaMetadataCompat metadata = getPodcast(podcastId);
        metadata = new MediaMetadataCompat.Builder(metadata)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
        .build();
        MutableMediaMetadata mutableMetadata = mPodcastListById.get(podcastId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata = metadata;
    }

    public void setFavorite(String podcastId, boolean favorite) {
        if (favorite) {
            mFavoriteTracks.add(podcastId);
        } else {
            mFavoriteTracks.remove(podcastId);
        }
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public boolean isFavorite(String podcastId) {
        return mFavoriteTracks.contains(podcastId);
    }

    /**
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     */
    public void retrieveMediaAsync(final Callback callback, final Context context) {
        Log.d(TAG, "retrieveMediaAsync called");
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately
                callback.onPodcastCatalogReady(true);
            }
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, State>() {
            @Override
            protected State doInBackground(Void... params) {
                retrieveMedia(context);
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(State current) {
                if (callback != null) {
                    callback.onPodcastCatalogReady(current == State.INITIALIZED);
                }
            }
        }.execute();
    }

    private synchronized void buildListsByGenre() {

        ConcurrentMap<String, List<Podcast>> newPodcastListByGenre = new ConcurrentHashMap<>();

        Iterator<Podcast> tracks = mSource.albumsIterator();
        while (tracks.hasNext()){
            Podcast podcast = tracks.next();
            List<Podcast> list = newPodcastListByGenre.get(podcast.getGenre());
            if (list == null) {
                list = new ArrayList<>();
                newPodcastListByGenre.put(podcast.getGenre(), list);
            }
            list.add(podcast);
        }
        mPodcastListByGenre = newPodcastListByGenre;
/*
        for (MutableMediaMetadata m : mPodcastListById.values()) {
            String genre = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
            List<MediaMetadataCompat> list = newPodcastListByGenre.get(genre);
            if (list == null) {
                list = new ArrayList<>();
                newPodcastListByGenre.put(genre, list);
            }
            list.add(m.metadata);
        }
        mPodcastListByGenre = newPodcastListByGenre;*/
    }

    private synchronized void buildListsByChannelName() {
        ConcurrentMap<String, List<MediaMetadataCompat>> newPodcastListByChannelName = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mPodcastListById.values()) {
            String podcastName = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
            List<MediaMetadataCompat> list = newPodcastListByChannelName.get(podcastName);
            if (list == null) {
                list = new ArrayList<>();
                newPodcastListByChannelName.put(podcastName, list);
            }
            list.add(m.metadata);
        }
        mPodcastListByChannelName = newPodcastListByChannelName;
    }

    private synchronized void retrieveMedia(Context context) {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;

                Iterator<MediaMetadataCompat> tracks = mSource.iterator(context);
                while (tracks.hasNext()) {
                    MediaMetadataCompat item = tracks.next();
                    String podcastId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mPodcastListById.put(podcastId, new MutableMediaMetadata(podcastId, item));
                }
                buildListsByGenre();
                buildListsByChannelName();
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

    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId, Resources resources) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (!MediaIDHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        Log.d(TAG, "MEDIA IDD RECEIVEDDDD "+mediaId);
        /*
        Log.d(TAG, MediaIDHelper.getParentMediaID(mediaId).concat(MEDIA_ID_PODCASTS_BY_GENRE_AND_CHANNEL_NAME));*/
        if (MEDIA_ID_ROOT.equals(mediaId)) {
            mediaItems.add(createBrowsableMediaItemForRoot(resources));

        }else if (MEDIA_ID_PODCASTS_BY_GENRE.equals(mediaId)) {
            for (String genre : getGenres()) {
                mediaItems.add(createBrowsableMediaItemForGenre(genre, resources));
            }
        }else if(mediaId.startsWith(MEDIA_ID_PODCASTS_BY_GENRE)) {//return podcast list by channel
            String genre = MediaIDHelper.getHierarchy(mediaId)[1];
            for (Podcast album : getPodcastByGenre(genre)) {
              //  Log.d(TAG, "For Genreeeeee "+genre+" addingggggggg "+album.getAlbum_title());
                mediaItems.add(createBrowsableMediaItemForPodcast(album, resources));
            }
        }else if (mediaId.startsWith(MEDIA_ID_PODCASTS_BY_GENRE_AND_CHANNEL_NAME)) {
            String channel = MediaIDHelper.getHierarchy(mediaId)[1];
            Log.d(TAG, "Channneeeeeeeel  "+channel);
            for (MediaMetadataCompat metadata : getEpisodeByChannelName(channel)) {
                mediaItems.add(createMediaItem(metadata));
            }
        } else {
            Log.w(TAG, "Skipping unmatched mediaId: "+ mediaId);
        }
        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForRoot(Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(MEDIA_ID_PODCASTS_BY_GENRE)
                .setTitle(resources.getString(R.string.browse_genres))
                .setSubtitle(resources.getString(R.string.browse_genre_subtitle))
                /*.setIconUri(Uri.parse("android.resource://" +
                        "com.example.android.uamp/drawable/ic_by_genre"))
               */ .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForGenre(String genre,
                                                                          Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_PODCASTS_BY_GENRE, genre))
                .setTitle(genre)
                .setSubtitle(resources.getString(
                        R.string.browse_podcast_by_genre_subtitle, genre))
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForPodcast(Podcast album,
                                                                          Resources resources) {
      //  String albumTitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
        Bundle bundle = new Bundle();
        bundle.putString(BaseActivity.EXTRA_SUMMARY, album.getSummary());
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_PODCASTS_BY_GENRE_AND_CHANNEL_NAME,
                        album.getAlbum_title()))
                .setTitle(album.getAlbum_title())
                .setIconUri(Uri.parse(album.getArtwork_uri()))
                .setSubtitle(album.getArtist())
                .setExtras(bundle)
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)
        String channel = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
        Bundle bundle = new Bundle(metadata.getBundle());
        /*bundle.putString(BaseActivity.EXTRA_EPISODE_PUBLISHED_DATE, metadata.getString(MediaMetadataCompat.METADATA_KEY_DATE));*/

        String hierarchyAwareMediaID = createMediaID(
                metadata.getDescription().getMediaId(), MEDIA_ID_PODCASTS_BY_GENRE_AND_CHANNEL_NAME, channel);
       /* MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .build();
*/
        /*
        TODO Commented the above way for creating media id for now, will have to check for repurcussions
        */
        MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat.Builder()
                .setMediaId(hierarchyAwareMediaID)/*
                .setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))*/
                .setExtras(bundle).build();

      //  Log.d(TAG, "Media Item created!!!!!");
        return new MediaBrowserCompat.MediaItem(mediaDescriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }


}
