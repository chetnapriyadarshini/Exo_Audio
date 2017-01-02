package com.application.chetna_priya.exo_audio.utils;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SortingHelper {

    public void sortArrayList(ArrayList<MediaBrowserCompat.MediaItem> mediaItemArrayList) {
        Collections.sort(mediaItemArrayList, new Comparator<MediaBrowserCompat.MediaItem>() {
            @Override
            public int compare(MediaBrowserCompat.MediaItem mediaItem1, MediaBrowserCompat.MediaItem mediaItem2) {
                String releaseDate1 = mediaItem1.getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_DATE);
                String releaseDate2 = mediaItem2.getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_DATE);
                //   Month Day Year
                String[] dateArr1 = releaseDate1.split(" ");
                String[] dateArr2 = releaseDate2.split(" ");
                int relyear1 = Integer.parseInt(dateArr1[2]);
                int relyear2 = Integer.parseInt(dateArr2[2]);
                //if the year is same check for the month
                if (relyear1 == relyear2) {
                    int compare = new MonthComparator().compare(dateArr1[1], dateArr2[1]);
                    //If months are equal we check for day
                    if (compare == 0) {
                        return new DateComaprator().compare(dateArr1[0], dateArr2[0]);
                    } else
                        return compare;
                } else
                    return relyear1 < relyear2 ? 1 : -1;
            }
        });
    }


    private String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sept", "Oct", "Nov", "Dec"};
    private ArrayList<String> monthArrayList = new ArrayList<>(Arrays.asList(monthList));

    private class MonthComparator implements Comparator<String> {

        @Override
        public int compare(String month1, String month2) {
            int index1 = monthArrayList.indexOf(month1);
            int index2 = monthArrayList.indexOf(month2);
            return index1 < index2 ? 1 : index1 == index2 ? 0 : -1;
        }
    }

    private class DateComaprator implements Comparator<String> {
        @Override
        public int compare(String day1, String day2) {
            int firstDay = Integer.parseInt(day1);
            int secondDay = Integer.parseInt(day2);
            return firstDay < secondDay ? 1 : firstDay == secondDay ? 0 : -1;
        }
    }
}
