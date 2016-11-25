package com.application.chetna_priya.exo_audio.Model;

import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.Entity.Episode;
import com.application.chetna_priya.exo_audio.Entity.Podcast;
import com.application.chetna_priya.exo_audio.Network.FetchIndividualPodcastEpisodes;
import com.application.chetna_priya.exo_audio.Network.LoadAvailablePodcastChannels;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class RemoteJsonSource implements MediaProviderSource {

    private static final String JSON_PODCAST = "podcast";
    //private static final String JSON_TITLE = "title";//check
    private static final String JSON_ALBUM = "album";//
    private static final String JSON_ARTIST = "artist";//check
    private static final String JSON_GENRE = "genre";//check
    private static final String JSON_SOURCE = "source";//check
    private static final String JSON_IMAGE = "image";//check
    private static final String JSON_TRACK_NUMBER = "trackNumber";//check
    private static final String JSON_TOTAL_TRACK_COUNT = "totalTrackCount";//check
    private static final String JSON_TRACK_URI = "track_uri";
   // private static final String JSON_DURATION = "duration";



    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        try {
            ArrayList<Podcast> podcastChannelLists = new LoadAvailablePodcastChannels().load();
            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            ArrayList<Episode> episodeList = new FetchIndividualPodcastEpisodes().load(podcastChannelLists);
            for(int i=0; i<episodeList.size();i++){
                tracks.add(buildFromPodcastEntity(episodeList.get(i)));
            }
            return tracks.iterator();
        } catch (Exception e) {
            Log.e(TAG, "Could not retrieve music list \n" + e);
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }

    private MediaMetadataCompat buildFromPodcastEntity(Episode episode) throws JSONException {

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.
        //noinspection ResourceType
        long duration = /*Long.parseLong(episode.getEpisode_duration())**/1000;//ms
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, episode.getId())
                .putString(MediaProviderSource.CUSTOM_METADATA_TRACK_SOURCE, episode.getEpisode_pod_link())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, episode.getPodcast().getAlbum_title())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, episode.getPodcast().getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, episode.getPodcast().getGenre())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, episode.getPodcast().getArtwork_uri())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.getEpisode_title())
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, episode.getPodcast().getTrackNumber())
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, episode.getPodcast().getTotalTrackCount())
                .build();
    }

}
