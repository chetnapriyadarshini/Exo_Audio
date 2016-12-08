package com.application.chetna_priya.exo_audio.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.application.chetna_priya.exo_audio.Entity.Genre;
import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;
import java.util.List;


public  class GenreHelper {

    private final String ARTS = "Arts";
    private final String COMEDY = "Comedy";
    private final String EDUCATION = "Education";
    private final String KIDS_FAMILY= "Kids & Family";
    private final String HEALTH = "Health";
    final String TV_FILM = "TV & Film";
    final String MUSIC = "Music";
    final String NEWS_POLITICS = "News & Politics";
    final String RELIGION_SPIRITUALITY = "Religion & Spirituality";
    final String SCIENCE_MEDICINE = "Science & Medicine";
    final String SPORTS_RECREATION = "Sports & Recreation";
    final String TECHNOLOGY = "Technology";
    final String BUSINESS = "Business";
    final String GAMES_HOBBIES = "Games & Hobbies";
    final String SOCIETY_CULTURE = "Society & Culture";
    private final String GOVERNMENT_ORIGANIZATION = "Government & Organization";
    public static final String TOP_PODCASTS = "Top Podcasts";

    final int resId = R.drawable.ic_launcher;

    public ArrayList<Genre> getGenreList(){
        ArrayList<Genre> genreArrayList = new ArrayList<>();

        int TOTAL_GENRE = 16;

        for(int i = 0; i< TOTAL_GENRE; i++){
            Genre genre = new Genre(getGenre(i), getGenreImage(i));
            genreArrayList.add(genre);
        }

        return  genreArrayList;
    }

    private int getGenreImage(int position) {
        return resId;
    }

    private String getGenre(int position){
        switch (position){
            case 0:
                return ARTS;
            case 1:
                return COMEDY;
            case 2:
                return EDUCATION;
            case 3:
                return KIDS_FAMILY;
            case 4:
                return HEALTH;
            case 5:
                return TV_FILM;
            case 6:
                return MUSIC;
            case 7:
                return NEWS_POLITICS;
            case 8:
                return RELIGION_SPIRITUALITY;
            case 9:
                return SCIENCE_MEDICINE;
            case 10:
                return SPORTS_RECREATION;
            case 11:
                return TECHNOLOGY;
            case 12:
                return BUSINESS;
            case 13:
                return GAMES_HOBBIES;
            case 14:
                return SOCIETY_CULTURE;
            case 15:
                return GOVERNMENT_ORIGANIZATION;
        }
        return null;
    }


    public static String getGenreUrl(String category, Context context) {
        int limit = context.getResources().getInteger(R.integer.num_albums);
        String limitParam = "limit";
        switch (category){
            case TOP_PODCASTS:
                String BASE_URI  =  "https://itunes.apple.com/us/rss/toppodcasts";
                Uri.Builder uriBuilder = Uri.parse(BASE_URI).buildUpon();
                uriBuilder.appendQueryParameter(limitParam, String.valueOf(limit));
                uriBuilder.appendPath("xml");
                return uriBuilder.build().toString();
        }

        return null;
    }
}
