package com.application.chetna_priya.exo_audio.Utils;

import com.application.chetna_priya.exo_audio.Entity.Genre;
import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chetna_priya on 12/3/2016.
 */

public  class GenreHelper {

    final String ARTS = "Arts";
    final String COMEDY = "Comedy";
    final String EDUCATION = "Education";
    final String KIDS_FAMILY= "Kids & Family";
    final String HEALTH = "Health";
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
    final String GOVERNMENT_ORIGANIZATION = "Government & Organization";

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


}
