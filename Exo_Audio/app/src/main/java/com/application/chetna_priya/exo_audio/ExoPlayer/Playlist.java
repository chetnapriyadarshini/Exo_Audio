package com.application.chetna_priya.exo_audio.ExoPlayer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chetna_priya on 10/24/2016.
 */

public class Playlist {

    static Playlist playlistInstance;

    private Playlist(){}

    public static Playlist getPlaylistInstance(){
        if(playlistInstance == null){
            playlistInstance = new Playlist();
        }
        return playlistInstance;
    }

    private static Queue<Album> albumQueue = new LinkedList<>();

    public void addAlbumToList(){
        albumQueue.add(new Album("ALBUM1",
                "http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3"));

        albumQueue.add(new Album("ALBUM2",
                "http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3"));
     }

    public boolean isPlaylistEmpty(){
        return albumQueue.size()==0;
    }

    public void empty() {
        albumQueue = null;
        albumQueue = new LinkedList<>();
    }

    public Album getCurrentAlbumToPlay(){
        return albumQueue.remove();
    }

    public void addAlbum(Album album){
        albumQueue.add(album);
    }
}
