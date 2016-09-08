package com.example.chetna_priya.saitanacyclemodule.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chetna_priya on 9/8/2016.
 */
public class NetClientGET extends AsyncTask<String,Void, Void>{


    private static final String TAG = NetClientGET.class.getSimpleName();
    private final String fetchUrl;
    private final CopyOnWriteArrayList<OnResultCallback> listeners;
    private int resultCode = HttpURLConnection.HTTP_OK;
    private String output;
    private JSONObject accessObj;

    public interface OnResultCallback {
        void onResult(int resultCode, JSONObject output);
    }

    public NetClientGET(String url ,Context context){
        this.fetchUrl = url;
        listeners = new CopyOnWriteArrayList<>();
        addListener((OnResultCallback) context);
    }

    public void addListener(OnResultCallback onResultCallback){
        listeners.add(onResultCallback);
    }

    public void removeListener(OnResultCallback onResultCallback){
        listeners.remove(onResultCallback);
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String accessToken = params[0];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("accessToken", accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Accessing localhostttttttttttttttt "+jsonObject);
            conn.setRequestProperty("Authorization",accessToken);
            conn.setRequestProperty("Accept", "application/json");

            resultCode = conn.getResponseCode();
            Log.d(TAG, "REQUESSSSSSSSSSSSSSSSSSSSSSSSSSSTTTTTTTT "+resultCode);
            if (resultCode != HttpURLConnection.HTTP_OK) {

                switch (resultCode){
                    case 400:
                        Log.d(TAG, "Invalid JSON sent");
                        break;
                    case 401:
                        Log.d(TAG, "User is unauthorized");
                        break;
                    case 403:
                        Log.d(TAG, "Invalid access token sent");
                        break;
                    case 200:
                        Log.d(TAG, "AlL OKKK");
                        break;
                    default:
                        Log.d(TAG, "Error code "+resultCode);
                }

            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            Log.d(TAG,"Output from Server .... \n");
            String output, totalOutput = "";
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                Log.d(TAG, output);
                totalOutput = totalOutput.concat(output);
            }

            try {
                accessObj = new JSONObject(totalOutput);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            resultCode = -1;
            e.printStackTrace();

        } catch (IOException e) {
            resultCode = -1;
            e.printStackTrace();

        }
        for (OnResultCallback listener : listeners) {
            listener.onResult(resultCode, accessObj);
        }
        return null;
    }
}
