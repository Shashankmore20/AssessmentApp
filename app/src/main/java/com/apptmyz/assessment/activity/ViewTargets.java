package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.adapter.TargetAdapter;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.fragment.HomeFragment;
import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.model.TargetModel;
import com.apptmyz.assessment.model.TopicModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTargets extends AppCompatActivity implements TargetCallback {

    ImageView backBtn;
    Context context;
    RecyclerView recyclerView;

    LottieAnimationView noDataAnim;
    UserDetails userDetails;
    DataSource dataSource;
    ArrayList<String> subjectNameList;
    ArrayList<SubjectModel> subjectsList;
    private DataSharedPref dataSharedPref;
    String dailyTargetType = "D";
    String weeklyTargetType = "W";
    String monthlyTargetType = "M";
    Long userId = Long.valueOf(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_targets);

        dataSharedPref = new DataSharedPref(this);
        dataSource = new DataSource(this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        Util.logD("userdata");
        Util.logD(userdata);

        //Get Subjects
        subjectsList = dataSource.subjects.getSubjects();
        subjectNameList = new ArrayList<>();

        for (SubjectModel subject : subjectsList) {
            subjectNameList.add(subject.getSubjectName());
        }

        TextView dailyBtn = findViewById(R.id.dailyBtn);
        TextView weeklyBtn = findViewById(R.id.weeklyBtn);
        TextView monthlyBtn = findViewById(R.id.monthlyBtn);
        backBtn = findViewById(R.id.backBtnTargets);
        noDataAnim = findViewById(R.id.noDataAnim);

        userId = userDetails.getUserId();

        recyclerView = findViewById(R.id.target_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getTargets(ViewTargets.this, dailyTargetType, userId, new TargetCallback() {
            @Override
            public void onTargetsLoaded(ArrayList<TargetModel> targetList) {

                DataSource dataSource = new DataSource(ViewTargets.this);
                TargetAdapter adapter = new TargetAdapter(ViewTargets.this,targetList,dataSource);
                recyclerView.setAdapter(adapter);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update properties for dailyBtn
                dailyBtn.setBackgroundResource(R.drawable.rounded_btn_daily);
                dailyBtn.setTextColor(Color.WHITE);

                // Reset properties for other buttons
                weeklyBtn.setBackgroundResource(0);  // Set background to null
                weeklyBtn.setTextColor(Color.parseColor("#297066"));  // Set text color to #297066
                monthlyBtn.setBackgroundResource(0);
                monthlyBtn.setTextColor(Color.parseColor("#297066"));

                getTargets(ViewTargets.this, dailyTargetType, userId, new TargetCallback() {
                    @Override
                    public void onTargetsLoaded(ArrayList<TargetModel> targetList) {

                        DataSource dataSource = new DataSource(ViewTargets.this);
                        TargetAdapter adapter = new TargetAdapter(context,targetList,dataSource);
                        recyclerView.setAdapter(adapter);
                    }
                });

            }
        });

        weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update properties for weeklyBtn
                weeklyBtn.setBackgroundResource(R.drawable.rounded_btn_daily);
                weeklyBtn.setTextColor(Color.WHITE);

                // Reset properties for other buttons
                dailyBtn.setBackgroundResource(0);
                dailyBtn.setTextColor(Color.parseColor("#297066"));
                monthlyBtn.setBackgroundResource(0);
                monthlyBtn.setTextColor(Color.parseColor("#297066"));

                getTargets(ViewTargets.this, weeklyTargetType, userId, new TargetCallback() {
                    @Override
                    public void onTargetsLoaded(ArrayList<TargetModel> targetList) {

                        DataSource dataSource = new DataSource(ViewTargets.this);
                        TargetAdapter adapter = new TargetAdapter(context,targetList,dataSource);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });

        monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update properties for monthlyBtn
                monthlyBtn.setBackgroundResource(R.drawable.rounded_btn_daily);
                monthlyBtn.setTextColor(Color.WHITE);

                // Reset properties for other buttons
                dailyBtn.setBackgroundResource(0);
                dailyBtn.setTextColor(Color.parseColor("#297066"));
                weeklyBtn.setBackgroundResource(0);
                weeklyBtn.setTextColor(Color.parseColor("#297066"));

                getTargets(ViewTargets.this, monthlyTargetType, userId, new TargetCallback() {
                    @Override
                    public void onTargetsLoaded(ArrayList<TargetModel> targetList) {

                        DataSource dataSource = new DataSource(ViewTargets.this);
                        TargetAdapter adapter = new TargetAdapter(context,targetList,dataSource);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });

    }

    @Override
    public void onTargetsLoaded(ArrayList<TargetModel> targetList) {

        DataSource dataSource = new DataSource(ViewTargets.this);
        TargetAdapter adapter = new TargetAdapter(context,targetList,dataSource);
        recyclerView.setAdapter(adapter);
    }

    public interface DeleteCallback {
        void onDeleteSuccess();
        void onDeleteFailed(String errorMessage);
    }

    private String findSubjectByID(long subjectId) {
        for (SubjectModel subject : subjectsList) {
            if (subject.getId() == subjectId) {
                Log.d("Subject",subject.getSubjectName().toString());
//                topicList = dataSource.topics.getTopics(subjectId);
                return subject.getSubjectName();
            }
        }
        return null;
    }

    public void getTargets(Context context, String dailyTargetType, Long userId, TargetCallback callback) {

            ProgressDialog dialog = ProgressDialog.show(context, "", "Loading Data...", true);
            dialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {


            String url = App.GET_TARGETS_URL.replace("{type}", dailyTargetType).replace("{userId}", String.valueOf(userId));
            System.out.println(url);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                ArrayList<TargetModel> targetList = new ArrayList<>();

                                JSONArray data = response.getJSONArray("data");


                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);

                                    TargetModel targetModel = new TargetModel();

                                    targetModel.setId(jsonObject.getInt("id"));
                                    targetModel.setUserId(jsonObject.getLong("userId"));
                                    targetModel.setStartDate(jsonObject.getString("startDate"));
                                    targetModel.setEndDate(jsonObject.getString("endDate"));
                                    targetModel.setSubjectId(jsonObject.getLong("subjectId"));
                                    targetModel.setTopicId(jsonObject.isNull("topicId")? null : jsonObject.getLong("topicId"));
                                    targetModel.setTestCount(jsonObject.isNull("testCount") ? null : jsonObject.getInt("testCount"));
                                    targetModel.setRepeatingType(jsonObject.isNull("repeatingType") ? null : jsonObject.getInt("repeatingType"));
                                    targetModel.setRepeatingDay(jsonObject.isNull("repeatingDay") ? null : jsonObject.getInt("repeatingDay"));
                                    targetModel.setRepeatingWeek(jsonObject.isNull("repeatingWeek") ? null : jsonObject.getInt("repeatingWeek"));
                                    targetModel.setRepeatingMonth(jsonObject.isNull("repeatingMonth") ? null : jsonObject.getInt("repeatingMonth"));
                                    targetModel.setTargetName(jsonObject.getString("targetName"));
                                    targetModel.setCompletedTarget(jsonObject.isNull("completedTarget") ? null : jsonObject.getInt("completedTarget"));

                                    Log.d("Target ID", String.valueOf(targetModel.getId()));
                                    Log.d("Target Name", targetModel.getTargetName());
                                    Log.d("subjectID", String.valueOf(targetModel.getSubjectId()));
                                    Log.d("topicId", String.valueOf(targetModel.getTopicId()));
                                    targetList.add(targetModel);

                                }
                                DataSource dataSource = new DataSource(ViewTargets.this);
                                TargetAdapter adapter = new TargetAdapter(context,targetList,dataSource);
                                recyclerView.setAdapter(adapter);

                                if (targetList.isEmpty()) {
                                    TextView notargets = findViewById(R.id.noTargetsAvailable);
                                    notargets.setVisibility(View.VISIBLE);
                                    noDataAnim.setVisibility(View.VISIBLE);
                                    noDataAnim.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            noDataAnim.playAnimation();
                                        }
                                    });
                                } else {
                                    TextView notargets = findViewById(R.id.noTargetsAvailable);
                                    notargets.setVisibility(View.GONE);
                                    noDataAnim.setVisibility(View.GONE);
                                }

                                callback.onTargetsLoaded(targetList);

                            } else if (status.equals("failed")) {

                            } else {
                                Toast.makeText(context, "loading targets Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }, error -> {

                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(ViewTargets.this)
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
            Log.e("Targets: ", "response");
//            System.out.println(jsonObjReq);
            requestQueue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTarget(Context context, Integer targetId, DeleteCallback callback) {
        ProgressDialog dialog = ProgressDialog.show(context, "", "Deleting Target...", true);
        dialog.show();

        DataSource dataSource = new DataSource(context);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            String url = App.DELETE_TARGETS_URL.replace("{id}", String.valueOf(targetId));

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        dialog.dismiss();
                        try {
                            // Handle success response
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");

                            if (status.equals("true")) {
                                callback.onDeleteSuccess();
                            } else if (status.equals("failed")) {
                                callback.onDeleteFailed(message);
                            } else {
                                callback.onDeleteFailed("Deleting target failed");
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        dialog.dismiss();
                        // Handle error response
                        callback.onDeleteFailed("Error deleting target");
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };

            requestQueue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}