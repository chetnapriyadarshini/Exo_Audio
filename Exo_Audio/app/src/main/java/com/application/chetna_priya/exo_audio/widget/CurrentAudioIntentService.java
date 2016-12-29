package com.application.chetna_priya.exo_audio.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.exoPlayer.playerService.MediaNotificationManager;
import com.application.chetna_priya.exo_audio.ui.AudioActivity;
import com.application.chetna_priya.exo_audio.ui.MainActivity;
import com.application.chetna_priya.exo_audio.utils.AlbumArtCache;


public class CurrentAudioIntentService extends IntentService {


    private static final String TAG = CurrentAudioIntentService.class.getSimpleName();
/*
    private  PendingIntent mPauseIntent;
    private  PendingIntent mPlayIntent;

    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;*/


    private static final int REQUEST_CODE = 100;

    private static MediaMetadataCompat metadataCompat;
    private static int state;
    // private MediaBrowserCompat mMediaBrowser;

    public CurrentAudioIntentService() {
        super("CurrentAudioIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getAction() == null) {
            if(metadataCompat == null)
                updateWidgetToDefaultValues();
            else
                updateWidget(null);
            return;
        }

        switch (intent.getAction()){

            case MediaNotificationManager.ACTION_DEFAULT_BACK:
                updateWidgetToDefaultValues();
                break;

            case MediaNotificationManager.ACTION_PLAYBACK_STATE_CHANGED:
             //   mPlaybackState = intent.getParcelableExtra(MediaNotificationManager.PLAYBACK_STATE_KEY);
              //  mMetadata = intent.getParcelableExtra(MediaNotificationManager.METADATA_KEY);
                updateWidget(intent);
                break;

            default:
                updateWidgetToDefaultValues();
        }
    }

    private void updateWidget(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                CurrentAudioProvider.class));


        for (int appWidgetId : appWidgetIds) {
          //  MediaDescriptionCompat description = mMetadata.getDescription();
            if(intent != null)
                metadataCompat = intent.getParcelableExtra(MediaNotificationManager.METADATA_KEY);
            Bitmap art = null;
            String fetchArtUrl = null;
            String artUrl = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
            if (artUrl != null) {
                // This sample assumes the iconUri will be a valid URL formatted String, but
                // it can actually be any valid Android Uri formatted String.
                // async fetch the album art icon
                art = AlbumArtCache.getInstance().getBigImage(artUrl);
                if (art == null) {
                    fetchArtUrl = artUrl;
                    // use a placeholder art while the remote art is being downloaded
                    art = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher);
                }
            }
            int layoutId = getLayoutId(appWidgetManager,appWidgetId);
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            if (fetchArtUrl != null) {
                fetchBitmapFromURLAsync(fetchArtUrl, views);
            }
            int maxLength = getResources().getInteger(R.integer.max_podcast_title_length);
            String text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
            if(text == null)
                text = getString(R.string.no_media_played);

            if(text.length() > maxLength)
                text = text.substring(0, maxLength).concat("...");

            views.setImageViewBitmap(R.id.widget_icon, art);
            views.setTextViewText(R.id.widget_description, text);
            if(intent != null)
                state = intent.getIntExtra(MediaNotificationManager.PLAYBACK_STATE_KEY, PlaybackStateCompat.STATE_NONE);
            if(state == PlaybackStateCompat.STATE_PLAYING ||
                    state == PlaybackStateCompat.STATE_BUFFERING) {
                views.setImageViewResource(R.id.widget_play_pause, R.drawable.exo_controls_pause);
                views.setOnClickPendingIntent(R.id.widget_play_pause,
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE));
            }else {
                views.setImageViewResource(R.id.widget_play_pause, R.drawable.exo_controls_play);
                views.setOnClickPendingIntent(R.id.widget_play_pause,
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_PLAY));
            }

            views.setOnClickPendingIntent(R.id.widget, createContentIntent(metadataCompat.getDescription()));

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }


    private void updateWidgetToDefaultValues() {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                CurrentAudioProvider.class));

        for (int appWidgetId : appWidgetIds) {

            int layoutId = getLayoutId(appWidgetManager, appWidgetId);
            Bitmap art = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            String text = getString(R.string.no_media_played);

            views.setImageViewBitmap(R.id.widget_icon, art);
            views.setTextViewText(R.id.widget_description, text);
            views.setImageViewResource(R.id.widget_play_pause, R.drawable.exo_controls_play);
            Intent openUI = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, openUI,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getLayoutId(AppWidgetManager appWidgetManager, int appWidgetId) {
        int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
        int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_current_default_width);
        int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_current_large_width);
        int layoutId;
        if (widgetWidth >= largeWidth) {
            layoutId = R.layout.widget_current_large;
        } else if (widgetWidth >= defaultWidth) {
            layoutId = R.layout.widget_current;
        } else {
            layoutId = R.layout.widget_current_small;
        }
        return layoutId;
    }


    private void fetchBitmapFromURLAsync(final String bitmapUrl, final RemoteViews views) {
        AlbumArtCache.getInstance().fetch(bitmapUrl, new AlbumArtCache.FetchListener() {
            @Override
            public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {/*
                if (mMetadata != null && mMetadata.getDescription().getIconUri() != null &&
                        mMetadata.getDescription().getIconUri().toString().equals(artUrl))*/
                {
                    // If the media is still the same, update the notification:
                    Log.d(TAG, "fetchBitmapFromURLAsync: set bitmap to " + artUrl);
                    views.setImageViewBitmap(R.id.widget_icon, bitmap);
                }
            }
        });
    }


    private PendingIntent createContentIntent(MediaDescriptionCompat description) {
        Intent openUI = new Intent(this, AudioActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openUI.putExtra(AudioActivity.EXTRA_START_FULLSCREEN, true);
        if (description != null) {
            openUI.putExtra(AudioActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, description);
        }
        return PendingIntent.getActivity(this, REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }




    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_current_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_current_default_width);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
