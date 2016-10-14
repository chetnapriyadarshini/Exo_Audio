package com.app.chetna_priya.audiomodule;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.chetna_priya.myexoplayersample.PlayerClass;

public class AudioActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, PlayerClass.Callbacks {

    private static final String TAG = AudioActivity.class.getSimpleName();
    private PlayerClass playerClass;
    private AppCompatSeekBar seekBar;
    ImageButton fwdBtn, bwdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        Uri contentUri = Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3");
        Uri[] uris = {contentUri};
        playerClass = new PlayerClass(this, surfaceView, uris);
        seekBar = (AppCompatSeekBar) findViewById(R.id.seek_audio);
        seekBar.setOnSeekBarChangeListener(this);
        fwdBtn = (ImageButton) findViewById(R.id.forward_audio);
        bwdBtn = (ImageButton) findViewById(R.id.rewind_audio);
        playerClass.setCallbacksListener(this);
    }

    public void manipulatePlayer(View view) {

        switch (view.getId()){
            case R.id.play_audio:
                if(playerClass.isPlaying())
                    playerClass.pause();
                else
                    playerClass.play();
                break;
            case R.id.rewind_audio:
                    playerClass.backward();

                break;
            case R.id.forward_audio:
                    playerClass.forward();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Log.d(TAG, "Seek BAR POSITIONNNNNNN position "+i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void updateSeekBar(int durationMs, int totalDurMs) {
        //Log.d(TAG, "DURATION: "+durationMs+" TOTAL DURATION MS : "+totalDurMs);
        float progress = (totalDurMs/(durationMs*100));
        //Log.d(TAG, "PROGRESS ACHIEVED: "+progress+" PROGRESS BEFORE: "+seekBar.getProgress());
       // seekBar.setProgress(progress);
    }

    @Override
    public void setFwdBtnEnabled(boolean enabled) {
        fwdBtn.setEnabled(enabled);
    }

    @Override
    public void setBwdBtnEnabled(boolean enabled) {
        bwdBtn.setEnabled(enabled);
    }
}
