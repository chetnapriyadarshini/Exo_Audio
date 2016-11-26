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
        long duration = convertStringDurationToMs(episode.getEpisode_duration());
        ///*Long.parseLong(episode.getEpisode_duration())**/1000;//ms
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

    private long convertStringDurationToMs(String episode_duration) {
        Log.d(TAG, "RECEIVEDDDDDD episode_duration for parsingggggggg "+episode_duration);

        /*
        The tag can be formatted HH:MM:SS, H:MM:SS, MM:SS, or M:SS (H = hours, M = minutes, S = seconds).
        If an integer is provided (no colon present), the value is assumed to be in seconds.
        If one colon is present, the number to the left is assumed to be minutes,
        and the number to the right is assumed to be seconds.
        If more than two colons are present, the numbers furthest to the right are ignored.
         */

        if(!episode_duration.contains(":")){
            /*
            Accounts for condition: If an integer is provided (no colon present), the value is assumed to be in seconds.
             */
            return Long.parseLong(episode_duration)*1000;
        }

        long duration = 0;
        String[] durArr = episode_duration.split(":");

        long hr =  0;
        long min = 0;
        long sec = 0;
        int currIndex = 0;

        if(durArr.length == 3) {
            hr = Long.parseLong(durArr[currIndex]) * 60 * 60 * 1000;//in msecs
            currIndex++;
        }


        if(durArr.length >= 2) {
            min = Long.parseLong(durArr[currIndex]) * 60 * 1000;//in msecs
            currIndex++;
        }

        sec = Long.parseLong(durArr[currIndex])*1000;//in msecs

        duration = hr + min + sec;

        Log.d(TAG, "Returning durationnnnnnnnnn "+duration);

        return duration;
    }

}
