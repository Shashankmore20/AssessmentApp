package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.ComplexityModel;
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TakenTestsActivity extends AppCompatActivity {

    DataSource dataSource;
    ArrayList<TestSummaryModel> testSummaryModelArrayList;

    ArrayList<ComplexityModel> levelsArrayList;

    String subject, topic;
    Long subjectId, topicId;

    TextView subject_tv;
    ListView tests_lv;
    Spinner level_sp;

    UserDetails userDetails;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yourtests);

        dataSource = new DataSource(this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        testSummaryModelArrayList = dataSource.topicTestsSummary.getTests(-1);
        back = findViewById(R.id.iv_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                intent.putExtra("yourExtraKey", "FragPerformance");
//                startActivity(intent);
                loadSubjectTestsInitial();
                finish();
            }
        });
        Intent intent = getIntent();
        subjectId = intent.getLongExtra("subjectId", 0L);
        subject = intent.getStringExtra("subject");
        topicId = intent.getLongExtra("topicId", 0L);
        topic = intent.getStringExtra("topic");

        System.out.println(subject + " " + topic);

        subject_tv = findViewById(R.id.subject_tv);
        //  topic_tv = findViewById(R.id.topic_tv);
        if (subjectId == 0) {
            subject_tv.setVisibility(View.GONE);
        }
//        if(topicId == 0){
//            topic_tv.setVisibility(View.GONE);
//        }
        tests_lv = findViewById(R.id.tests_lv);
        level_sp = findViewById(R.id.level_sp);

        subject_tv.setText(subject);

        //   topic_tv.setText(topic);

        levelsArrayList = dataSource.complexity.getComplexityList();
        ArrayAdapter<ComplexityModel> arrayAdapter_classes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levelsArrayList);
        level_sp.setAdapter(arrayAdapter_classes);

        level_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                testSummaryModelArrayList = dataSource.topicTestsSummary.getTests(levelsArrayList.get(i).getId());

                TestsAdapter adapter = new TestsAdapter(TakenTestsActivity.this, testSummaryModelArrayList);
                tests_lv.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    public void onBackPressed(){
//        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//        startActivity(intent);
        loadSubjectTestsInitial();
        this.finish();
    }

    public class TestsAdapter extends ArrayAdapter<TestSummaryModel> {

        private final Activity context;
        ArrayList<TestSummaryModel> testSummaryModelArrayList;

        public TestsAdapter(Activity context, ArrayList<TestSummaryModel> testSummaryModelArrayList) {
            super(context, R.layout.taken_test_row, testSummaryModelArrayList);

            this.context = context;
            this.testSummaryModelArrayList = testSummaryModelArrayList;

        }

        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.taken_test_row_new, null, true);

            holder = new ViewHolder();
            holder.date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            holder.fromTime_tv = (TextView) rowView.findViewById(R.id.fromTime_tv);
            holder.toTime_tv = (TextView) rowView.findViewById(R.id.toTime_tv);
            holder.score_tv = (TextView) rowView.findViewById(R.id.score_tv);

            Util.logD(testSummaryModelArrayList.get(position).toString());
//            if (testSummaryModelArrayList.get(position).getTestComplexity() == 0) {
//                holder.level_tv.setText("Random\nLevel");
//                holder.level_tv.setBackgroundColor(getColor(R.color.grey_20));
//            } else
//
//            if (testSummaryModelArrayList.get(position).getTestComplexity() == 1) {
//                holder.level_tv.setText("Easy");
//                holder.level_tv.setBackgroundColor(getColor(R.color.appcolor));
//            } else if (testSummaryModelArrayList.get(position).getTestComplexity() == 2) {
//                holder.level_tv.setText("Medium");
//                holder.level_tv.setBackgroundColor(getColor(R.color.yellow_color));
//            } else if (testSummaryModelArrayList.get(position).getTestComplexity() == 3) {
//                holder.level_tv.setText("Hard");
//                holder.level_tv.setBackgroundColor(getColor(R.color.red_color));
//            }

            holder.date_tv.setText(formateDate(testSummaryModelArrayList.get(position).getTestDate()));
            if (testSummaryModelArrayList.get(position).getStartTime() != null) {
                holder.fromTime_tv.setText(testSummaryModelArrayList.get(position).getStartTime().substring(11));
            }
            if (testSummaryModelArrayList.get(position).getEndTime() != null) {
                holder.toTime_tv.setText(testSummaryModelArrayList.get(position).getEndTime().substring(11));
            }
            holder.score_tv.setText(testSummaryModelArrayList.get(position).getTotalCorrectAnswers() + "/" + testSummaryModelArrayList.get(position).getTotalQuestions());

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CompleteActivity2.class);
                    intent.putExtra("totalCorrectAns", testSummaryModelArrayList.get(position).getTotalCorrectAnswers());
                    intent.putExtra("totalWrongAns", testSummaryModelArrayList.get(position).getTotalWrongAnswers());
                    intent.putExtra("totalUnAnswered", -1);
                    intent.putExtra("avgDuration", testSummaryModelArrayList.get(position).getAvgDuration());
                    intent.putExtra("subject", dataSource.subjects.getSubjectName(testSummaryModelArrayList.get(position).getSubjectId()));
                    intent.putExtra("level", testSummaryModelArrayList.get(position).getTestComplexity());
                    intent.putExtra("topic", dataSource.topics.getTopicName(testSummaryModelArrayList.get(position).getTopicId()));
                    intent.putExtra("testId", testSummaryModelArrayList.get(position).getTestId());
                    intent.putExtra("subjectId", testSummaryModelArrayList.get(position).getSubjectId());
                    startActivity(intent);
                }
            });

            return rowView;
        }
    }

    private String formateDate(String dateTime) {
        Date date;
        String dateInString = dateTime;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = inputFormat.parse(dateInString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    class ViewHolder {
        TextView date_tv, fromTime_tv, toTime_tv, score_tv;
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Your Tests");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    public void loadSubjectTestsInitial() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading Data...", true);
        dialog.show();
        TopicSummaryModel topicSummaryModel = new TopicSummaryModel();

        Map model = new HashMap();
        model.put("userId", userDetails.getUserId());
        model.put("subjectId", null);
        model.put("topicId", null);

        System.out.print(model);

        ObjectMapper mapper = new ObjectMapper();


        RequestQueue queue = Volley.newRequestQueue(this);

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
                                Toast.makeText(this, "loading subjects Failed", Toast.LENGTH_SHORT).show();
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