package com.apptmyz.assessment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.databinding.ActivityMainBinding;
import com.apptmyz.assessment.helper.AudienceProgress;
import com.apptmyz.assessment.helper.CircleImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private long back_pressed;
    private DataSharedPref dataSharedPref;
    private ActivityMainBinding binding;
    private Intent intent;

    CircleImageView imgProfile;
    public AudienceProgress progress;
    TextView tvName, tvRank, tvScore, tvCoin, tvTotalQue, tvCorrect, tvInCorrect, tvCorrectP, tvInCorrectP;
    String totalQues = "100";
    String correctQues = "40";
    String inCorrectQues = "60";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dataSharedPref = new DataSharedPref(this);
//        setUpToolbar();
        getUser();
        dashboard();

    }

    private void getUser() {
        tvName = findViewById(R.id.tvName);
        //imgProfile.setImageUrl(dataSharedPref.getSession("username"), imageLoader);
        tvName.setText(dataSharedPref.getSession("username"));
    }

    private void dashboard() {
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        tvScore = findViewById(R.id.tvScore);
        tvCoin = findViewById(R.id.tvCoin);

        tvTotalQue = findViewById(R.id.tvAttended);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvInCorrect = findViewById(R.id.tvInCorrect);
        tvCorrectP = findViewById(R.id.tvCorrectP);
        tvInCorrectP = findViewById(R.id.tvInCorrectP);
        progress = findViewById(R.id.progress);

        tvTotalQue.setText(totalQues);
        tvCorrect.setText(correctQues);
        tvInCorrect.setText(inCorrectQues);

        float percentCorrect = (Float.parseFloat(correctQues) * 100) / Float.parseFloat(totalQues);
        float percentInCorrect = (Float.parseFloat(inCorrectQues) * 100) / Float.parseFloat(totalQues);
        int perctn = Math.round(percentCorrect);
        int perctnincorrect = Math.round(percentInCorrect);
        tvCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_green, 0, 0, 0);
        tvInCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red, 0, 0, 0);
        tvCorrectP.setText(perctn + "%");
        tvInCorrectP.setText(perctnincorrect + "%");
        progress.SetAttributesForStatistics(getApplicationContext());

        progress.setMaxProgress(Integer.parseInt(String.valueOf(100)));
        progress.setCurrentProgress(Integer.parseInt(String.valueOf(50)));
    }

    private void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //custom drawwe layout
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_baseline_person_4_24);
        actionBarDrawerToggle.setToolbarNavigationClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        //close custom drawer layout
        actionBarDrawerToggle.syncState();
        // set click listner
        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    break;
//                case R.id.nav_home2:
//                    intent = new Intent(getApplicationContext(), MainActivity2.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    break;
//                case R.id.nav_mytest:
//                    intent = new Intent(getApplicationContext(), MyContestActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    break;
//                case R.id.nav_testdesign:
//                    intent = new Intent(getApplicationContext(), TestDesignActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    break;
//                case R.id.nav_newtest:
//                    intent = new Intent(getApplicationContext(), CreateTestActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    break;

                case R.id.nav_logout:
                    dataSharedPref = new DataSharedPref(this);
                    dataSharedPref.logout();

                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    break;
//
//                case R.id.nav_newtest2:
//                    intent = new Intent(getApplicationContext(), MyContestActivity2.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    break;

               /* case R.id.nav_hindi:
                    Toast.makeText(MainActivity.this, "Click Langauge", Toast.LENGTH_SHORT).show();
                    break;*/
            }
            return true;
        });

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
            moveTaskToBack(true);
        } else {
            View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, "Press Again to Exit", Snackbar.LENGTH_LONG).show();
            back_pressed = System.currentTimeMillis();
        }

    }
}