package com.example.chetna_priya.saitanacyclemodule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chetna_priya on 9/8/2016.
 */
public class CreditCardObj implements Parcelable{

    private String cardNumber;
    private String name;
    private String expirationDate;
    private String securityCode;

    public CreditCardObj(String cardNumber, String name, String expirationDate, String securityCode){
        this.cardNumber = cardNumber;
        this.name = name;
        this.expirationDate = expirationDate;
        this.securityCode = securityCode;
    }

    protected CreditCardObj(Parcel in) {
        cardNumber = in.readString();
        name = in.readString();
        expirationDate = in.readString();
        securityCode = in.readString();
    }

    public static final Creator<CreditCardObj> CREATOR = new Creator<CreditCardObj>() {
        @Override
        public CreditCardObj createFromParcel(Parcel in) {
            return new CreditCardObj(in);
        }

        @Override
        public CreditCardObj[] newArray(int size) {
            return new CreditCardObj[size];
        }
    };

    public String getCardNumber() {
        return cardNumber;
    }

    public String getName() {
        return name;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cardNumber);
        parcel.writeString(name);
        parcel.writeString(expirationDate);
        parcel.writeString(securityCode);
    }
}
