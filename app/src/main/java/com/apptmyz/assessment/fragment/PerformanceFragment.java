package com.apptmyz.assessment.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.LoginActivity;
import com.apptmyz.assessment.activity.MainActivity2;
import com.apptmyz.assessment.activity.NotificationActivity;
import com.apptmyz.assessment.activity.PlayActivity2;
import com.apptmyz.assessment.activity.TakenTestsActivity;
import com.apptmyz.assessment.activity.TopicActivity;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.ComplexityModel;
import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.model.TestPlayQuestionModel;
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceFragment extends Fragment {

    private DataSource dataSource;

    ArrayList<ComplexityModel> levelsArrayList;

    RecyclerView subjects_rv;
    Spinner levels_sp;

    Button your_tests_btn;
    ImageButton reload_btn;

    int selected_subject = 0;

    float TotalQuestion = 0;
    UserDetails userDetails;

    int totalTestAttempt;
    float avg_performance;
    TextView performance_tv, crtAnsCount_tv, totalTests_tv, avgTime_tv, noData;
    ProgressBar performance_bar;
    Spinner subjects;
    ArrayList<SubjectModel> arrayList_subjects;
    PieChart pieChart;
    Long subjectId;
    int levelid;
    ImageView iv_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_performance_home, container, false);

        dataSource = new DataSource(getContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        iv_home = view.findViewById(R.id.iv_home);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.your_tests_btn:
                        loadSubjectTests();
//                        loadSubjectTestsInitial();
                        break;
                    case R.id.reload_btn:
                        getPositionSp();
                        break;
                }
            }
        };
        arrayList_subjects = dataSource.subjects.getSubjects();
        Util.logD(arrayList_subjects.toString());
        your_tests_btn = view.findViewById(R.id.your_tests_btn);
        reload_btn = view.findViewById(R.id.reload_btn);
        your_tests_btn.setOnClickListener(listener);
        reload_btn.setOnClickListener(listener);
        pieChart = view.findViewById(R.id.pieChartNew);
        noData = view.findViewById(R.id.NoDataTest);

        levelsArrayList = dataSource.complexity.getComplexityList();

        levels_sp = view.findViewById(R.id.levels_sp);

//        loadSubjectTestsInitial();

        ArrayAdapter<ComplexityModel> arrayAdapter_levels = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, levelsArrayList);
        levels_sp.setAdapter(arrayAdapter_levels);

        levelid = levelsArrayList.get(0).getId();
        levels_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TopicSummaryModel temp = new TopicSummaryModel();
                levelid = levelsArrayList.get(i).getId();
                temp = dataSource.topicTestsSummary.getTestsSummary(levelid, subjectId.intValue());

                setStats(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        performance_bar = view.findViewById(R.id.perfomance_bar);
        performance_tv = view.findViewById(R.id.perfomance_tv);
        crtAnsCount_tv = view.findViewById(R.id.crtAnsCount_tv);
        totalTests_tv = view.findViewById(R.id.totalTests_tv);
        avgTime_tv = view.findViewById(R.id.avgTime_tv);


        subjects = view.findViewById(R.id.subjects_spinner);

        arrayList_subjects = dataSource.subjects.getSubjects();


        ArrayAdapter<SubjectModel> arrayAdapter_subjects = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList_subjects);
        subjects.setAdapter(arrayAdapter_subjects);
        subjectId = arrayList_subjects.get(0).getId();
        Util.logD("subjectid : " + subjectId);
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TopicSummaryModel temp = new TopicSummaryModel();
                subjectId = arrayList_subjects.get(position).getId();
                selected_subject = position;
                temp = dataSource.topicTestsSummary.getTestsSummary(levelid, subjectId.intValue());
                setStats(temp);
                Log.d("selected Subject", String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void setPieChart(int correct, int wrong) {
        //PIE CHART CODE
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(correct, "Questions Correct"));
        entries.add(new PieEntry(wrong, "Tests Attempted"));

        float deliveredValue = (entries.get(0).getValue() / TotalQuestion) * 100; // Get the value of "Delivered"
        Log.d("totalQuestions", String.valueOf(TotalQuestion));
        Log.d("entries 0", String.valueOf(entries.get(0)));
        Log.d("deliv Value", String.valueOf(deliveredValue));

        // Calculate total value
        int totalValue = 0;
        for (PieEntry entry : entries) {
            totalValue += (int) entry.getValue();
        }
        int[] customColors = {getResources().getColor(R.color.btn_color), getResources().getColor(R.color.appcolor)};

        // Create a dataset with the data
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(customColors);  // Set colors for each entry
        // Create PieData object and set it to the chart
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));  // Set the percentage formatter
        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(true);
        boolean isHoleEnabled = pieChart.isDrawHoleEnabled();
        // Log the result or use it in your logic
//        Log.d("HoleEnabled", "Is Hole Enabled: " + isHoleEnabled);
        // Set chart description
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setEntryLabelTextSize(0f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(2000);
        pieChart.animateX(2000);
        pieChart.setDrawCenterText(false);
//      Set center text
        String centerText = String.valueOf((int) deliveredValue);
        Log.d("centerText", centerText + "%");
        pieChart.setCenterText(centerText + "%");
        pieChart.setCenterTextSize(20f);
        if (!Float.isNaN(deliveredValue) && totalTestAttempt != 0) {
            pieChart.setDrawCenterText(true);
        } else {
            pieChart.setDrawCenterText(false);
        }
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        // Refresh the chart
        pieChart.invalidate();

        //END OF PIECHART CODE
    }

    private void getPositionSp() {
        levels_sp.getSelectedItemPosition();
    }

    public void setStats(TopicSummaryModel temp) {

        if (temp.getTotalQuestions() != 0) {
            performance_bar.setProgress(temp.getTotalCorrectQuestions() * 100 / temp.getTotalQuestions());
            TotalQuestion = temp.getTotalQuestions();
            avg_performance = (temp.getTotalCorrectQuestions() * 100 / temp.getTotalQuestions());
            performance_tv.setText(avg_performance + "%");
            crtAnsCount_tv.setText(temp.getTotalCorrectQuestions() + "/" + temp.getTotalQuestions());
            noData.setVisibility(View.INVISIBLE);
        } else {
            performance_bar.setProgress(0);
            avg_performance = (0);
            performance_tv.setText(avg_performance + "%");
            crtAnsCount_tv.setText(0 + "/" + 0);
            noData.setVisibility(View.VISIBLE);
        }

        totalTests_tv.setText(temp.getTotalAttemptedTests() + "");
        totalTestAttempt = temp.getTotalAttemptedTests();
        Log.d("tottests", String.valueOf(totalTestAttempt));

        //Calculating avg time from milliSeconds

        long minutes = (temp.getAverageDuration() / 1000) / 60;
        long seconds = (temp.getAverageDuration() / 1000) % 60;

        avgTime_tv.setText(minutes + "m " + seconds + "s");

        setPieChart(temp.getTotalCorrectQuestions(), temp.getTotalAttemptedTests());
    }

    public void loadSubjectTests() {
        TopicSummaryModel topicSummaryModel = new TopicSummaryModel();

        Map model = new HashMap();
        model.put("userId", userDetails.getUserId());
        if (arrayList_subjects.get(selected_subject).getId() == -1) {
            model.put("subjectId", null);
            model.put("topicId", null);
        } else {
            model.put("subjectId", arrayList_subjects.get(selected_subject).getId());
            Log.d("selectSubject", String.valueOf(selected_subject));
        }

        System.out.print(model);

        ObjectMapper mapper = new ObjectMapper();

        ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Loading Data...", true);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

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

                                topicSummaryModel.setSubjectId(arrayList_subjects.get(selected_subject).getId());
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

                                Intent intent = new Intent(getContext(), TakenTestsActivity.class);
                                if (selected_subject == -1) {
                                    intent.putExtra("subject", "");
                                    intent.putExtra("subjectId", 0);
                                    intent.putExtra("topicId", "");
                                    intent.putExtra("topic", 0);
                                } else {
                                    intent.putExtra("subject", arrayList_subjects.get(selected_subject).getSubjectName());
                                    intent.putExtra("subjectId", arrayList_subjects.get(selected_subject).getId());
                                    intent.putExtra("topicId", "");
                                    intent.putExtra("topic", 0);
                                }
                                startActivity(intent);

                            } else if (status.equals("failed")) {

                            } else {
                                Toast.makeText(getContext(), "loading subjects Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
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

    public void loadSubjectTestsInitial() {
        ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Loading Data...", true);
        dialog.show();
        TopicSummaryModel topicSummaryModel = new TopicSummaryModel();

        Map model = new HashMap();
        model.put("userId", userDetails.getUserId());
        model.put("subjectId", null);
        model.put("topicId", null);

        System.out.print(model);

        ObjectMapper mapper = new ObjectMapper();


        RequestQueue queue = Volley.newRequestQueue(getContext());

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
                                Toast.makeText(getContext(), "loading subjects Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
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