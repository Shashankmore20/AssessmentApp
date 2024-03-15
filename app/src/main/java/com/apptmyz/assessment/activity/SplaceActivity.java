package com.apptmyz.assessment.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;

public class SplaceActivity extends AppCompatActivity {

    private DataSharedPref session;
    ProgressBar splashProgress;
    int SPLASH_TIME = 3000;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splace);
        splashProgress = findViewById(R.id.splashProgress);
        playProgress();
        session = new DataSharedPref(this);
        dataSource = new DataSource(this);
        initializeAuth();

    }


    public void initializeAuth() {
        Handler handler = new Handler();
        handler.postDelayed(this::checkCurrentUser, 2500);
    }

    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(2500)
                .start();
    }

    public void checkCurrentUser() {
        Log.d("LOGINFLOW", "IN checkCurrentUser");
        String logout = dataSource.sharedPreferences.getValue(Constants.LOGOUT_PREF);

//        if (session.getSession("token"
//
//        ).length() != 0) {
//            Log.d("LOGINFLOW", "USER SESSION BLOCK " + session.getSession("username"));
//            Intent intent = new Intent(SplaceActivity.this, MainActivity2.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Log.d("LOGINFLOW", "USER NOT LOGIN");
//            Intent intent = new Intent(SplaceActivity.this, OnBoardingActivity.class);
//            startActivity(intent);
//            finish();
//        }

        Intent intent = null;
        if (Util.isValidString(logout) && logout.equals(Constants.FALSE)) {
            intent = new Intent(this, MainActivity2.class);
            finish();
        } else {
            intent = new Intent(this, OnBoardingActivity.class);
            finish();
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

}