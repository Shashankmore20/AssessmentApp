package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.apptmyz.assessment.R;

public class NotificationActivity extends AppCompatActivity {

    int notications_count = 0;

    RelativeLayout noNotification_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initializeUI();

        noNotification_rl = findViewById(R.id.noNotification_rl);

        if(notications_count == 0){
            noNotification_rl.setVisibility(View.VISIBLE);
        }


    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}