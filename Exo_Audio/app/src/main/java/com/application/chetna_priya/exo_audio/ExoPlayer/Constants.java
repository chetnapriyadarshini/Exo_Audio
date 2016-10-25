package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.application.chetna_priya.exo_audio.R;

/**
 * Created by chetna_priya on 10/24/2016.
 */

public class Constants {

    public interface ACTION {
      //  public static String MAIN_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.action.main";
     //   public static String INIT_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.init";
        public String PREV_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.action.prev";
        public static String PLAY_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.play";
        public static String NEXT_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.next";
        public static String STARTFOREGROUND_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.application.chetna_priya.exo_audio.ExoPlayer.stopforeground";

    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_album_art, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
}
