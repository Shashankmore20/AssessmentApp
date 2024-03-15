package com.apptmyz.assessment.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.Profile;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.fragment.HomeFragment;
import com.apptmyz.assessment.fragment.PerformanceFragment;
import com.apptmyz.assessment.fragment.PofileFrragment;
import com.apptmyz.assessment.fragment.ScheduleFragment;
import com.apptmyz.assessment.fragment.StudyFragment;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private final int ID_HOME = 1;
    private final int ID_STUDY = 2;
    private final int ID_PERFORMANCE = 3;
    private final int SCHEDULE = 4;
    private final int PROFILE = 5;
    private DataSource dataSource;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout drawerLayout2;
    private BottomNavigationView bottom_navigation;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Intent intent;
    private DataSharedPref dataSharedPref;

    ImageView notification_iv;

    TextView toolBar_title;

    TextView name_tv, class_tv;
    ImageView arrow_iv;

    UserDetails userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_new);

        dataSharedPref = new DataSharedPref(this);
        dataSource = new DataSource(this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        Util.logD("userdata");
        Util.logD(userdata);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.notification_iv:
                        Intent intent = new Intent(MainActivity2.this, NotificationActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

//        notification_iv = findViewById(R.id.notification_iv);
//        notification_iv.setOnClickListener(listener);

        loadStudentsSubjectsData();
        loadComplexityData();
        loadSubjectTests();

//        toolBar_title = findViewById(R.id.toolbar_title);

        bottom_navigation = findViewById(R.id.bottomNavigationView);
        drawerLayout2 = findViewById(R.id.drawerLayout);

        bottom_navigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home_nav:
                        selectedFragment = new HomeFragment();
                        drawerLayout2.setBackgroundColor(Color.WHITE);
                        break;
                    case R.id.discover_nav:
                        selectedFragment = new StudyFragment();
                        drawerLayout2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.activity_backcolor));
                        break;
                    case R.id.performance_nac:
                        selectedFragment = new PerformanceFragment();
                        drawerLayout2.setBackgroundColor(Color.WHITE);
                        break;
                    case R.id.del_nav:
                        selectedFragment = new ScheduleFragment();
                        drawerLayout2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.activity_backcolor));
                        break;
                    case R.id.utility_nav:
                        selectedFragment = new ProfileFrragment();
                        drawerLayout2.setBackgroundColor(Color.WHITE);
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            }
        });
//        bottom_navigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
//        bottom_navigation.add(new MeowBottomNavigation.Model(ID_STUDY, R.drawable.ic_book));
//        bottom_navigation.add(new MeowBottomNavigation.Model(ID_PERFORMANCE, R.drawable.ic_baseline_auto_graph_24));
//        bottom_navigation.add(new MeowBottomNavigation.Model(SCHEDULE, R.drawable.ic_twotone_perm_contact_calendar_24));
//        bottom_navigation.add(new MeowBottomNavigation.Model(PROFILE, R.drawable.ic_twotone_person_24));


//        bottom_navigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
//            @Override
//            public void onShowItem(MeowBottomNavigation.Model item) {
//               Fragment selectedFragment = null;
//               switch (item.getId()) {
//                   case 1:
//                       selectedFragment = new HomeFragment();
//                       toolBar_title.setText("HOME");
//                       break;
//                   case 2:
//                       selectedFragment = new StudyFragment();
//                       toolBar_title.setText("Study");
//                       break;
//                   case 3:
//                       selectedFragment = new PerformanceFragment();
//                       toolBar_title.setText("Performance");
//                       break;
//                   case 4:
//                       selectedFragment = new ScheduleFragment();
//                       toolBar_title.setText("Schedule");
//                       break;
//                   case 5:
//                       selectedFragment = new ProfileFrragment();
//                       toolBar_title.setText("Profile");
//                       break;
//               }
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, selectedFragment)
//                        .commit();
//            }
//        });
//
//        bottom_navigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
//            @Override
//            public void onClickItem(MeowBottomNavigation.Model item) {
//
//            }
//        });
//
//        bottom_navigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
//            @Override
//            public void onReselectItem(MeowBottomNavigation.Model item) {
//
//            }
//        });
//
//        bottom_navigation.show(ID_HOME, true);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                new HomeFragment()).commit();

//        setUpToolbar();
    }

    public void loadStudentsSubjectsData() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading Data...", true);
        dialog.show();

//        userDetails = UserDetails.getInstance();


        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            System.out.println("url " + App.SUBJECTS_TOPICS_URL + userDetails.getUserClass());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    App.SUBJECTS_TOPICS_URL + userDetails.getUserClass(), null,

                    response -> {
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                dataSource.subjects.clearAll();
                                dataSource.topics.clearAll();

                                JSONArray subjectsJsonArray = new JSONArray(obj.getString("data"));

                                for (int i = 0; i < subjectsJsonArray.length(); i++) {
                                    JSONObject subject = subjectsJsonArray.getJSONObject(i);
                                    dataSource.subjects.addSubjects(subject.getLong("id"), subject.getString("subjectName"), subject.getString("subjectDescription"), subject.getString("fileName"));

                                    JSONArray topicsJsonArray = new JSONArray(subject.getString("topicList"));

                                    for (int j = 0; j < topicsJsonArray.length(); j++) {
                                        JSONObject topics = topicsJsonArray.getJSONObject(j);
//                                        System.out.println("--------------------");
//                                        System.out.println(topics);
//                                        Log.i("TOPICS", topics.toString());
                                        dataSource.topics.addTopics(topics.getLong("id"), topics.getLong("subjectId"), topics.getString("topicName"), topics.getString("topicDescription"));
                                    }
                                }

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

                            }
                            //TODO failed status
                            else if (status.equals("failed")) {

                            } else {
                                Toast.makeText(MainActivity2.this, "loading subjects Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    System.out.println();
                    return headers;
                }
            };
            Log.e("Subjects: ", "response");
//            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadComplexityData() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading Data...", true);
        dialog.show();

//        userDetails = UserDetails.getInstance();


        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            System.out.println("url " + App.COMPLEXITY_MASTER_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    App.COMPLEXITY_MASTER_URL, null,

                    response -> {
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                dataSource.complexity.clearAll();
                                JSONArray complexJsonArray = new JSONArray(obj.getString("data"));

                                for (int i = 0; i < complexJsonArray.length(); i++) {
                                    JSONObject subject = complexJsonArray.getJSONObject(i);
                                    dataSource.complexity.addComplexity(subject.getLong("id"), subject.getString("level"));
                                }
                            }
                            //TODO failed status
                            else if (status.equals("failed")) {

                            } else {
                                Toast.makeText(MainActivity2.this, "loading complexity Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    System.out.println();
                    return headers;
                }
            };
            Log.e("Complexity: ", "response");
//            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
        public void onBackPressed() {
//        dataSharedPref= new DataSharedPref(this);
//        try {
//            dataSharedPref.stayOnScreen();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//            AlertDialog dialog= new AlertDialog.Builder(getApplicationContext())
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("Alert")
//                    .setMessage("Do you want to logout?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                            startActivity(intent);
////                            DataSharedPref dataSharedPref = new DataSharedPref(getApplicationContext());
////                            dataSharedPref.logout();
////                            finish();
//                        }
//                    })
//                    .setNegativeButton("No", null)
////                    .setOnDismissListener()
//                    .show();
//
//        Button nega = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        nega.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                startActivity(intent);
//            }
//        });
//
//        DataSharedPref dataSharedPref = new DataSharedPref(this);
//        dataSharedPref.logout();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Alert")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
//        super.onBackPressed();
//        finishAffinity();

    }

    private void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //custom drawwe layout
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.);
        actionBarDrawerToggle.setToolbarNavigationClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        //close custom drawer layout
        actionBarDrawerToggle.syncState();
        // set click listner
//        navigationView = findViewById(R.id.navigation_menu);

        /*
        View headerView = navigationView.getHeaderView(0);


        name_tv = headerView.findViewById(R.id.name_tv);
        class_tv = headerView.findViewById(R.id.class_tv);
        arrow_iv = headerView.findViewById(R.id.arrow_iv);

        */

        name_tv.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        class_tv.setText(userDetails.getUserClass() + "");
        arrow_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });

        /*
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(getApplicationContext(), MainActivity2.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_logout:
                    dataSharedPref = new DataSharedPref(this);
                    dataSharedPref.logout();
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Do you want to logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                    drawerLayout.closeDrawers();
                    break;

            }
            return true;
        });
        */

    }

    public void loadSubjectTests() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading Data...", true);
        dialog.show();
        TopicSummaryModel topicSummaryModel = new TopicSummaryModel();

        Map model = new HashMap();
        model.put("userId", userDetails.getUserId());
        model.put("subjectId", null);
        model.put("topicId", null);

        System.out.print(model);

        ObjectMapper mapper = new ObjectMapper();


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        try {

            String data = mapper.writeValueAsString(model);

            System.out.println("url " + App.TOPIC_SUMMARY_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.TOPIC_SUMMARY_URL, new JSONObject(data),

                    response -> {
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                dataSource.topicSummary.clearTopicSummary();
                                dataSource.topicTestsSummary.clearTestsSummary();

                                JSONObject topicSummaryJsonObject = new JSONObject(obj.getString("data"));

                                topicSummaryModel.setSubjectId(0L);
                                topicSummaryModel.setTopicId(0L);
                                topicSummaryModel.setTotalQuestions(topicSummaryJsonObject.getInt("totalQuestions"));
                                topicSummaryModel.setTotalQuestionsAttempted(topicSummaryJsonObject.getInt("totalAttemptedQuestions"));
                                topicSummaryModel.setTotalCorrectQuestions(topicSummaryJsonObject.getInt("totalCorrectQuestions"));
                                topicSummaryModel.setAveragePercentage(topicSummaryJsonObject.getInt("averagePercentage"));
                                if (!topicSummaryJsonObject.isNull("averageDuration"))
                                    topicSummaryModel.setAverageDuration(topicSummaryJsonObject.getLong("averageDuration"));

                                JSONArray testsJsonArray = topicSummaryJsonObject.getJSONArray("testList");

                                ArrayList<TestSummaryModel> testSummaryModelArrayList = new ArrayList<>();
                                TestSummaryModel testSummaryModel;

                                for (int i = 0; i < testsJsonArray.length(); i++) {
                                    JSONObject testJsonObject = testsJsonArray.getJSONObject(i);
                                    testSummaryModel = new TestSummaryModel();
                                    testSummaryModel.setTestId(testJsonObject.getLong("testId"));
                                    testSummaryModel.setTestDate(testJsonObject.getString("testDate"));
                                    if (testJsonObject.isNull("testDuration")) {
                                        testSummaryModel.setTestDuration(0L);
                                    } else {
                                        testSummaryModel.setTestDuration(testJsonObject.getLong("testDuration"));

                                    }
                                    testSummaryModel.setStartTime(testJsonObject.getString("startTime"));
                                    testSummaryModel.setEndTime(testJsonObject.getString("endTime"));
                                    if (testJsonObject.isNull("testComplexity")) {
                                        testSummaryModel.setTestComplexity(0);
                                    } else {
                                        testSummaryModel.setTestComplexity(testJsonObject.getInt("testComplexity"));
                                    }
                                    testSummaryModel.setSubjectId(testJsonObject.getLong("subjectId"));
                                    testSummaryModel.setTopicId(testJsonObject.getLong("topicId"));
                                    if (testJsonObject.isNull("totalQuestions")) {
                                        testSummaryModel.setTotalQuestions(0);
                                    } else {
                                        testSummaryModel.setTotalQuestions(testJsonObject.getInt("totalQuestions"));
                                    }
                                    if (testJsonObject.isNull("totalCorrectAnswers")) {
                                        testSummaryModel.setTotalCorrectAnswers(0);
                                    } else {
                                        testSummaryModel.setTotalCorrectAnswers(testJsonObject.getInt("totalCorrectAnswers"));
                                    }
                                    if (testJsonObject.isNull("totalWrongAnswers")) {
                                        testSummaryModel.setTotalWrongAnswers(0);
                                    } else {
                                        testSummaryModel.setTotalWrongAnswers(testJsonObject.getInt("totalWrongAnswers"));
                                    }
                                    if (testJsonObject.isNull("avgDuration")) {
                                        testSummaryModel.setAvgDuration(0L);
                                    } else {
                                        testSummaryModel.setAvgDuration(testJsonObject.getLong("avgDuration"));
                                    }

                                    testSummaryModelArrayList.add(testSummaryModel);
                                }
                                topicSummaryModel.setTotalAttemptedTests(testSummaryModelArrayList.size());
                                topicSummaryModel.setTestSummaryModelArrayList(testSummaryModelArrayList);

                                dataSource.topicSummary.addTopicSummary(topicSummaryModel);


                            } else if (status.equals("failed")) {

                            } else {
                                Toast.makeText(getApplicationContext(), "loading subjects Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(MainActivity2.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };
            Log.e("TopicSummary: ", "response");
            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}