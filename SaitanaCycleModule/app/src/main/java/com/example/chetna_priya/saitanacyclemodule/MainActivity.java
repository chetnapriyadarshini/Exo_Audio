package com.example.chetna_priya.saitanacyclemodule;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chetna_priya.saitanacyclemodule.backend.JSONService;
import com.example.chetna_priya.saitanacyclemodule.backend.NetClientPOST;

public class MainActivity extends AppCompatActivity implements NetClientPOST.OnResultCallback {

    private static final String ACCESS_TOKEN_EXTRA = "access_token";
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int RESULT_CODE_SUCCESS = 200;
    private final int RESULT_CODE_FAIL = 1;
    EditText email, passowrd;
    private int email_allowed_length = 128;
    private int passowrd_allowed_length = 32;
    Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.enter_email);
        email.addTextChangedListener(new CardWatcher(email_allowed_length));
        passowrd = (EditText) findViewById(R.id.enter_password);
        passowrd.addTextChangedListener(new CardWatcher(passowrd_allowed_length));
        login = (Button) findViewById(R.id.login_btn);
        register = (Button) findViewById(R.id.register_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONService jsonService = new JSONService();
                jsonService.authenticateUser(email.getText().toString(), passowrd.getText().toString(), MainActivity.this);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                JSONService jsonService = new JSONService();
                jsonService.registerUser(email.getText().toString(), passowrd.getText().toString(), MainActivity.this);
            }
        });
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
        Snackbar.make(login, message, Snackbar.LENGTH_LONG).show();

    }
}
