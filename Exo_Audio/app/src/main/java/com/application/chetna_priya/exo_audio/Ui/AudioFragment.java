package com.application.chetna_priya.exo_audio.Ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.Ui.PlaybackControlView.CustomPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

public class AudioFragment extends Fragment {
    private static final String TAG = AudioFragment.class.getSimpleName();
//    private CustomPlaybackControlView mCustomPlaybackControlView;

    public AudioFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);
   //     mCustomPlaybackControlView = (CustomPlaybackControlView) rootView.findViewById(R.id.exo_player_control);
       return rootView;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy the PLAYER class created by AUDIO ACTIVITY");

        super.onDestroy();
    }


    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;//Remember to detach Listener here if you implement one
    }

}
