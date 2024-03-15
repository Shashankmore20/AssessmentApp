package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.utils.Constants;
import com.google.gson.Gson;

public class ProfileActivity extends AppCompatActivity {

    TextView fullName_tv, loginName_tv, dob_tv, email_tv, number_tv;

    TextView name_tv, class_tv;

    TextView textView;

    UserDetails userDetails;
    DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dataSource = new DataSource(this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
//        userDetails = UserDetails.getInstance();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.textView:
                        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        name_tv = findViewById(R.id.name_tv);
        class_tv = findViewById(R.id.class_tv);
        name_tv.setText(userDetails.getFirstName());
        class_tv.setText("Class " + userDetails.getUserClass() + "");

        fullName_tv = findViewById(R.id.fullName_tv);
        loginName_tv = findViewById(R.id.loginName_tv);
        dob_tv = findViewById(R.id.dob_tv);
        email_tv = findViewById(R.id.email_tv);
        number_tv = findViewById(R.id.number_tv);

        fullName_tv.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        loginName_tv.setText(userDetails.getLoginName());
        dob_tv.setText(userDetails.getDob());
        email_tv.setText(userDetails.getEmailId());
        number_tv.setText(userDetails.getContactNumber());

        initializeUI();
        textView = findViewById(R.id.textView);
        textView.setText("Edit Profile");
        textView.setOnClickListener(listener);

    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}