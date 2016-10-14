package com.app.chetna_priya.audiomodule.Exoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

/**
 * Created by chetna_priya on 9/3/2016.
*/
public class ExtractorRendererBuilder implements ExoAudioPlayer.RendererListener
{
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private final Context context;
    private final String userAgent;
    private final Uri uri;

    public ExtractorRendererBuilder(Context context, String userAgent, Uri uri) {
        this.context = context;
        this.userAgent = userAgent;
        this.uri = uri;
    }

    public interface listener {
        void onSuccess(TrackRenderer[] renderers, DefaultBandwidthMeter bandwidthMeter);
        void onFailure();
    }

    @Override
    public void buildRenderers(ExoAudioPlayer player) {
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        Handler mainHandler = player.getMainHandler();

        ExtractorSampleSource sampleSource;
        // Build the video and audio renderers.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);
        /*
        If the uri is based on internet we use OkhttpDataSource constructed by us to enable fast
        fetching of data over the internet otherwise we fall back on defaultUriDataSource
         */
        if (uri.toString().indexOf("http://") == 0 || uri.toString().indexOf("https://") == 0){
            DataSource httpdataSource = new OkHttpDataSource(userAgent,null,bandwidthMeter);
            sampleSource = new ExtractorSampleSource(uri, httpdataSource, allocator,
                    BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE, mainHandler, player, 0);
        }else {
            DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
            sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                    BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE, mainHandler, player, 0);
        }

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT, null, true, mainHandler, player,
                AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

        // Invoke the callback.
        TrackRenderer[] renderers = new TrackRenderer[ExoAudioPlayer.RENDERER_COUNT];
        renderers[ExoAudioPlayer.TYPE_AUDIO] = audioRenderer;
        player.onSuccess(renderers, bandwidthMeter);
    }

    @Override
    public void cancel() {

    }
}
