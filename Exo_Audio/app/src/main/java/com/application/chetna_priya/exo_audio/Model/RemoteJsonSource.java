package com.application.chetna_priya.exo_audio.model;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.entity.Episode;
import com.application.chetna_priya.exo_audio.entity.Podcast;
import com.application.chetna_priya.exo_audio.network.FetchIndividualPodcastEpisodes;
import com.application.chetna_priya.exo_audio.network.LoadAvailablePodcastChannels;
import com.application.chetna_priya.exo_audio.utils.GenreHelper;
import com.application.chetna_priya.exo_audio.utils.PreferenceHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;



class RemoteJsonSource implements MediaProviderSource {

    final String TAG = RemoteJsonSource.class.getSimpleName();
    private ArrayList<Podcast> podcastChannelLists = new ArrayList<>();


    @Override
    public Iterator<Podcast> albumsIterator() {
        return podcastChannelLists.iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> iterator(Context context) {
        try {
            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            ArrayList<String> genres = PreferenceHelper.getSavedGenres(context);
            Log.d(TAG, genres.toString());
            if(genres == null)
                return tracks.iterator();
            LoadAvailablePodcastChannels loadChannels = new LoadAvailablePodcastChannels();
            for(int i = 0; i< genres.size(); i++)
            {
                ArrayList<Podcast> podsublist = loadChannels.load(GenreHelper.getGenreUrl(genres.get(i)), genres.get(i));
                podcastChannelLists.addAll(podsublist);
            }


            /*
            We now load all the available episodes for all the selected channels
             */
            FetchIndividualPodcastEpisodes fetchEpisodes = new FetchIndividualPodcastEpisodes();
            ArrayList<Episode> episodeList = new ArrayList<>();
            for(int i = 0; i< podcastChannelLists.size(); i++)
            {
                ArrayList<Episode> episodeSubList = fetchEpisodes.load(podcastChannelLists.get(i));
                if(episodeSubList != null) {
                    for (int j = 0; j < episodeSubList.size(); j++) {
                        episodeList.add(episodeSubList.get(j));
                    }
                }else{
                    Log.d(TAG, "DO NOT ADDDDDDDDDDDD");
                }
            }
            /*
            Finally build the metadata from the episodes and add it to the tracks
             */
            for(int i=0; i<episodeList.size();i++) {
                MediaMetadataCompat metadata = buildFromPodcastEntity(episodeList.get(i));
                if(metadata != null)
                    tracks.add(metadata);
            }
            Log.d(TAG, "Returningggggggggg iterator -----------------");
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
        long duration;
        try {
            duration = convertStringDurationToMs(episode.getEpisode_duration());
        }catch (NumberFormatException e){
            e.printStackTrace();
            return null;
        }
        if(duration == -1)
            return null;

        ///*Long.parseLong(episode.getEpisode_duration())**/1000;//ms
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, episode.getId())
                .putString(MediaProviderSource.CUSTOM_METADATA_TRACK_SOURCE, episode.getEpisode_pod_link())
                .putString(MediaProviderSource.CUSTOM_METADATA_EPISODE_TRACK_SUMMARY, episode.getEpisode_summary())
                .putString(MediaProviderSource.CUSTOM_METADATA_PODCAST_SUMMARY, episode.getPodcast().getSummary())
                .putLong(MediaProviderSource.CUSTOM_METADATA_PODCASTID, episode.getPodcast().getTrackNumber())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, episode.getPodcast().getAlbum_title())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, episode.getPodcast().getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_DATE, episode.getEpisode_published_on())
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, episode.getPodcast().getGenre())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, episode.getPodcast().getArtwork_uri())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.getEpisode_title())
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, episode.getPodcast().getTrackNumber())
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, episode.getPodcast().getTotalTrackCount())
                .build();
    }

    private long convertStringDurationToMs(String episode_duration) {
      //  Log.d(TAG, "RECEIVEDDDDDD episode_duration for parsingggggggg "+episode_duration);

        /*
        The tag can be formatted HH:MM:SS, H:MM:SS, MM:SS, or M:SS (H = hours, M = minutes, S = seconds).
        If an integer is provided (no colon present), the value is assumed to be in seconds.
        If one colon is present, the number to the left is assumed to be minutes,
        and the number to the right is assumed to be seconds.
        If more than two colons are present, the numbers furthest to the right are ignored.
         */
        if(episode_duration == null)
            return -1;
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
            durArr[currIndex] = durArr[currIndex].trim();
            hr = Long.parseLong(durArr[currIndex]) * 60 * 60 * 1000;//in msecs
            currIndex++;
        }


        if(durArr.length >= 2) {
            durArr[currIndex] = durArr[currIndex].trim();
            min = Long.parseLong(durArr[currIndex]) * 60 * 1000;//in msecs
            currIndex++;
        }


        durArr[currIndex] = durArr[currIndex].trim();
        sec = Long.parseLong(durArr[currIndex])*1000;//in msecs

        duration = hr + min + sec;

       // Log.d(TAG, "Returning durationnnnnnnnnn "+duration);

        return duration;
    }

}
