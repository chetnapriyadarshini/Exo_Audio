package com.application.chetna_priya.exo_audio.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.application.chetna_priya.exo_audio.Entity.Genre;
import com.application.chetna_priya.exo_audio.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public  class GenreHelper {

    public static final int TOTAL_ITEM_COUNT = 13;
    private static final String ARTS = "Arts";
    private static final String COMEDY = "Comedy";
    private static final String EDUCATION = "Education";
    private static final String KIDS_FAMILY= "Kids & Family";
    private static final String HEALTH = "Health";
    private static final String TV_FILM = "TV & Film";
 //   private final String MUSIC = "Music";
    private static final String NEWS_POLITICS = "News & Politics";
    private static final String RELIGION_SPIRITUALITY = "Religion & Spirituality";
    private static final String SCIENCE_MEDICINE = "Science & Medicine";
  //  private static final String SPORTS_RECREATION = "Sports & Recreation";
    private static final String TECHNOLOGY = "Technology";
    private static final String BUSINESS = "Business";
    private static final String GAMES_HOBBIES = "Games & Hobbies";
    private static final String SOCIETY_CULTURE = "Society & Culture";
    //private static final String GOVERNMENT_ORIGANIZATION = "Government & Organization";
    public static final String TOP_PODCASTS = "Top Podcasts";

    final int resId = R.drawable.ic_launcher;

    public ArrayList<Genre> getGenreList(){
        ArrayList<Genre> genreArrayList = new ArrayList<>();

        for(int i = 0; i< TOTAL_ITEM_COUNT; i++){
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
          /*  case 6:
                return MUSIC;
          */  case 6:
                return NEWS_POLITICS;
            case 7:
                return RELIGION_SPIRITUALITY;
            case 8:
                return SCIENCE_MEDICINE;
           /* case 9:
                return SPORTS_RECREATION;
           */ case 9:
                return TECHNOLOGY;
            case 10:
                return BUSINESS;
            case 11:
                return GAMES_HOBBIES;
            case 12:
                return SOCIETY_CULTURE;
            /*case 14:
                return GOVERNMENT_ORIGANIZATION;
        */}
        return null;
    }


    public static String getGenreUrl(String category, int limit) {
        /*
        	Available Media : movie, podcast, music, musicVideo, audiobook, shortFilm, tvShow, software, ebook, all
         */
        String LIMIT_PARAM = "limit";
        Uri.Builder uriBuilder;

        if(!(category.equals(TOP_PODCASTS))) {
            final String ITUNES_BASE_URL = "https://itunes.apple.com/search?";
            final String TERM_PARAM = "term";
            final String MEDIA_PARAM = "media";
            final String podcastmediaVal = "podcast";
          //  final String musicmediaVal = "music";
          //  final String audiobookmediaVal = "audiobook";

            uriBuilder = Uri.parse(ITUNES_BASE_URL).buildUpon();
            uriBuilder.appendQueryParameter(MEDIA_PARAM, podcastmediaVal);

            //  uriBuilder.appendQueryParameter(MEDIA_PARAM, musicmediaVal);
            // uriBuilder.appendQueryParameter(MEDIA_PARAM, audiobookmediaVal);

            uriBuilder.appendQueryParameter(TERM_PARAM, category);
            /*
            This applies for cases when we want all podcasts in a cetegory
             */
            if(limit != -1)
                uriBuilder.appendQueryParameter(LIMIT_PARAM, String.valueOf(limit));


            Uri builtUri = uriBuilder.build();
            return builtUri.toString();
        }else{

            String BASE_URI  =  "https://itunes.apple.com/us/rss/toppodcasts";
            uriBuilder = Uri.parse(BASE_URI).buildUpon();
            uriBuilder.appendQueryParameter(LIMIT_PARAM, String.valueOf(limit));
            uriBuilder.appendPath("xml");
            return uriBuilder.build().toString();
        }
    }
}
