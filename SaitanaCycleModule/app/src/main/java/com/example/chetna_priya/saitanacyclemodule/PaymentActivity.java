package com.example.chetna_priya.saitanacyclemodule;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
    Spinner month, year;

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
        month = (Spinner) findViewById(R.id.spinner_month);
        year = (Spinner) findViewById(R.id.spinner_year);
        cardNum = (EditText) findViewById(R.id.enter_card_number);
        cardNum.addTextChangedListener(new CardWatcher(cardNumSize));
        name = (EditText) findViewById(R.id.enter_name);
        securitycode = (EditText) findViewById(R.id.enter_code);
        securitycode.addTextChangedListener(new CardWatcher(securitycodeSize));
        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accessToken != null) {
                    String number = cardNum.getText().toString();
                    String code = securitycode.getText().toString();
                    String names = name.getText().toString();
                    if(validate(names, number, code)) {
                        creditCardObj = new CreditCardObj(number, name.getText().toString(),
                                getExpiryDate(), code);
                        JSONService jsonService = new JSONService();
                        jsonService.authenticatePayment(accessToken, creditCardObj, PaymentActivity.this);
                    }else
                        Snackbar.make(submitBtn, getString(R.string.invalid_name_number_code), Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }

    private boolean validate(String name,String number, String code) {
        if(name.length() == 0)
            return false;
        if(number.length() < 16)
            return false;
        if(code.length() < 4)
            return false;
        return true;
    }

    private String getExpiryDate() {
        return String.format(month.getSelectedItem().toString()+"/"+year.getSelectedItem().toString());
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
