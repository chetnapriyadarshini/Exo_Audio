package com.application.chetna_priya.exo_audio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;

public class PreferenceHelper {
    private static final String TAG = PreferenceHelper.class.getSimpleName();
    private static final String SAVED_GENRES = "saved_genres";
    private static final String IS_GENRE_PREFERENCE_SET = "is_genre_preference_set";

    public static boolean isInitialGenrePreferenceSet(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(IS_GENRE_PREFERENCE_SET, false);
    }

    public static void saveGenrePreferences(Context context, ArrayList<String> savedGenres) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(SAVED_GENRES, new HashSet<>(savedGenres));
        editor.putBoolean(IS_GENRE_PREFERENCE_SET, true);
        editor.apply();
    }

    public static ArrayList<String> getSavedGenres(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        HashSet<String> set = (HashSet<String>) sharedPref.getStringSet(SAVED_GENRES, null);
        if (set == null) {
            return null;
        }

        return new ArrayList<>(set);
    }

}
