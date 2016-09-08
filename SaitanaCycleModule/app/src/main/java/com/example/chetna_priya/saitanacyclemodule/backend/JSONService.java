package com.example.chetna_priya.saitanacyclemodule.backend;

import android.content.Context;

import com.example.chetna_priya.saitanacyclemodule.CreditCardObj;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by chetna_priya on 9/8/2016.
 */
public class JSONService  {

    private String url = "http://192.168.43.205:8080/api/v1/";
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void registerUser(String userEmail, String userPassword, Context context){
        String regUrl = url.concat("register");
        User user = new User(userEmail, userPassword);
        new NetClientPOST(user,regUrl,context,NetClientPOST.REQUEST_CODE_REGISTER_USER).execute();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void authenticateUser(String userEmail, String userPassword, Context context){
        String authUrl = url.concat("auth");
        User user = new User(userEmail, userPassword);
        new NetClientPOST(user,authUrl, context, NetClientPOST.REQUEST_CODE_AUTHENTICATE_USER).execute();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void authenticatePayment(String accessToken, CreditCardObj creditCardObj, Context context){
        String authUrl = url.concat("rent");
        new NetClientPOST(accessToken,creditCardObj,authUrl, context, NetClientPOST.REQUEST_CODE_AUTHENTICATE_PAYMENT).execute();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getPlaces(String accessToken, Context context){
        String placeurl = url.concat("places");
        new NetClientGET(placeurl,context).execute(accessToken);
    }

}
