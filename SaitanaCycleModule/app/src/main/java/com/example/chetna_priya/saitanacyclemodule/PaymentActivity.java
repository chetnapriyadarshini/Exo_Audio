package com.example.chetna_priya.saitanacyclemodule;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chetna_priya.saitanacyclemodule.backend.JSONService;
import com.example.chetna_priya.saitanacyclemodule.backend.NetClientPOST;

public class PaymentActivity extends AppCompatActivity implements NetClientPOST.OnResultCallback{

    private static final String ACCESS_TOKEN_EXTRA = "access_token";
    private static final String CREDIT_CARD_OBJ = "credit_card";
    private final int RESULT_CODE_SUCCESS = 200;
    private String accessToken;
    EditText cardNum, name, securitycode;
    Button submitBtn;
    private CreditCardObj creditCardObj;
    private int cardNumSize = 16;
    private int securitycodeSize = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if(getIntent().hasExtra(ACCESS_TOKEN_EXTRA)){
            accessToken = getIntent().getStringExtra(ACCESS_TOKEN_EXTRA);
        }
        else if(savedInstanceState.containsKey(ACCESS_TOKEN_EXTRA)){
            accessToken = savedInstanceState.getString(ACCESS_TOKEN_EXTRA);
        }
        cardNum = (EditText) findViewById(R.id.enter_card_number);
        cardNum.addTextChangedListener(new CardWatcher(cardNumSize));
        name = (EditText) findViewById(R.id.enter_name);
        securitycode = (EditText) findViewById(R.id.enter_code);
        securitycode.addTextChangedListener(new CardWatcher(securitycodeSize));
        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                creditCardObj = new CreditCardObj("1234567890123456", "Chetna", "22/16","764");
                if(accessToken != null) {
                    JSONService jsonService = new JSONService();
                    jsonService.authenticatePayment(accessToken, creditCardObj,PaymentActivity.this);
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CREDIT_CARD_OBJ, creditCardObj);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResult(int resultCode, String output) {
        if(resultCode == RESULT_CODE_SUCCESS){
            Snackbar.make(submitBtn, getString(R.string.payment_success), Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(submitBtn, getString(R.string.authentication_unsuccess), Snackbar.LENGTH_LONG).show();
        }
    }
}
