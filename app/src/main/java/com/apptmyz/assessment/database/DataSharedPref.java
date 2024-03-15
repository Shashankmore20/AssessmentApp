package com.apptmyz.assessment.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptmyz.assessment.activity.MainActivity;

public class DataSharedPref {

    private final SharedPreferences sharedpreferences;
    private final Context context;
    public static final String MYSESSION = "session";
    private String sessionValue = null;

    public DataSharedPref(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(MYSESSION, Context.MODE_PRIVATE);

    }

    public String getSession(String key) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        sessionValue = sharedpreferences.getString(key, "");
        return sessionValue;
    }

    public void getAllsession() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        sharedpreferences.getAll();
    }

    public void setSession(String id, String username, String email, String accountNonExpired,
                           String accountNonLocked, String credentialsNonExpired, String enabled,
                           String token) {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("userid", id);
        editor.putString("firstname", username);
        editor.putString("lastname", email);
        editor.putString("loginname", accountNonExpired);
        editor.putString("emailid", accountNonLocked);
        editor.putString("dob", credentialsNonExpired);
        editor.putString("gender", enabled);
        editor.putString("profilePicture", token);
        editor.commit();
    }

    public void logout() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MYSESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    /*
    public void stayOnScreen() throws InterruptedException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYSESSION,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();
        editor.wait();

    }

   /* public void close(View view) {
        context.finish();
    }*/


}
