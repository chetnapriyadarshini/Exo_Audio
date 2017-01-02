package com.application.chetna_priya.exo_audio.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by chetna_priya on 11/23/2016.
 */

public class MediaIDHelper {

    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final String MEDIA_ID_PODCASTS_BY_GENRE = "__BY_GENRE__";
    public static final String MEDIA_ID_PODCASTS_BY_GENRE_AND_CHANNEL_NAME = "__BY_CHANNEL__";
    public static final String MEDIA_ID_PODCASTS_BY_SEARCH = "__BY_SEARCH__";

    private static final char CATEGORY_SEPARATOR = '/';
    private static final char LEAF_SEPARATOR = '|';

    static final String TAG = MediaIDHelper.class.getSimpleName();

    /**
     * Create a String value that represents a playable or a browsable media.
     * <p>
     * Encode the media browseable categories, if any, and the unique podcast ID, if any,
     * into a single String mediaID.
     * <p>
     * MediaIDs are of the form <categoryType>/<categoryValue>-podcastTitle|<podcastEpisodeUniqueId>, to make it easy
     * to find the category (like genre) that a podcast was selected from, so we
     * can correctly build the playing queue. This is specially useful when
     * one podcast can appear in more than one list, like "by genre -> genre_1"
     * and "by artist -> artist_1".
     *
     * @param podcastId  Unique podcast ID for playable items, or null for browseable items.
     * @param categories hierarchy of categories representing this item's browsing parents
     * @return a hierarchy-aware media ID
     */
    public static String createMediaID(String podcastId, String... categories) {
        StringBuilder sb = new StringBuilder();
        if (categories != null) {
            for (int i = 0; i < categories.length; i++) {
                if (!isValidCategory(categories[i])) {
                    throw new IllegalArgumentException("Invalid category: " + categories[0]);
                }
                sb.append(categories[i]);
                if (i < categories.length - 1) {
                    sb.append(CATEGORY_SEPARATOR);
                }
            }
        }
        if (podcastId != null) {
            sb.append(LEAF_SEPARATOR).append(podcastId);
        } else {
            sb.append(CATEGORY_SEPARATOR);
        }

        return sb.toString();
    }


    private static boolean isValidCategory(String category) {
        return category == null ||
                (
                        category.indexOf(CATEGORY_SEPARATOR) < 0 &&
                                category.indexOf(LEAF_SEPARATOR) < 0
                );
    }

    /**
     * Extracts unique musicID from the mediaID. mediaID is, by this sample's convention, a
     * concatenation of category (eg "by_genre"), categoryValue (eg "Classical") and unique
     * podcastID. This is necessary so we know where the user selected the podcast from, when the podcast
     * exists in more than one podcast list, and thus we are able to correctly build the playing queue.
     *
     * @param mediaID that contains the podcastID
     * @return musicID
     */
    public static String extractPodcastIDFromMediaID(@NonNull String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            return mediaID.substring(pos + 1);
        }
        return null;
    }


    /**
     * Extracts category and categoryValue from the mediaID. mediaID is, by this sample's
     * convention, a concatenation of category (eg "by_genre"), categoryValue (eg "Classical") and
     * mediaID. This is necessary so we know where the user selected the podcast from, when the podcast
     * exists in more than one podcast list, and thus we are able to correctly build the playing queue.
     *
     * @param mediaID that contains a category and categoryValue.
     */
    public static
    @NonNull
    String[] getHierarchy(@NonNull String mediaID) {
        //__ROOT__BY_GENRE__/COMEDY/_BY_CHANNEL__/COMEDY BANG BANG|EP 1
        //   Log.d(TAG, "MEDIA    IDDDDDDD _____ "+mediaID);
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            mediaID = mediaID.substring(0, (pos));
        }
        return mediaID.split(String.valueOf(CATEGORY_SEPARATOR));
    }


    public static String extractBrowseCategoryValueFromMediaID(@NonNull String mediaID) {
        String[] hierarchy = getHierarchy(mediaID);
        if (hierarchy.length == 2) {
            return hierarchy[1];
        }
        return null;
    }

    public static boolean isBrowseable(@NonNull String mediaID) {
        return mediaID.indexOf(LEAF_SEPARATOR) < 0;
    }

    public static String getParentMediaID(@NonNull String mediaID) {
        String[] hierarchy = getHierarchy(mediaID);
        if (!isBrowseable(mediaID)) {
            return createMediaID(null, hierarchy);
        }
        if (hierarchy.length <= 1) {
            return MEDIA_ID_ROOT;
        }
        String[] parentHierarchy = Arrays.copyOf(hierarchy, hierarchy.length - 1);
        Log.d(TAG, Arrays.asList(parentHierarchy).toString());
        return createMediaID(null, parentHierarchy);
    }
}
