package com.example.chetna_priya.saitanacyclemodule;

import android.app.Application;
import android.test.ApplicationTestCase;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    Client client;
    private String REST_SERVICE_URL = "http://192.168.43.205:8080/api/v1/";

    public ApplicationTest() {
        super(Application.class);
    }

    private void init(){
        this.client = ClientBuilder.newClient();
    }

    public static void main(String[] args){
        ApplicationTest test = new ApplicationTest();
        test.init();
        test.testRegisterUser();
        test.testLoginUser();
        test.testGetPlaces();
        test.testMakePayment();
    }

    private void testMakePayment() {

    }

    private void testGetPlaces() {

    }

    private void testLoginUser() {
    }


    private void testRegisterUser() {
        String url = REST_SERVICE_URL.concat("register");
    }
}