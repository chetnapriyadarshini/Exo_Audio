package com.application.chetna_priya.exo_audio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.ui.playbackControlView.CustomPlaybackControlView;
import com.application.chetna_priya.exo_audio.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioFragment extends Fragment {
    private static final String TAG = AudioFragment.class.getSimpleName();

    @BindView(R.id.exo_player_control)
    CustomPlaybackControlView mCustomPlaybackControlView;

    @BindView(R.id.btn_rew)
    ImageButton rewButton;

    @BindView(R.id.btn_next)
    ImageButton nextButton;

    @BindView(R.id.btn_ffwd)
    ImageButton ffwdButton;

    @BindView(R.id.btn_prev)
    ImageButton prevButton;

    @BindView(R.id.btn_play)
    ImageButton playPauseButton;

    @BindView(R.id.tv_time)
    TextView totalTime;

    @BindView(R.id.tv_time_current)
    TextView currentTime;

    @BindView(R.id.seek_mediacontroller_progress)
    SeekBar seekBar;

    @BindView(R.id.tv_speed)
    TextView speedView;
    private String mArtUrl;


    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
