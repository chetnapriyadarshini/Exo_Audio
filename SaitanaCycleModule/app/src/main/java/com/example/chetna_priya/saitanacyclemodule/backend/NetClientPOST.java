package com.example.chetna_priya.saitanacyclemodule.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.chetna_priya.saitanacyclemodule.CreditCardObj;
import com.example.chetna_priya.saitanacyclemodule.User;

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
public class NetClientPOST extends AsyncTask<Void,Void, Void>{


    private static final String TAG = NetClientPOST.class.getSimpleName();
    private static final String ACCESS_TOKEN = "accessToken";
    private CreditCardObj creditCardObject;
    private String accessToken = "";

    private String fetchUrl;
    private final CopyOnWriteArrayList<OnResultCallback> listeners;

   // private static final String SUCCESS_RESULT = "Success";
   // private static final String FAILURE_RESULT = "Failure";

    public final static int REQUEST_CODE_REGISTER_USER = 0;
    public final static int REQUEST_CODE_AUTHENTICATE_USER = 1;
    public final static int REQUEST_CODE_AUTHENTICATE_PAYMENT = 2;


    private final int RESULT_CODE = 200;
    private int resultCode = -1;
    private String result;
    private int requestCode = -1;
    User user;

    public interface OnResultCallback {
        void onResult(int resultCode, String output);
    }

    public NetClientPOST(User user,String url ,Context context, int requestCode){
        this.fetchUrl = url;
        listeners = new CopyOnWriteArrayList<>();
        addListener((OnResultCallback) context);
        this.requestCode = requestCode;
        this.user = user;
    }

    public NetClientPOST(String accessToken, CreditCardObj creditCardObj, String url, Context context, int requestCode){
        this.fetchUrl = url;
        listeners = new CopyOnWriteArrayList<>();
        addListener((OnResultCallback) context);
        this.requestCode = requestCode;
        this.accessToken = accessToken;
        this.creditCardObject = creditCardObj;
    }

    public void addListener(OnResultCallback onResultCallback){
        listeners.add(onResultCallback);
    }

    public void removeListener(OnResultCallback onResultCallback){
        listeners.remove(onResultCallback);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonObject = new JSONObject();

            switch(requestCode){
                case REQUEST_CODE_REGISTER_USER:
                case REQUEST_CODE_AUTHENTICATE_USER:
                    try {
                        jsonObject.put("email", user.getLogin_email());
                        jsonObject.put("password", user.getLogin_password());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Accessing localhostttttttttttttttt "+jsonObject);
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonObject.toString().getBytes());
                    os.flush();
                    break;

                case REQUEST_CODE_AUTHENTICATE_PAYMENT:
                    conn.setRequestProperty("Authorization", accessToken);
                    try {
                        jsonObject.put("number",creditCardObject.getCardNumber());
                        jsonObject.put("name", creditCardObject.getName());
                        jsonObject.put("expiration",creditCardObject.getExpirationDate());
                        jsonObject.put("code", creditCardObject.getSecurityCode());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Accessing localhostttttttttttttttt "+jsonObject);
                    OutputStream os2 = conn.getOutputStream();
                    os2.write(jsonObject.toString().getBytes());
                    os2.flush();
                    break;
            }

            Log.d(TAG, "Read Overrrr hereeeeeeeeeeee");
            resultCode = conn.getResponseCode();

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
            switch (requestCode){
                case REQUEST_CODE_AUTHENTICATE_PAYMENT:
                    result = totalOutput;
                    break;
                case REQUEST_CODE_AUTHENTICATE_USER:
                case REQUEST_CODE_REGISTER_USER:
                    try {
                        JSONObject accessObj = new JSONObject(totalOutput);
                        result = accessObj.getString(ACCESS_TOKEN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
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
                listener.onResult(resultCode, result);
            }
     return null;
    }
}
