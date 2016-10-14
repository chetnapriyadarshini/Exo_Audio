package com.app.chetna_priya.audiomodule.Exoplayer;

import android.media.MediaCodec;
import android.util.Log;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;

import java.io.IOException;

/**
 * Created by chetna_priya on 9/4/2016.
 */
public abstract class AudioPlayerImpl implements ExtractorSampleSource.EventListener, DefaultBandwidthMeter.EventListener,
        MediaCodecAudioTrackRenderer.EventListener{

    private static final String TAG = AudioPlayerImpl.class.getSimpleName();

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        Log.d(TAG, "On Audio Track Initialization Error "+e);
        e.printStackTrace();
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
        Log.d(TAG, "On Audio Track Write Error "+e);
        e.printStackTrace();
    }

    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        Log.d(TAG, "On Audio Track Underrun Error "+" Buffer Size: "+bufferSize
                +" BufferSizeMs "+bufferSizeMs+" elapsedSinceLastFeedMs "+elapsedSinceLastFeedMs);
    }

    @Override
    public void onLoadError(int sourceId, IOException e) {
        Log.e(TAG, "On Load Error Source Id : "+sourceId+" "+e);
        e.printStackTrace();
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        Log.e(TAG, "On Decoder Initialization Error "+e);
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        Log.e(TAG, "On Crypto Error "+e);
    }

    @Override
    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
        Log.d(TAG, "Decoder Initialized "+decoderName+" elapsedTime "+elapsedRealtimeMs+" initialization duration: "
                +initializationDurationMs);
    }

    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrate) {
        Log.d(TAG, "Decoder Initialized elapsedTime "+elapsedMs+" bytes: "
                +bytes+" bitrate: "+bitrate);
    }
}
