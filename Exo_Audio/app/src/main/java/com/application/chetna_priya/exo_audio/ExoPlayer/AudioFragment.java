package com.application.chetna_priya.exo_audio.ExoPlayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.R;

public class AudioFragment extends Fragment {
    private static final String TAG = AudioFragment.class.getSimpleName();

    private CustomPlaybackControlView exoplayerView;

    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);
        exoplayerView = (CustomPlaybackControlView) rootView.findViewById(R.id.exo_player_control);
       return rootView;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy the PLAYER class created by AUDIO ACTIVITY");
        exoplayerView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        exoplayerView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        exoplayerView.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;//Remember to detach listener here if you implement one
    }
}
