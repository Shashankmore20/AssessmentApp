package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubjectActivity extends AppCompatActivity {

    private DataSource dataSource;
    UserDetails userDetails;
    ArrayList<TopicModel> topicModelArrayList;

    ListView topics_lv;
    TextView subject_tv;

    Long subjectId;

    EditText et_search;
    String subjectText;
    private ImageView iv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        dataSource = new DataSource(getApplicationContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        initializeUI();

        iv_home = findViewById(R.id.iv_home);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent;
//                intent = new Intent(getApplicationContext(), MainActivity2.class);
//                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        subjectId = intent.getLongExtra("subjectId", 0);
        subjectText = intent.getStringExtra("subject");

        topicModelArrayList = dataSource.topics.getTopics(subjectId);

        topics_lv = findViewById(R.id.topics_lv);
        subject_tv = findViewById(R.id.subject_tv);

        et_search = findViewById(R.id.et_search);

        subject_tv.setText("Topics of "+ subjectText);

        System.out.println(topicModelArrayList);
        TopicsAdapter adapter = new TopicsAdapter(this, topicModelArrayList);
        topics_lv.setAdapter(adapter);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                filterTopics(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void filterTopics(String searchText) {
        ArrayList<TopicModel> filteredTopics = new ArrayList<>();

        for (TopicModel topic : topicModelArrayList) {
            if (topic.getTopicName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredTopics.add(topic);
            }
        }

        TopicsAdapter adapter = new TopicsAdapter(this, filteredTopics);
        topics_lv.setAdapter(adapter);
    }

    public class TopicsAdapter extends ArrayAdapter<TopicModel> {

        private final Activity context;
        ArrayList<TopicModel> topicsList;

        public TopicsAdapter(Activity context, ArrayList<TopicModel> topicsList) {
            super(context, R.layout.topic_row, topicsList);

            this.context = context;
            this.topicsList = topicsList;

        }

        public View getView(int position, View view, ViewGroup parent) {
            SubjectActivity.ViewHolder holder;
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.topic_row, null, true);

            holder = new SubjectActivity.ViewHolder();
            holder.topic_tv = (TextView) rowView.findViewById(R.id.topicName_tv);

            holder.topic_tv.setText(topicsList.get(position).getTopicName());
            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    loadTopicSummary(topicsList.get(position).getTopicId(), topicsList.get(position).getTopicName());
                }
            });

            return rowView;
        }
    }

    public void loadTopicSummary(Long topicId, String topicName) {

        TopicSummaryModel topicSummaryModel = new TopicSummaryModel();

        Map model = new HashMap();
        model.put("userId", userDetails.getUserId());
        model.put("subjectId", subjectId);
        model.put("topicId", topicId);

        System.out.print(model);

        ObjectMapper mapper = new ObjectMapper();

        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading Data...", true);
        dialog.show();

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

                                topicSummaryModel.setSubjectId(subjectId);
                                topicSummaryModel.setTopicId(topicId);
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
                                    if (testJsonObject.isNull("totalWrongAnswers")) {
                                        testSummaryModel.setAvgDuration(0L);
                                    } else {
                                        testSummaryModel.setAvgDuration(testJsonObject.getLong("avgDuration"));
                                    }


                                    testSummaryModelArrayList.add(testSummaryModel);
                                }
                                topicSummaryModel.setTotalAttemptedTests(testSummaryModelArrayList.size());
                                topicSummaryModel.setTestSummaryModelArrayList(testSummaryModelArrayList);

                                dataSource.topicSummary.addTopicSummary(topicSummaryModel);

                                Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                                intent.putExtra("subject", subjectText);
                                intent.putExtra("subjectId", subjectId);
                                intent.putExtra("topicId", topicId);
                                intent.putExtra("topic", topicName);

                                startActivity(intent);

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

    class ViewHolder {
        TextView topic_tv;
    }

    private void initializeUI() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(getString(R.string.my_test));
//
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
//        // set toolbar title and back button
//        toolbar.setNavigationOnClickListener(v -> finish());
    }
}