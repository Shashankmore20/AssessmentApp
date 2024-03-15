package com.apptmyz.assessment.fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.activity.LoginActivity;
import com.apptmyz.assessment.activity.RefreshableFragment;
import com.apptmyz.assessment.activity.ScheduleCallback;
import com.apptmyz.assessment.activity.TargetCallback;
import com.apptmyz.assessment.activity.ViewTargets;
import com.apptmyz.assessment.adapter.ScheduleAdapter;
import com.apptmyz.assessment.adapter.TargetAdapter;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.TargetModel;
import com.apptmyz.assessment.model.TestScheduleModel;
import com.apptmyz.assessment.utils.CalendarUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.ScheduleHelper;
import com.apptmyz.assessment.model.ScheduleModel;
import com.apptmyz.assessment.utils.CalendarUtils;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.google.gson.Gson;


import static com.apptmyz.assessment.utils.CalendarUtils.daysInMonthArray;
import static com.apptmyz.assessment.utils.CalendarUtils.daysInWeekArray;
import static com.apptmyz.assessment.utils.CalendarUtils.monthYearFromDate;
import static com.apptmyz.assessment.utils.CalendarUtils.selectedDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayScheduleFragment extends Fragment implements ScheduleCallback, RefreshableFragment {
    RecyclerView dayScheduleList;
    ScheduleHelper scheduleHelper;
    UserDetails userDetails;

    LottieAnimationView noDataAnim;
    TextView noSchedules;
    DataSource dataSource;
    private DataSharedPref dataSharedPref;
    Long userId = Long.valueOf(0);
    ArrayList<ScheduleModel> scheduleModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_schedule, container, false);

        CalendarUtils.selectedDate = LocalDate.now();
        scheduleHelper = new ScheduleHelper(getContext());

//        ScheduleModel scheduleModel = new ScheduleModel("Physics", "Test on Friction", "2022-04-26", LocalTime.parse("04:30"), LocalTime.parse("05:30"), false, "teacher");
//        scheduleHelper.insertValues(scheduleModel);
//        scheduleModel = new ScheduleModel("Physics", "Test on Friction", "2022-04-27", LocalTime.parse("04:30"), LocalTime.parse("05:30"), false, "teacher");
//        scheduleHelper.insertValues(scheduleModel);
//        scheduleModel = new ScheduleModel("Physics", "Test on Friction", "2022-04-28", LocalTime.parse("04:30"), LocalTime.parse("05:30"), false, "teacher");
//        scheduleHelper.insertValues(scheduleModel);
        dataSharedPref = new DataSharedPref(getContext());
        dataSource = new DataSource(getContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        Util.logD("userdata");
        Util.logD(userdata);

        userId = userDetails.getUserId();
        noDataAnim = view.findViewById(R.id.noDataAnim);

        noSchedules = view.findViewById(R.id.noSchedules);

        dayScheduleList = view.findViewById(R.id.dayScheduleList);
        dayScheduleList.setLayoutManager(new LinearLayoutManager(getContext()));

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayString = dateFormat.format(today);

        getSchedule(getContext(), userId, todayString,todayString, new ScheduleCallback() {
            @Override
            public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {

                DataSource dataSource = new DataSource(getContext());
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
                dayScheduleList.setAdapter(adapter);
            }
        });

        return view;
    }

    @Override
    public void refresh() {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayString = dateFormat.format(today);
        getSchedule(getContext(), userId, todayString, todayString, new ScheduleCallback() {
            @Override
            public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {

                DataSource dataSource = new DataSource(getContext());
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
                dayScheduleList.setAdapter(adapter);
            }
        });
    }

    public interface DeleteCallback {
        void onDeleteSuccess();
        void onDeleteFailed(String errorMessage);
    }
    @Override
    public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {
        DataSource dataSource = new DataSource(getContext());
        ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
        dayScheduleList.setAdapter(adapter);
    }

    public void getSchedule(Context context, Long userId, String startDate, String endDate, ScheduleCallback callback) {

        ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Loading Data...", true);
        dialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        try {

//            String url = App.GET_SCHEDULE_URL;
            System.out.println("url " + App.GET_SCHEDULE_URL);

            JSONObject requestBody = new JSONObject();
            requestBody.put("userId", userId);
            requestBody.put("startDate", startDate);
            requestBody.put("endDate", endDate);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, App.GET_SCHEDULE_URL, requestBody,
                    response -> {
                        dialog.dismiss();
                        System.out.println(requestBody);
                        try {
                            // to json object to extract data from it
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                ArrayList<TestScheduleModel> scheduleList = new ArrayList<>();

                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);

                                    TestScheduleModel scheduleModel = new TestScheduleModel();

                                    noSchedules.setVisibility(View.GONE);
                                    noDataAnim.setVisibility(View.GONE);
                                    scheduleModel.setId(jsonObject.getInt("id"));
                                    scheduleModel.setUserId(jsonObject.getLong("userId"));
                                    scheduleModel.setStartDate(jsonObject.isNull("startDate") ? null : jsonObject.getString("startDate"));
                                    scheduleModel.setEndDate(jsonObject.isNull("endDate") ? null : jsonObject.getString("endDate"));
                                    scheduleModel.setSubjectId(jsonObject.getLong("subjectId"));
                                    scheduleModel.setTopicId(jsonObject.isNull("topicId")? null : jsonObject.getLong("topicId"));
                                    scheduleModel.setTitle(jsonObject.getString("title"));
//                                    scheduleModel.setScheduledDateAndTime(jsonObject.getString("scheduledDateAndTime"));
                                    scheduleModel.setIsCompleted(jsonObject.isNull("isCompleted") ? null : jsonObject.getInt("completedTarget"));

                                    // Formatting the Date
                                    String scheduledDate = jsonObject.getString("scheduledDateAndTime").split(" ")[0];
                                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("d MMMM, yyyy ");
                                    try {
                                        Date date = inputDateFormat.parse(scheduledDate);
                                        String formattedDate = outputDateFormat.format(date);
                                        scheduleModel.setScheduledDateAndTime(formattedDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    // Formatting the Time
                                    String scheduledTime = jsonObject.getString("scheduledDateAndTime").split(" ")[1];
                                    SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a");
                                    try {
                                        Date time = inputTimeFormat.parse(scheduledTime);
                                        String formattedTime = outputTimeFormat.format(time);
                                        scheduleModel.setScheduledDateAndTime(scheduleModel.getScheduledDateAndTime() + " " + formattedTime);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                    Log.d("Target ID", String.valueOf(scheduleModel.getId()));
                                    Log.d("Target Name", scheduleModel.getTitle());
                                    scheduleList.add(scheduleModel);

                                }
                                callback.onSchedulesLoaded(scheduleList);
                                DataSource dataSource = new DataSource(getContext());
                                ScheduleAdapter adapter = new ScheduleAdapter(context,scheduleList,dataSource);
                                dayScheduleList.setAdapter(adapter);
                            } else if (status.equals("failed")) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                noSchedules.setVisibility(View.VISIBLE);
                                noDataAnim.setVisibility(View.VISIBLE);

                            } else {
//                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                noSchedules.setVisibility(View.VISIBLE);
                                noDataAnim.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }, error -> {

                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
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

    public static void deleteSchedule(Context context, Integer scheduleId, TodayScheduleFragment.DeleteCallback callback) {
        ProgressDialog dialog = ProgressDialog.show(context, "", "Deleting Target...", true);
        dialog.show();

        DataSource dataSource = new DataSource(context);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            String url = App.DELETE_SCHEDULE_URL.replace("{id}", String.valueOf(scheduleId));

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