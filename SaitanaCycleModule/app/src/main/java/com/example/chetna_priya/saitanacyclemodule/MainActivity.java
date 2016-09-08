package com.example.chetna_priya.saitanacyclemodule;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chetna_priya.saitanacyclemodule.backend.JSONService;
import com.example.chetna_priya.saitanacyclemodule.backend.NetClientPOST;

public class MainActivity extends AppCompatActivity implements NetClientPOST.OnResultCallback {

    private static final String ACCESS_TOKEN_EXTRA = "access_token";
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int RESULT_CODE_SUCCESS = 200;
    private final int RESULT_CODE_FAIL = 1;
    private TextView tview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tview = (TextView) findViewById(R.id.tv_hello);
        JSONService jsonService = new JSONService();
        jsonService.authenticateUser("crossover@crossover.com", "crossover", this);
    }

    @Override
    public void onResult(int resultCode, String token) {
        if(resultCode == RESULT_CODE_SUCCESS){
          //  tview.setText("Success");
            Log.d(TAG, "OUTPUTTTTTTTTTTTT"+token);
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(ACCESS_TOKEN_EXTRA, token);
            startActivity(intent);
        }else{
            makeToast(resultCode);
        }
    }

    private void makeToast(int resultCode) {
        String message = "";
        switch (resultCode){
            case 401:
                message =  "User is unauthorized";
                break;
            default:
                message = "An internal error occured, please try again";
        }
        Snackbar.make(tview, message, Snackbar.LENGTH_LONG).show();

    }
}
