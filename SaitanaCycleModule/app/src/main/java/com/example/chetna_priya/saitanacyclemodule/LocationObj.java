package com.example.chetna_priya.saitanacyclemodule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chetna_priya on 9/8/2016.
 */
public class LocationObj implements Parcelable{

    private double latitude, longitude;

    public LocationObj(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }


    protected LocationObj(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LocationObj> CREATOR = new Creator<LocationObj>() {
        @Override
        public LocationObj createFromParcel(Parcel in) {
            return new LocationObj(in);
        }

        @Override
        public LocationObj[] newArray(int size) {
            return new LocationObj[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
