package com.example.chetna_priya.myapplication;

/**
 * Created by chetna_priya on 9/3/2016.
 */
public class Recipe {


    private String name;
    private String description;

    public Recipe(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
