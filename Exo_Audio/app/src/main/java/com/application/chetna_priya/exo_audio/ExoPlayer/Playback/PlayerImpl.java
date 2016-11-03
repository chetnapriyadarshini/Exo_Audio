package com.application.chetna_priya.exo_audio.ExoPlayer.Playback;

import android.app.Activity;
import android.content.Context;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.application.chetna_priya.exo_audio.ExoPlayer.Playlist;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.AbstractPlaybackControlView;
import com.application.chetna_priya.exo_audio.ExoPlayer.PlaybackControlView.CustomPlaybackControlView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
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
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by chetna_priya on 10/13/2016.
 */

public class PlayerImpl implements /*ExoPlayer.EventListener,*/
        Playback{

    private static final String TAG = PlayerImpl.class.getSimpleName();
    private AbstractPlaybackControlView exoPlayerView;
    private Context mContext;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer exoPlayer;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private EventLogger eventLogger;
    private Handler mainHandler = new Handler();
    //TODO remember to look into these variables once you implement content provider and database
    private boolean shouldRestorePosition = false;
    private long playerPosition;
    private int playerWindow;
    //TODO remember to look into these variables once you implement content provider and database



    public PlayerImpl(Context context){
        Log.d(TAG, "PLAYER CLASS INITILIAZED BY PODCAST SERVICE");
        mContext = context;
    }

    public void attachView(AbstractPlaybackControlView playbackControlView){
        this.exoPlayerView = playbackControlView;
        if(exoPlayer != null)
            playbackControlView.setPlayer(exoPlayer);
    }


    private void createPlayerIfNeeded() {
        mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the exoPlayer
        exoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

        // Listen to exoPlayer events
        //TODO fix this
        //exoPlayer.addListener(this);
        //Set Event Logger as the Audio Debug Listener
        exoPlayer.setAudioDebugListener(eventLogger);
        //4. Attach view
        exoPlayerView.setPlayer(exoPlayer);
        if (shouldRestorePosition) {
            if (playerPosition == C.TIME_UNSET) {
                exoPlayer.seekToDefaultPosition(playerWindow);
            } else {
                exoPlayer.seekTo(playerWindow, playerPosition);
            }
        }

        eventLogger = new EventLogger();
        mediaDataSourceFactory = buildDataSourceFactory(true);

    }

    public void preparePlayer() {
        MediaSource[] mediaSources = new MediaSource[1];
       // for (int i = 0; i < albumArrayList.size(); i++)
        {
            mediaSources[0] = buildMediaSource(Playlist.getPlaylistInstance().getCurrentAlbumToPlay().getAlbum_uri(), null);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        exoPlayer.prepare(mediaSource, !shouldRestorePosition);
        // Prepare the exoPlayer with the source.
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
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

   /* @Override
    public void releasePlayer(SimpleExoPlayer exoPlayer) {
        if (exoPlayer != null) {
            shouldRestorePosition = false;
            Timeline timeline = exoPlayer.getCurrentTimeline();
            if (timeline != null) {
                playerWindow = exoPlayer.getCurrentWindowIndex();
                Timeline.Window window = timeline.getWindow(playerWindow, new Timeline.Window());
                if (!window.isDynamic) {
                    shouldRestorePosition = true;
                    playerPosition = window.isSeekable ? exoPlayer.getCurrentPosition() : C.TIME_UNSET;
                }
            }
            exoPlayer.release();
            eventLogger = null;
        }
    }*/

    /*@Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playWhenReady && exoPlayerView.playerNeedsMediaSourceAndFocus(exoPlayer)*//* && playbackState != ExoPlayer.STATE_BUFFERING*//*) {
            Log.d(TAG, "PLAYER SHOULD BE PREPARED");
            {
                Intent serviceIntent = new Intent(mContext, PodcastService.class);
                serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                mContext.startService(serviceIntent);
                preparePlayer();
            }
        }
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
    }*/

    public void setPlaybackParams(PlaybackParams playbackParams) {
        exoPlayer.setPlaybackParams(playbackParams);
    }

    @Override
    public void play() {
        createPlayerIfNeeded();
    }

    @Override
    public void pause() {

    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop(boolean notifyListeners) {

    }
}
