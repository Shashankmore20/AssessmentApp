package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.apptmyz.assessment.R;

public class SuccessfullScreen extends AppCompatActivity {

    private Button loginScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfull_screen);

        loginScreen = findViewById(R.id.loginScreen);

        loginScreen.setOnClickListener(view -> goToLoginScreen());

    }

    private void goToLoginScreen(){
        Intent intent = new Intent(SuccessfullScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}