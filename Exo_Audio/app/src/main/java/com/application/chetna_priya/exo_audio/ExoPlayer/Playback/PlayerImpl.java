package com.application.chetna_priya.exo_audio.exoPlayer.playback;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.PlaybackParams;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.application.chetna_priya.exo_audio.DemoApplication;
import com.application.chetna_priya.exo_audio.data.PodcastContract;
import com.application.chetna_priya.exo_audio.model.MediaProviderSource;
import com.application.chetna_priya.exo_audio.model.PodcastProvider;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.utils.MediaIDHelper;
import com.application.chetna_priya.exo_audio.utils.PathHelper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
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

public class PlayerImpl implements ExoPlayer.EventListener, AudioManager.OnAudioFocusChangeListener, Playback{

    private static final String TAG = PlayerImpl.class.getSimpleName();
    private final AudioManager mAudioManager;
    private final PodcastProvider mPodcastProvider;
    private int mState;
 //   private AbstractPlaybackControlView exoPlayerView;
    private Context mContext;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer
            exoPlayer;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private EventLogger eventLogger;
    private Handler mainHandler = new Handler();

    //TODO remember to look into these variables once you implement content provider and database
    private boolean shouldRestorePosition = false;
    private long mCurrentPosition;
    private int playerWindow;
    //TODO remember to look into these variables once you implement content provider and database
    private Callback mCallback;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS = 0;
    // we have full audio focus
    private static final int AUDIO_FOCUSED  = 1;
    // Type of audio focus we have:
    private int mAudioFocus = AUDIO_NO_FOCUS;
    public static final float VOLUME_NORMAL = 1.0f;
    private boolean mPlayOnFocusGain;

    public final int DEFAULT_FAST_FORWARD_MS = 30000;
    public final int DEFAULT_REWIND_MS = 30000;


    private int rewindMs = DEFAULT_REWIND_MS;
    private int fastForwardMs = DEFAULT_FAST_FORWARD_MS;
    private String mCurrentMediaId;
    private final WifiManager.WifiLock mWifiLock;
    private boolean isDurationSet = false;
    private String NETWORK = "network";
    private String LOCAL = "local";
    private String dataSource = NETWORK;


    public PlayerImpl(Context context, PodcastProvider podcastProvider){
        Log.d(TAG, "PLAYER CLASS INITILIAZED BY PODCAST SERVICE");
        mContext = context;
        mPodcastProvider = podcastProvider;
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mState = PlaybackStateCompat.STATE_NONE;
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        this.mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "podway_lock");

    }


    private void createPlayerIfNeeded() {
        if(exoPlayer == null) {
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
            exoPlayer.addListener(this);
            //Set Event Logger as the Audio Debug Listener
            exoPlayer.setAudioDebugListener(eventLogger);
            //4. Attach view
           // exoPlayerView.setPlayer(exoPlayer);
            if (shouldRestorePosition) {
                if (mCurrentPosition == C.TIME_UNSET) {
                    exoPlayer.seekToDefaultPosition(playerWindow);
                } else {
                    exoPlayer.seekTo(playerWindow, mCurrentPosition);
                }
            }

            eventLogger = new EventLogger();
            if(dataSource.equals(LOCAL))
                mediaDataSourceFactory = buildLocalDataSourceFactory();
            else
                mediaDataSourceFactory = buildDataSourceFactory(true);
        }

    }

    public void preparePlayer(MediaSource[] mediaSources) {
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        exoPlayer.prepare(mediaSource, !shouldRestorePosition);
        // Prepare the exoPlayer with the source.
     //  exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);/*
        if(mCallback != null)
            mCallback.onPlaybackStatusChanged(mState);*/
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
        return ((DemoApplication) mContext.getApplicationContext())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildLocalDataSourceFactory() {
        return ((DemoApplication) mContext.getApplicationContext())
                .buildFileDataSourceFactory();
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((DemoApplication) mContext.getApplicationContext())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }


    public void relaxResources(boolean releaseExoPlayer) {
        if (releaseExoPlayer && exoPlayer != null)
        {
            shouldRestorePosition = false;
            Timeline timeline = exoPlayer.getCurrentTimeline();
            if (timeline != null) {
                playerWindow = exoPlayer.getCurrentWindowIndex();
                Timeline.Window window = timeline.getWindow(playerWindow, new Timeline.Window());
                if (!window.isDynamic) {
                    shouldRestorePosition = true;
                    mCurrentPosition = window.isSeekable ? exoPlayer.getCurrentPosition() : C.TIME_UNSET;
                }
            }
            exoPlayer.release();
            eventLogger = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if(isLoading)
        {
          //  mState = PlaybackStateCompat.STATE_CONNECTING;
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

       // mCurrentPosition = exoPlayer.getCurrentPosition();
     //   Log.d(TAG, "PLAYER STATE CHANGEDDDDDDDDDD "+playbackState/*+" CURRENT POSITION "+mCurrentPosition
          //      +" DURATION "+exoPlayer.getDuration()+" IS LEFTTTTT "+(exoPlayer.getDuration()-mCurrentPosition)*/);
        if(exoPlayer.getCurrentPosition() >= exoPlayer.getDuration()) {
            mCallback.onCompletion();
            return;
        }
        switch (playbackState){
            case ExoPlayer.STATE_BUFFERING:
                mState = PlaybackStateCompat.STATE_BUFFERING;
                mCallback.onPlaybackStatusChanged(mState);
                break;
            case ExoPlayer.STATE_ENDED:
                mCallback.onCompletion();
                break;
            case ExoPlayer.STATE_READY:
                if(playWhenReady) {
                    if(!isDurationSet){
                        isDurationSet = true;
                        mPodcastProvider.updatePodcastDuration(mCallback.getCurrentPodcastID(),exoPlayer.getDuration());
                    }
                    mState = PlaybackStateCompat.STATE_PLAYING;
                  Log.d(TAG, "THe duration set by exoplayer is : "+exoPlayer.getDuration());
                } else
                    mState = PlaybackStateCompat.STATE_PAUSED;
                mCallback.onPlaybackStatusChanged(mState);
                break;
            case ExoPlayer.STATE_IDLE:
                mState = PlaybackStateCompat.STATE_NONE;
                mCallback.onPlaybackStatusChanged(mState);
                break;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        mCallback.onPlaybackStatusChanged(mState);
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
        mState = PlaybackStateCompat.STATE_ERROR;
        mCallback.onPlaybackStatusChanged(mState);
    }

    @Override
    public void onPositionDiscontinuity() {
        mCallback.onPlaybackStatusChanged(mState);
    }


    private void showToast(int messageId) {
        showToast(mContext.getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void setPlaybackParams(PlaybackParams playbackParams) {
        exoPlayer.setPlaybackParams(playbackParams);
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }


    @Override
    public void stop(boolean notifyListeners) {
        mState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners && mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
        mCurrentPosition = getCurrentStreamPosition();
        // Give up Audio focus
        giveUpAudioFocus();
        // Relax all resources
        relaxResources(true);
    }

    private void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus");
        if (mAudioFocus == AUDIO_FOCUSED) {
            if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_NO_FOCUS;
            }

        }
    }

    @Override
    public void setState(int state) {
        this.mState = state;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (exoPlayer != null && exoPlayer.getPlayWhenReady());
    }


    @Override
    public long getCurrentStreamPosition() {
        return exoPlayer != null ?
                exoPlayer.getCurrentPosition() : mCurrentPosition;
    }

    @Override
    public float getPlaybackSpeed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return exoPlayer != null && exoPlayer.getPlaybackParams()!= null?
                    exoPlayer.getPlaybackParams().getSpeed() : 1.0f;
        }
        return 1.0f;
    }

    @Override
    public void updateLastKnownStreamPosition() {
        if (exoPlayer != null) {
            mCurrentPosition = exoPlayer.getCurrentPosition();
        }
    }

    @Override
    public void play(MediaSessionCompat.QueueItem item ) {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();

        String mediaId = item.getDescription().getMediaId();
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
        if (mediaHasChanged) {
            mCurrentPosition = 0;
            mCurrentMediaId = mediaId;
        }

        if (mState == PlaybackStateCompat.STATE_PAUSED && !mediaHasChanged && exoPlayer != null) {
            configMediaPlayerState();
        } else {
            mState = PlaybackStateCompat.STATE_STOPPED;
            relaxResources(false); // release everything except ExoPlayer
            MediaMetadataCompat track = mPodcastProvider.getPodcast(
                    MediaIDHelper.extractPodcastIDFromMediaID(mediaId));
           String source = track.getString(MediaProviderSource.CUSTOM_METADATA_TRACK_SOURCE);
            Cursor cursor = mContext.getContentResolver().query(PodcastContract.EpisodeEntry.CONTENT_URI,
                    new String[]{PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_NAME},
                    PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_MEDIA_ID+ " = ?",
                    new String[]{mediaId}, null);
            if(cursor!= null && cursor.moveToFirst()){
                String name = cursor.getString(cursor.getColumnIndex(PodcastContract.EpisodeEntry.COLUMN_PODCAST_EPISODE_NAME));
                String path = PathHelper.getDownloadPodcastPath(mContext);
                source = path+"/"+name;
                cursor.close();
                //We want the exoplayer to play from local resources so we have to do a new setup
                dataSource = NETWORK;
            }else
                dataSource = LOCAL;
            //If the data source has changed we need to reinitialize the exoplayer
            /*
            We need to check here if the exoplayer has changed its data source
             */
            //noinspection ResourceType

            Log.d(TAG, "The Duration set by us is "+track.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

            try{
                createPlayerIfNeeded();
                mState = PlaybackStateCompat.STATE_BUFFERING;
                MediaSource[] mediaSources = new MediaSource[]{buildMediaSource(Uri.parse(source), null)};
                preparePlayer(mediaSources);
                // If we are streaming from the internet, we want to hold a
                // Wifi lock, which prevents the Wifi radio from going to
                // sleep while the song is playing.
                mWifiLock.acquire();
                if(mCallback != null)
                    mCallback.onPlaybackStatusChanged(mState);
            }catch (Exception ex){
                Log.e(TAG, ex+ "Exception playing song");
                if (mCallback != null) {
                    mCallback.onError(ex.getMessage());
                }
            }
        }

    }

    @Override
    public void pause() {
        Log.d(TAG, "PAUSE REQUEST RECEIVEDDDDDDDDDDDDDDDDDD IN PLAYERIMPL");
        if (mState == PlaybackStateCompat.STATE_PLAYING || mState == PlaybackStateCompat.STATE_BUFFERING) {
            // Pause media player and cancel the 'foreground service' state.
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                exoPlayer.setPlayWhenReady(false);
                mCurrentPosition = exoPlayer.getCurrentPosition();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false);
            giveUpAudioFocus();
        }
        mState = PlaybackStateCompat.STATE_PAUSED;
        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void seekTo(long position) {
        Log.d(TAG, "seekTo called with "+ position);

        if (exoPlayer == null) {
            // If we do not have a current media player, simply update the current position
            mCurrentPosition = position;
        } else {
            if (exoPlayer.getPlayWhenReady()) {
                mState = PlaybackStateCompat.STATE_BUFFERING;
            }
            exoPlayer.seekTo(position);
            if (mCallback != null) {
                mCallback.onPlaybackStatusChanged(mState);
            }
        }
    }

    @Override
    public void changeSpeed(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            PlaybackParams playbackParams = new PlaybackParams();
            playbackParams.setSpeed(speed);
            exoPlayer.setPlaybackParams(playbackParams);
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void setCurrentStreamPosition(int pos) {
        this.mCurrentPosition = pos;
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return mCurrentMediaId;
    }

    private void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus");
        if (mAudioFocus != AUDIO_FOCUSED) {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_FOCUSED;
            }
        }
    }

    private void configMediaPlayerState(){
        if (mAudioFocus == AUDIO_NO_FOCUS) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                pause();
            }
        } else {  // we have audio focus:
            exoPlayer.setVolume(VOLUME_NORMAL);

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                if (exoPlayer != null && !exoPlayer.getPlayWhenReady()) {
                    Log.d(TAG,"configMediaPlayerState startMediaPlayer. seeking to "+
                            mCurrentPosition);
                    if (mCurrentPosition == exoPlayer.getCurrentPosition()) {
                        Log.d(TAG, "SET PLAYINGGGGGGGGGGGGGGGGGGG");
                        exoPlayer.setPlayWhenReady(true);
                        mState = PlaybackStateCompat.STATE_PLAYING;
                    } else {
                        Log.d(TAG, "SET BUFFERRINGGGGGGGGGGG "+mCurrentPosition+" playerposition "+exoPlayer.getCurrentPosition());
                        exoPlayer.seekTo(mCurrentPosition);
                        mState = PlaybackStateCompat.STATE_BUFFERING;
                        exoPlayer.setPlayWhenReady(true);
                    }
                }
                mPlayOnFocusGain = false;
            }
        }

        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        Log.d(TAG, "onAudioFocusChange. focusChange="+ focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            mAudioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                mPlayOnFocusGain = true;
            }
        }
        configMediaPlayerState();
    }

    @Override
    public void rewind() {
        Timeline currentTimeline = exoPlayer.getCurrentTimeline();
        if(currentTimeline != null)
            seekTo(Math.max(exoPlayer.getCurrentPosition() - rewindMs, 0));
    }

    @Override
    public void fastForward() {

        seekTo(Math.min(exoPlayer.getCurrentPosition() + fastForwardMs, exoPlayer.getDuration()));
    }


    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds.
     */
    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
    }
}
