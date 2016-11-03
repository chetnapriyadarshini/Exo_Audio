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
import com.application.chetna_priya.exo_audio.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.PlaybackControlView.CustomPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

import static android.content.Context.BIND_AUTO_CREATE;

public class AudioFragment extends Fragment implements AbstractPlaybackControlView.ActivityCallbacks {
    private static final String TAG = AudioFragment.class.getSimpleName();

    private CustomPlaybackControlView exoplayerView;
    private MediaBrowserCompat mMediaBrowser;

    public AudioFragment() {
        // Required empty public constructor
    }


    private PodcastService mService;
    private boolean mBound;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PodcastService.LocalBinder binder = (PodcastService.LocalBinder) service;
            mService = binder.getService();
            mService.setViewForPlayer(exoplayerView);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


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

        super.onDestroy();
    }

    @Override
    public void onStart() {

        Intent serviceIntent = new Intent(getActivity(), PodcastService.class);
        if(PodcastService.isServiceRunning)
            getActivity().bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        mMediaBrowser.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
        mMediaBrowser.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;//Remember to detach listener here if you implement one
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void setSupportMediaControllerForActivity(MediaControllerCompat mediaController) {
        getActivity().setSupportMediaController(mediaController);
    }

    @Override
    public void setMediaBrowser(MediaBrowserCompat mediaBrowser) {
        mMediaBrowser = mediaBrowser;
    }
}
