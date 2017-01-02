package com.application.chetna_priya.exo_audio.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.exoplayer.playerservice.MediaNotificationManager;

public class CurrentAudioProvider extends AppWidgetProvider {

    private static final String TAG = CurrentAudioProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, CurrentAudioIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, CurrentAudioIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        Intent serviceIntent = new Intent(context, CurrentAudioIntentService.class);
        switch (intent.getAction()) {
            case MediaNotificationManager.ACTION_DEFAULT_BACK:
                serviceIntent.setAction(intent.getAction());
                context.startService(serviceIntent);
                break;

            case MediaNotificationManager.ACTION_PLAYBACK_STATE_CHANGED:
                serviceIntent.setAction(intent.getAction());
                String stateKey = MediaNotificationManager.PLAYBACK_STATE_KEY;
                //  String metadataKeyAlbum = MediaMetadataCompat.METADATA_KEY_ALBUM;
                // String metadataKeyUri = MediaMetadataCompat.METADATA_KEY_ART_URI;
                String metadata_key = MediaNotificationManager.METADATA_KEY;
                serviceIntent.putExtra(stateKey, intent.getIntExtra(stateKey, PlaybackStateCompat.STATE_NONE));
                serviceIntent.putExtra(metadata_key, intent.getParcelableExtra(metadata_key));
                //    serviceIntent.putExtra(metadataKeyUri, intent.getStringExtra(metadataKeyUri));
                context.startService(serviceIntent);
                break;

            default:
                Log.d(TAG, "Receievdddddddd but not matcehdddddddddddd");
        }
    }
}
