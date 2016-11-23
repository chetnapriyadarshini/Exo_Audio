package com.application.chetna_priya.exo_audio.TaskLoaders;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.application.chetna_priya.exo_audio.Entity.Genre;
import com.application.chetna_priya.exo_audio.Entity.Podcast;
import com.application.chetna_priya.exo_audio.Network.FeedParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chetna_priya on 11/22/2016.
 */

public class LoadAvailablePodcastChannels{

    private static final String TAG = LoadAvailablePodcastChannels.class.getSimpleName();
    private OkHttpClient client;

    public ArrayList<Podcast> load(){
        client = new OkHttpClient();

        final String ITUNES_BASE_URL = "https://itunes.apple.com/search?";
        final String TERM_PARAM = "term";
        final String MEDIA_PARAM = "media";
        final String mediaVal = "podcast";
        final String termVal = "comedy";

        try {
            Uri.Builder uriBuilder = Uri.parse(ITUNES_BASE_URL).buildUpon();
            uriBuilder.appendQueryParameter(MEDIA_PARAM, mediaVal);
            uriBuilder.appendQueryParameter(TERM_PARAM, termVal);


            Uri builtUri = uriBuilder.build();
            String url = builtUri.toString();

            Log.d(TAG, "URLLLLLLLLLLL "+url);

            Request request = new Request.Builder()
                    .url(new URL(url)).build();

            Response response = client.newCall(request).execute();

            return  parseFeedUrlFromJSON(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally {
            client = null;
        }
        return null;
    }

    private ArrayList<Podcast> parseFeedUrlFromJSON(String responseJSONString) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(responseJSONString);
        final String OWM_RESULTS = "results";
        final String OWM_FEED_URL = "feedUrl";
        final String OWM_TRACK_ID = "trackId";
        final String OWM_ALBUM = "trackName";
        final String OWM_ARTIST="artistName";
        final String OWM_ARTWORK_URI="artworkUrl100";
        final String OWM_TOTAL_TRACKS="trackCount";
        final String OWM_GENRE= "primaryGenreName";

        JSONArray resultArray = jsonObject.getJSONArray(OWM_RESULTS);
        final FeedParser feedParser = new FeedParser();
        ArrayList<Podcast> podcastList = new ArrayList<>();
        for(int i = 0; i<resultArray.length(); i++)
        {
            long trackId = -1;
            String album =null;
            String artist=null;
            String artWorkUri = null;
            int totalTracks = -1;
            String genre=null;

            if(resultArray.getJSONObject(i).has(OWM_TRACK_ID)){
                trackId = (long) resultArray.getJSONObject(i).get(OWM_TRACK_ID);
            }
            if(resultArray.getJSONObject(i).has(OWM_ALBUM)){
                album = (String) resultArray.getJSONObject(i).get(OWM_ALBUM);
            }
            if(resultArray.getJSONObject(i).has(OWM_ARTIST)){
                artist = (String) resultArray.getJSONObject(i).get(OWM_ARTIST);
            }
            if(resultArray.getJSONObject(i).has(OWM_ARTWORK_URI)){
                artWorkUri = (String) resultArray.getJSONObject(i).get(OWM_ARTWORK_URI);
            }
            if(resultArray.getJSONObject(i).has(OWM_GENRE)){
                genre = (String) resultArray.getJSONObject(i).get(OWM_GENRE);
            }
            if(resultArray.getJSONObject(i).has(OWM_TOTAL_TRACKS)){
                totalTracks = (int) resultArray.getJSONObject(i).get(OWM_TOTAL_TRACKS);
            }
            if(resultArray.getJSONObject(i).has(OWM_FEED_URL)) {
                final String feedObj = (String) resultArray.getJSONObject(i).get(OWM_FEED_URL);
                Log.d(TAG, feedObj);
/*
                Uri builtUri = Uri.parse(feedObj).buildUpon().build();
                String url = builtUri.toString();

                Request request = new Request.Builder().url(new URL(url)).build();

                Response response = client.newCall(request).execute();*/
                /*Podcast podcast  = feedParser.parsePodcast(response.body().byteStream(), trackId,
                        feedObj);
                *//*if(podcast != null)
                    podcastList.add(podcast);*/
                Podcast podcast = new Podcast(trackId,album,feedObj,artist,genre,totalTracks,artWorkUri);
                podcastList.add(podcast);
            }
        }
        return podcastList;
    }
}