package com.application.chetna_priya.exo_audio.TaskLoaders;

import com.application.chetna_priya.exo_audio.Entity.Episode;
import com.application.chetna_priya.exo_audio.Entity.Podcast;
import com.application.chetna_priya.exo_audio.Network.FeedParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chetna_priya on 11/22/2016.
 */
public class FetchIndividualPodcastEpisodes {

    public ArrayList<Episode> load(ArrayList<Podcast> podList){

        for(int i=0; i<podList.size();i++) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(new URL(podList.get(i).getTrackUri())).build();
                Response response = client.newCall(request).execute();
                return new FeedParser().parseEpisodes(response.body().byteStream(), podList.get(i));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
