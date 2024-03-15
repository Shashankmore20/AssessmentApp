package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.ChoicesModel;
import com.apptmyz.assessment.model.TestModel;
import com.apptmyz.assessment.model.TestQuestionsModel;
import com.apptmyz.assessment.utils.Constants;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CompleteActivity2 extends AppCompatActivity {


    TextView score_text;
    TextView crtAnsCount_tv, wrongAnsCount_tv, unAnsCount_tv;
    TextView view_solutions_btn;
    LinearLayout unAns_ll;
    UserDetails userDetails;
    TestModel testModel = new TestModel();
    ArrayList<TestQuestionsModel> testQuestionsModelArrayList;
    DataSource dataSource;
    LineChart lineChart;
    String subject, topic;
    int level;
    int totalCorrectAns, totalWrongAns, totalUnAnswered;
    int totalQuestionsCount;
    Long avgDuration;
    Long testId, subjectId;
    ImageView close;
    List<Integer> userAnswerList; // declare the list
    List<Integer> correctAnswerList; // declare the list

    int y1, y2, y3, y4, y5, y6, y7, y8, y9, y10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");

        topic = intent.getStringExtra("topic");
        level = (intent.getIntExtra("level", 0));
        totalCorrectAns = intent.getIntExtra("totalCorrectAns", 0);
        totalWrongAns = intent.getIntExtra("totalWrongAns", 0);
        totalUnAnswered = intent.getIntExtra("totalUnAnswered", 0);
        totalQuestionsCount = totalCorrectAns + totalWrongAns + totalUnAnswered;
        avgDuration = intent.getLongExtra("avgDuration", 0);
        subjectId = intent.getLongExtra("subjectId", 0);
        testId = intent.getLongExtra("testId", 0);
        Log.e("onCreate: Test Id ", testId + "");
        close = findViewById(R.id.iv_close);
        dataSource = new DataSource(getApplicationContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent1);

            }
        });

        lineChart = findViewById(R.id.lineChart);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.view_solutions_btn:
                        Intent intent = new Intent(getApplicationContext(), ReviewActivity2.class);
                        intent.putExtra("totalCorrectAns", totalCorrectAns);
                        intent.putExtra("totalWrongAns", totalWrongAns);
                        intent.putExtra("totalUnAnswered", totalUnAnswered);
                        intent.putExtra("avgDuration", avgDuration);
                        intent.putExtra("subject", subject);
                        intent.putExtra("level", level);
                        intent.putExtra("topic", topic);
                        intent.putExtra("testId", testId);
                        startActivity(intent);
                        break;
                }
            }
        };

        initializeUI();

        System.out.println(totalCorrectAns + " " + totalWrongAns + " " + totalUnAnswered);

        crtAnsCount_tv = findViewById(R.id.crtAnsCount_tv);
        wrongAnsCount_tv = findViewById(R.id.wrongAnsCount_tv);
        unAnsCount_tv = findViewById(R.id.unAnsCount_tv);

        score_text = findViewById(R.id.score_text);

        view_solutions_btn = findViewById(R.id.view_solutions_btn);
        view_solutions_btn.setOnClickListener(listener);

        crtAnsCount_tv.setText(totalCorrectAns + "");
        wrongAnsCount_tv.setText(totalWrongAns + "");
        unAnsCount_tv.setText(totalUnAnswered + "");

        int complextion_percentage = totalCorrectAns * 100 / (totalQuestionsCount);
        score_text.setText(complextion_percentage + "%");

        List<Entry> lineChartData = new ArrayList<>();
        lineChartData.add(new Entry(1, 1));
        lineChartData.add(new Entry(2, 1));
        lineChartData.add(new Entry(3, 1));
        lineChartData.add(new Entry(4, 1));
        lineChartData.add(new Entry(5, 1));
        lineChartData.add(new Entry(6, 0));
        lineChartData.add(new Entry(7, 1));
        lineChartData.add(new Entry(8, 1));
        lineChartData.add(new Entry(9, 1));
        lineChartData.add(new Entry(10, 1));

        testModel = new TestModel();
        testQuestionsModelArrayList = new ArrayList<>();

        loadQuestionsData(new DataLoadCallback() {
            @Override
            public void onDataLoaded() {
                // This callback is invoked when data is loaded, so we can call setLineChart here
                setLineChart(userAnswerList, correctAnswerList);
            }
        });
//        setLineChart(testQuestionsModelArrayList);
    }

    private void initializeUI() {
        /*
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(getString(R.string.my_test));
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
//        // set toolbar title and back button
//        toolbar.setNavigationOnClickListener(v -> finish());
         */
    }

    private void loadQuestionsData(DataLoadCallback callback) {
        ProgressDialog dialog = ProgressDialog.show(CompleteActivity2.this, "", "Loading Data...", true);

        RequestQueue queue = Volley.newRequestQueue(CompleteActivity2.this);
        String url = App.TEST_SOLUTIONS + testId + "/questions";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                response -> {
                    try {
                        handleResponse(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        dialog.dismiss();
                    }
                },
                error -> {
                    handleError(error);
                    dialog.dismiss();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                return headers;
            }
        };

        queue.add(jsonObjReq);
    }

    private void handleResponse(JSONObject response) throws JSONException {
        String status = response.getString("status");

        if (status.equals("true")) {
            JSONObject testJsonObj = response.getJSONObject("data");
            testModel.setTotalQuestions(testJsonObj.getInt("totalQuestions"));
            testModel.setTotalCorrectAnswers(testJsonObj.getInt("totalCorrectQuestions"));
            testModel.setAvgPercentage(testJsonObj.getInt("averagePercentage"));
            testModel.setAvgDuration(testJsonObj.getLong("averageDuration"));

            JSONArray questionJsonArray = testJsonObj.getJSONArray("questionsList");
            correctAnswerList = new ArrayList<>();
            userAnswerList = new ArrayList<>();
            for (int i = 0; i < questionJsonArray.length(); i++) {
                JSONObject questionJsonObj = questionJsonArray.getJSONObject(i);
                TestQuestionsModel testQuestionsModel = new TestQuestionsModel();

                testQuestionsModel.setQuestionId(questionJsonObj.getLong("questionId"));

                if (!questionJsonObj.isNull("userAnswer")) {
                    String[] uAns = questionJsonObj.getString("userAnswer").split(",");
                    ArrayList<Integer> userList = new ArrayList<>();
                    for (int temp = 0; temp < uAns.length; temp++) {
                        String userAnswerString = uAns[temp].trim(); // Trim to remove leading/trailing whitespaces
                        if (!userAnswerString.isEmpty()) {
                            try {
                                userList.add(Integer.parseInt(userAnswerString));
                                Log.d("userAnswer", String.valueOf(userList));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                // Handle the case where the value is not a valid integer
                            }
                        }
                    }

                    testQuestionsModel.setUserAnswerList(userList);
                    userAnswerList.addAll(userList);

                    Log.d("userAnswerListt", String.valueOf(userAnswerList));
                } else {
                    testQuestionsModel.setUserAnswerList(new ArrayList<>());
                }
                testQuestionsModel.getUserAnswerList();
                testQuestionsModel.setAnswerStatus(questionJsonObj.getInt("questionId"));

                JSONObject questionDetailsJsonObj = questionJsonObj.getJSONObject("questions");

                testQuestionsModel.setQuestion(questionDetailsJsonObj.getString("question"));

                JSONArray choiceJsonArray = questionDetailsJsonObj.getJSONArray("choiceList");
                ArrayList<ChoicesModel> choicesModelArrayList = new ArrayList<>();
                for (int j = 0; j < choiceJsonArray.length(); j++) {
                    ChoicesModel choicesModel = new ChoicesModel();
                    JSONObject choiceJsonObj = choiceJsonArray.getJSONObject(j);

                    choicesModel.setChoiceId(choiceJsonObj.getInt("id"));
                    choicesModel.setChoice(choiceJsonObj.getString("choice"));
                    choicesModel.setChoiceText(choiceJsonObj.getString("choiceText"));

                    choicesModelArrayList.add(choicesModel);
                }
                testQuestionsModel.setChoicesList(choicesModelArrayList);

                JSONArray ansJsonObj = questionDetailsJsonObj.getJSONArray("answer");
                ArrayList<Integer> answerList = new ArrayList<>();
                for (int temp = 0; temp < ansJsonObj.length(); temp++) {
                    String answerString = ansJsonObj.getString(temp).trim(); // Trim to remove leading/trailing whitespaces
                    if (!answerString.isEmpty()) {
                        try {
                            answerList.add((int) Float.parseFloat(answerString));
                        } catch (NumberFormatException e) {
                            String option = answerString.toUpperCase(); // Convert to uppercase for case-insensitivity
                            int optionNumber = 0;

                            switch (option) {
                                case "A":
                                    optionNumber = 1;
                                    break;
                                case "B":
                                    optionNumber = 2;
                                    break;
                                case "C":
                                    optionNumber = 3;
                                    break;
                                case "D":
                                    optionNumber = 4;
                                    break;
                                case "E":
                                    optionNumber = 5;
                                    break;
                            }
                            answerList.add(optionNumber);
                            e.printStackTrace();
                        }
                    }
                }
                correctAnswerList.addAll(answerList);
                testQuestionsModel.setAnswerList(answerList);
                Log.d("CorrectAnswers", String.valueOf(answerList));
                testQuestionsModel.setQuestionComplexity(questionDetailsJsonObj.getInt("questionComplexity"));
                testQuestionsModel.setQuestionType(0);

//                correctAnswerList = answerList;
                Log.d("CorrectAnswerList", String.valueOf(correctAnswerList));
                Log.d("CorrectSize", String.valueOf(correctAnswerList.size()));
                testQuestionsModelArrayList.add(testQuestionsModel);
            }

            setLineChart(userAnswerList, correctAnswerList);
        } else if (status.equals("failed")) {
            // Handle failure...
        } else {
            Toast.makeText(CompleteActivity2.this, "Loading subjects failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        if (error.toString().equals("com.android.volley.AuthFailureError")) {
            new AlertDialog.Builder(CompleteActivity2.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Alert")
                    .setMessage("Token Expired. Logging Out !!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(CompleteActivity2.this, LoginActivity.class);
                        dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                        startActivity(intent);
                    })
                    .show();
        }
    }

    private void setLineChart(List<Integer> userAnswers, List<Integer> correctAnswers) {
        if (userAnswers == null || correctAnswers == null) {
            // Handle the case where lists are null
            Log.d("nullReturn", "error Occured");
//            Log.d("userAnswerList size", String.valueOf(userAnswerList.size()));
//            Log.d("correctAnswerList size", String.valueOf(correctAnswerList.size()));
            return;
        }

        List<Entry> dataEntries = new ArrayList<>();

        // Check if both lists have the same size
        int minSize = Math.min(userAnswers.size(), correctAnswers.size());
        Log.d("size", String.valueOf(userAnswers.size()));
        Log.d("size", String.valueOf(correctAnswers.size()));
        for (int i = 0; i < minSize; i++) {
            int userAnswer = userAnswers.get(i);
            int correctAnswer = correctAnswers.get(i);

            // Compare user answers with correct answers
            boolean isCorrect = userAnswer == correctAnswer;
            dataEntries.add(new Entry(i + 1, isCorrect ? 1 : 0));
        }

        LineDataSet dataSet = new LineDataSet(dataEntries, "Performance Over Time");
        dataSet.setColor(Color.parseColor("#4BA296"));
        dataSet.setCircleColor(Color.parseColor("#4BA296"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(0f);
        dataSet.setValueTextColor(Color.BLACK);

        // Create a gradient fill below the line
        int startColor = Color.parseColor("#664BA296");
        int endColor = Color.parseColor("#00FFFFFF"); // Transparent color
//        Paint paint = new Paint();
//        paint.setShader(new LinearGradient(0, 0, 0, lineChart.getHeight(),
//                startColor, endColor, Shader.TileMode.CLAMP));

        Drawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{endColor, startColor});

        dataSet.setFillDrawable(drawable);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);

        // Create LineChart object and set the data
        LineChart lineChart = findViewById(R.id.lineChart);
        lineChart.setData(lineData);


        lineChart.getDescription().setEnabled(false);

        lineChart.getLegend().setEnabled(false);

        // Disabling grid lines
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        // Customize the X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);

        // Setting the  custom values for the X-axis
        final String[] customValues = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        lineChart.getXAxis().setAxisMinimum(1f);
        lineChart.getXAxis().setAxisMaximum(customValues.length - 0);
        lineChart.setVisibleXRangeMaximum(customValues.length);
        xAxis.setLabelCount(customValues.length, true);
        // Disabling the Left Y-axis legend
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);


        // Disabling the Right Y-axis legend
        YAxis rightyAxis = lineChart.getAxisRight();
        rightyAxis.setDrawLabels(false);
        rightyAxis.setDrawAxisLine(false);

        // Refresh the chart
        lineChart.invalidate();

        // ANIMATION (optional)
        lineChart.animateX(900, Easing.EaseInOutQuart); // Customize the X AXIS Animation Duration and Easing Type
        lineChart.animateY(2000, Easing.EaseInBounce); // Customize the Y AXIS Animation Duration and Easing Type
    }

    private interface DataLoadCallback {
        void onDataLoaded();
    }
}