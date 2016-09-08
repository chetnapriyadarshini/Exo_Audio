package com.example.chetna_priya.saitanacyclemodule.backend;

/**
 * Created by chetna_priya on 9/8/2016.
 */

import java.io.Serializable;

public class User implements Serializable{

    private String login_email;
    private String login_password;

    public User(String email, String password){
        setLogin_email(email);
        setLogin_password(password);
    }


    public String getLogin_email() {
        return login_email;
    }
    public void setLogin_email(String login_email) {
        this.login_email = login_email;
    }

    public String getLogin_password() {
        return login_password;
    }

    public void setLogin_password(String login_password) {
        this.login_password = login_password;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof User))
            return false;
        User user = (User) obj;
        if(login_email.equals(user.getLogin_email())
                && login_password.equals(user.getLogin_password())){
            return true;
        }
        return false;
    }
}
