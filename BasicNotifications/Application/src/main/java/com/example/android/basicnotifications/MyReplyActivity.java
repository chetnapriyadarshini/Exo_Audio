package com.example.android.basicnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by chetna_priya on 8/9/2016.
 */
public class MyReplyActivity  extends Activity {

    private static final String TAG = MyReplyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IN MyReplyActivity ");
        TextView replyView = (TextView) findViewById(R.id.tv_reply);
        String message = (String) getMessageText(getIntent());
        Log.d(TAG, "Message Receivedddddddddddd");
        if(message != null)
         replyView.setText(message);
        else
            replyView.setText("NO MEssage REceiveddddd Sorrry    !!!!!");

    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(MainActivity.EXTRA_VOICE_REPLY);
        }
        return null;
    }
}
