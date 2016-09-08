package com.example.chetna_priya.saitanacyclemodule;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by chetna_priya on 9/8/2016.
 */
public class PlaceObj implements Parcelable {

    private LocationObj location;
    private String id;
    private String name;

    public PlaceObj(@NonNull LocationObj location, String id, String name){
        this.location = location;
        this.id = id;
        this.name = name;
    }


    protected PlaceObj(Parcel in) {
        location = in.readParcelable(LocationObj.class.getClassLoader());
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<PlaceObj> CREATOR = new Creator<PlaceObj>() {
        @Override
        public PlaceObj createFromParcel(Parcel in) {
            return new PlaceObj(in);
        }

        @Override
        public PlaceObj[] newArray(int size) {
            return new PlaceObj[size];
        }
    };

    public LocationObj getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(location, i);
        parcel.writeString(id);
        parcel.writeString(name);
    }
}
