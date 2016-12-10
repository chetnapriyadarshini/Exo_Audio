package com.application.chetna_priya.exo_audio.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.R;

public class FeaturedFragment extends Fragment {

    public static final int REQUEST_CODE_ADD_GENRES = 2;
    private FeatureRecyViewAdapter featureRecyViewAdapter;
    private final String TAG = FeaturedFragment.class.getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ON CREATE VIEWWWWWWWW CALLEDDDDDDDDDDD");
        View rootView = inflater.inflate(R.layout.layout_featured_fragment, container, false);
        /*
        This is the view consisting of many recycler view diff category podcast and a cardview of add
        new categories all enclosed in a scroll view
         */
        CardView add = (CardView) rootView.findViewById(R.id.add_new_categories_cardview);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent genreIntent = new Intent(getActivity(), GenreActivity.class);
                genreIntent.putExtra(GenreActivity.IS_FIRST_TIME, false);
                getActivity().startActivityForResult(genreIntent, REQUEST_CODE_ADD_GENRES);
            }
        });
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.album_category_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        featureRecyViewAdapter = new FeatureRecyViewAdapter(getActivity());
        recyclerView.setAdapter(featureRecyViewAdapter);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "NOTIFYYYYYYYYYYYYYYYYY  YYYYYYYYYYYYY "+featureRecyViewAdapter);
        if(requestCode == REQUEST_CODE_ADD_GENRES){
            //TODO the featurerecyclerview is null, find why and fix
          //  featureRecyViewAdapter.notifyDataSetChanged();
        }
    }
}
