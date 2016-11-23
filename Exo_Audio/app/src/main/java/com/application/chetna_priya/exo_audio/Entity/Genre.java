package com.application.chetna_priya.exo_audio.Entity;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by chetna_priya on 11/23/2016.
 */

public class Genre {

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
    public @interface GenreCategory {}

    public static final String ARTS = "Arts";
    public static final String COMEDY = "Comedy";
    public static final String EDUCATION = "Education";
    public static final String KIDS_FAMILY= "Kids & Family";
    public static final String HEALTH = "Health";
    public static final String TV_FILM = "TV & Film";
    public static final String MUSIC = "Music";
    public static final String NEWS_POLITICS = "News & Politics";
    public static final String RELIGION_SPIRITUALITY = "Religion & Spirituality";
    public static final String SCIENCE_MEDICINE = "Science & Medicine";
    public static final String SPORTS_RECREATION = "Sports & Recreation";
    public static final String TECHNOLOGY = "Technology";
    public static final String BUSINESS = "Business";
    public static final String GAMES_HOBBIES = "Games & Hobbies";
    public static final String SOCIETY_CULTURE = "Society & Culture";
    public static final String GOVERNMENT_ORIGANIZATION = "Government & Organization";

}