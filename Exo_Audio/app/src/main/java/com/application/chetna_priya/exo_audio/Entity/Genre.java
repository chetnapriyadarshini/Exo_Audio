package com.application.chetna_priya.exo_audio.Entity;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.util.ArrayList;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by chetna_priya on 11/23/2016.
 */

public class Genre {

    private String genre_desc;
    private int genre_icon;

    public Genre(String genre_desc, int genre_icon){
        this.genre_desc = genre_desc;
        this.genre_icon = genre_icon;
    }


    public String getGenre_desc() {
        return genre_desc;
    }

    public int getGenre_icon() {
        return genre_icon;
    }

    /*
    @Retention(SOURCE)
    @StringDef({ARTS,
            COMEDY,
            EDUCATION,
            KIDS_FAMILY,
            HEALTH,
            TV_FILM,
            MUSIC,
            NEWS_POLITICS,
            RELIGION_SPIRITUALITY,
            SCIENCE_MEDICINE,
            SPORTS_RECREATION,
            TECHNOLOGY,
            BUSINESS,
            GAMES_HOBBIES,
            SOCIETY_CULTURE,
            GOVERNMENT_ORIGANIZATION})
    public @interface GenreCategory {}*/


    /*public String getGenre(int position){
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
    }*/


    public static int getItemCount() {
        return 16;
    }

}