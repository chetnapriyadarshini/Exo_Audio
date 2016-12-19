package com.application.chetna_priya.exo_audio.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class DownloadListener extends BroadcastReceiver {

    final String TAG = DownloadListener.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager dm  = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            long[] id = PreferenceHelper.getSavedDownloadEnqueueId(context);
            query.setFilterById(id);
            Cursor c = dm.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                Log.d(TAG, "COLUMMMMMMMNNNNNNNNN "+columnIndex);

            }
        }
    }
}
