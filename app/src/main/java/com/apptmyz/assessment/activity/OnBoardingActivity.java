package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.ViewPagerAdapter;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class OnBoardingActivity extends AppCompatActivity {

    private CircleIndicator3 indicator3;
    private ViewPager2 viewPager2;
    private TextView login, imageText;
    private Button signup;
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private Handler sliderHandler = new Handler();
    private String[] imageDescriptions = new String[]{
            "Marks",
            "Time Table",
            "Attendance"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        indicator3 = findViewById(R.id.indicator);
        viewPager2 = findViewById(R.id.view_pager);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        imageText = findViewById(R.id.imageText);

        arrayList.add(R.drawable.slide2);
        arrayList.add(R.drawable.slide1);
        arrayList.add(R.drawable.slide3);

        login.setOnClickListener(view -> goToLoginScreen());
        signup.setOnClickListener(view -> goToRegistrationScreen());

        ViewPagerAdapter adapter = new ViewPagerAdapter(OnBoardingActivity.this,arrayList);
        viewPager2.setAdapter(adapter);

        indicator3.setViewPager(viewPager2);



        Runnable slideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int totalItems = arrayList.size();
                int nextItem = (currentItem + 1) % totalItems;
                viewPager2.setCurrentItem(nextItem);
            }
        };

        // A runnable with a delay of 10 seconds
        sliderHandler.postDelayed(slideRunnable, 10000);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(slideRunnable);
                sliderHandler.postDelayed(slideRunnable, 10000);
                imageText.setText(imageDescriptions[position]);
                // Checking if the last image is reached in the pager
                if (position == arrayList.size() - 1) {
                    // Return to first page
                    sliderHandler.postDelayed(() -> viewPager2.setCurrentItem(0, false), 10000);
                } else {
                    // Normal scroll
                    sliderHandler.postDelayed(slideRunnable, 10000);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to avoid memory leaks
        sliderHandler.removeCallbacksAndMessages(null);
    }

    private void goToRegistrationScreen(){
        Intent intent = new Intent(OnBoardingActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void goToLoginScreen(){
        Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}