package com.application.chetna_priya.exo_audio.Ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.R;

public class FeaturedFragment extends Fragment {
    /*
    public static FeaturedFragment getInstance(String category_title, String category_url){

    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_featured_fragment, container, false);
        return rootView;
    }
}
