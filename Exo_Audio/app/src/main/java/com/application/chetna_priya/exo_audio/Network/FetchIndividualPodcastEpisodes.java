package com.application.chetna_priya.exo_audio.network;

import com.application.chetna_priya.exo_audio.entity.Episode;
import com.application.chetna_priya.exo_audio.entity.Podcast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchIndividualPodcastEpisodes {

    final String TAG = FetchIndividualPodcastEpisodes.class.getSimpleName();

    public ArrayList<Episode> load(Podcast podcast) {

        //for(int i=0; i<podList.size();i++)
        {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(new URL(podcast.getTrackUri())).build();
                Response response = client.newCall(request).execute();
                // Log.d(TAG, "Returninggggggg somethinggggggggggg");
                return new FeedParser().parseEpisodes(response.body().byteStream(), podcast);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        //  Log.d(TAG, "Returning nulllllllllllll");
        return null;
    }

}
