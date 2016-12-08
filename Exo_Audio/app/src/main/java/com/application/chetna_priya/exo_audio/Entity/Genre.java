package com.application.chetna_priya.exo_audio.Entity;


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

}