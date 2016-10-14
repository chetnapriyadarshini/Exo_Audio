package com.example.chetna_priya.myexoplayersample;

/**
 * Created by chetna_priya on 9/3/2016.
 */
public class AudioSample {

    String name;
    String url;
    //Bitmap sampleBitmap

    public AudioSample(String name, String url){

        this.name = name;
        this.url = url;
    }


    public String getUrl() {
        return url;
    }


    public String getSampleName() {
        return name;
    }



}
