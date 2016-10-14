package com.example.chetna_priya.audiolib;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AudioActivityFragment extends Fragment {

    private static final String TAG = AudioActivityFragment.class.getSimpleName();

    public AudioActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_audio, container, false);
        Log.d(TAG, "ON CREATE VIEWWWWWWWWWWWW");
        VideoView videoView = (VideoView) rootView.findViewById(R.id.media_view);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        AudioService.setAudio("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3", "Treat you Better");
        Intent serviceIntent = new Intent(getActivity(), AudioService.class);
        serviceIntent.setAction(getString(R.string.ACTION_PLAY));
        getActivity().startService(serviceIntent);
        return rootView;
    }
}
