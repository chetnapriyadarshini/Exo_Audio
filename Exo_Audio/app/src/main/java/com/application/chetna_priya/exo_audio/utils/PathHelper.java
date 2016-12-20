package com.application.chetna_priya.exo_audio.utils;

import android.content.Context;
import android.os.Environment;

import com.application.chetna_priya.exo_audio.R;

import java.io.File;

/**
 * Created by chetna_priya on 12/19/2016.
 */

public class PathHelper {

    public static String getDownloadPodcastPath(Context context){
        File direct = new File(Environment.getExternalStorageDirectory()
                +"/" +context.getString(R.string.app_name));

        if (!direct.exists()) {
            direct.mkdirs();
        }
        return String.valueOf(direct);
    }
}
