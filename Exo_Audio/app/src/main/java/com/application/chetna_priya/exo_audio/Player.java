package com.application.chetna_priya.exo_audio;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by chetna_priya on 10/13/2016.
 */

public class Player implements ExoPlayer.EventListener{

    private CustomPlaybackControlView exoPlayerView;
    private Context mContext;
    private static boolean isCreated = false;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private EventLogger eventLogger;
    private Handler mainHandler;
    private boolean shouldRestorePosition = false;
    private long playerPosition;
    private int playerWindow;
    private boolean shouldAutoPlay;

    public Player(Context context, CustomPlaybackControlView exoPlayerView){
        mContext = context;
        this.exoPlayerView = exoPlayerView;
        createPlayer(false);
    }

    private void createPlayer(boolean autoPlayWhenReady) {
        mainHandler = new Handler();
        eventLogger = new EventLogger();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        // Listen to player events
        player.addListener(this);
        //Set Event Logger as the Audio Debug Listener
        player.setAudioDebugListener(eventLogger);
        //4. Attach view
        exoPlayerView.setPlayer(player);
        if (shouldRestorePosition) {
            if (playerPosition == C.TIME_UNSET) {
                player.seekToDefaultPosition(playerWindow);
            } else {
                player.seekTo(playerWindow, playerPosition);
            }
        }
        player.setPlayWhenReady(autoPlayWhenReady);
        isCreated = true;
    }

    public void preparePlayer(Uri[] uris) {
        // Produces DataSource instances through which media data is loaded.
       /* DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, mContext.getString(R.string.app_name)));
       */ // Produces Extractor instances for parsing the media data.
      //  ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
       /* Uri contentUri = Uri.parse("http://feeds.soundcloud.com/stream/280380933-comedybangbang-442-andy-daly-jeremy-rowley.mp3");
        MediaSource videoSource = new ExtractorMediaSource(contentUri,
                dataSourceFactory, extractorsFactory, null, null);*/
        MediaSource[] mediaSources = new MediaSource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            mediaSources[i] = buildMediaSource(uris[i], null);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        player.prepare(mediaSource, !shouldRestorePosition);
        // Prepare the player with the source.
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((DemoApplication) ((Activity)mContext).getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *     DataSource factory.
     * @return A new HttpDataSource factory.
     */
    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((DemoApplication) ((Activity)mContext).getApplication())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public void play(Uri[] uris){
        if(isCreated)
            preparePlayer(uris);
        else
            createPlayer(true);
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            shouldRestorePosition = false;
            Timeline timeline = player.getCurrentTimeline();
            if (timeline != null) {
                playerWindow = player.getCurrentWindowIndex();
                Timeline.Window window = timeline.getWindow(playerWindow, new Timeline.Window());
                if (!window.isDynamic) {
                    shouldRestorePosition = true;
                    playerPosition = window.isSeekable ? player.getCurrentPosition() : C.TIME_UNSET;
                }
            }
            player.release();
            player = null;
            eventLogger = null;
        }
    }


    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = mContext.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = mContext.getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = mContext.getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = mContext.getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
    }

    @Override
    public void onPositionDiscontinuity() {

    }


    private void showToast(int messageId) {
        showToast(mContext.getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
