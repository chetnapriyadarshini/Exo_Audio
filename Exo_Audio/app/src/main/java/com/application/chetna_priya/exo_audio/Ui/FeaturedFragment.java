package com.application.chetna_priya.exo_audio.Ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.R;

public class FeaturedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_featured_fragment, container, false);
        /*
        This is the view consisting of many recycler view diff category podcast and a cardview of add
        new categories all enclosed in a scroll view
         */
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.album_category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new FeatureRecyViewAdapter(getActivity()));
        return rootView;
    }
}
