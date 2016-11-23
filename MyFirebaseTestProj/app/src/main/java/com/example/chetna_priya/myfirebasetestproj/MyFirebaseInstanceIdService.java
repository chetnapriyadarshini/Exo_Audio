package com.example.chetna_priya.myfirebasetestproj;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by chetna_priya on 10/17/2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String currentToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(currentToken);
    }

    private void sendRegistrationToServer(String currentToken) {

    }
}
