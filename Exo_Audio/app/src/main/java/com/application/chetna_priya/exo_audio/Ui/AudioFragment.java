package com.application.chetna_priya.exo_audio.Ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.chetna_priya.exo_audio.ExoPlayer.PlayerService.PodcastService;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.CustomPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

import static android.content.Context.BIND_AUTO_CREATE;

public class AudioFragment extends Fragment {
    private static final String TAG = AudioFragment.class.getSimpleName();

    public AudioFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);
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
       // mListener = null;//Remember to detach listener here if you implement one
    }

}
