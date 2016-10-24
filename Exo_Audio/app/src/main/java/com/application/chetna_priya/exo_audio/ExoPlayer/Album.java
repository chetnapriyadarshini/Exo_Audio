package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.net.Uri;

/**
 * Created by chetna_priya on 10/24/2016.
 */

public class Album  {

    private String album_name;
    private Uri album_uri;

    public Album(String album_name, String album_uri){
        this.album_name = album_name;
        this.album_uri = Uri.parse(album_uri);
    }

    public String getAlbum_name() {
        return album_name;
    }

    public Uri getAlbum_uri() {
        return album_uri;
    }
}
