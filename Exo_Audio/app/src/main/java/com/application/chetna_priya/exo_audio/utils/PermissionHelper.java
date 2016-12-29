package com.application.chetna_priya.exo_audio.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class PermissionHelper {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_USAGE = 1;

    public static boolean requestForPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
/*
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    permission)) {
                //TODO add expalanation
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else*/ {

                // No explanation needed, we can request the permission.
                int requestCode = 0;
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    requestCode = MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_USAGE;

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permission},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        return true;
    }
}
