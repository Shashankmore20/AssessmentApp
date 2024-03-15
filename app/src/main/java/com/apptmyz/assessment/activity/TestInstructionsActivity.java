package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.apptmyz.assessment.model.ChoicesModel;
import com.apptmyz.assessment.model.TestPlayQuestionModel;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestInstructionsActivity extends AppCompatActivity {

    DataSource dataSource;
    String subject, topic;
    Long subjectId, topicId;
    int level;
    int noOfQuestions = 10;
    Long testId;

    TextView subject_tv, topic_tv, level_tv;

    TextView testTime;
    EditText questions_counter;
    Button btn_increment, btn_decrement, startTest;

    TextView rule1, rule2, rule3, rule4;

    UserDetails userDetails;
    ImageView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instructions);

        dataSource = new DataSource(this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        subjectId = intent.getLongExtra("subjectId", 0);
        topic = intent.getStringExtra("topic");
        topicId = intent.getLongExtra("topicId", 0);
        level = intent.getIntExtra("level", 0);
        home = findViewById(R.id.iv_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_increment:
                        noOfQuestions++;
                        questions_counter.setText(noOfQuestions + "");
                        testTime.setText(noOfQuestions + "");
                        break;
                    case R.id.btn_decrement:
                        if (noOfQuestions > 2) {
                            noOfQuestions--;
                            questions_counter.setText(noOfQuestions + "");
                            testTime.setText(noOfQuestions + "");
                        }
                        break;
                    case R.id.startTestBtn:
                        loadQuestions();
                        break;
                }
            }
        };

        rule1 = findViewById(R.id.rule1);
        rule2 = findViewById(R.id.rule2);
        rule3 = findViewById(R.id.rule3);
        rule4 = findViewById(R.id.rule4);

        String text = "1.0 mark is awarded for correct attempts and \n0.0 mark for incorrect attempts.";

        SpannableString spannableString = new SpannableString(text);

        int startIndex1 = text.indexOf("1.0");
        int endIndex1 = startIndex1 + "1.0".length();
        int startIndex2 = text.indexOf("0.0");
        int endIndex2 = startIndex2 + "0.0".length();

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.darkgray_ins)), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.darkgray_ins)), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        rule1.setText(spannableString);
        rule2.setText("Tap on options to select correct answer.");
        rule3.setText("Toggle between questions.");
        rule4.setText("Bookmark questions for reviewing later.");

        subject_tv = findViewById(R.id.subject_tv);
        topic_tv = findViewById(R.id.topic_tv);
        level_tv = findViewById(R.id.level_tv);
        level_tv.setVisibility(View.VISIBLE);

        subject_tv.setText(subject);
        topic_tv.setText(topic);
        if (level == 0) {
            level_tv.setText("Random");
        } else if (level == 1) {
            level_tv.setText("Beginner Level");
        } else if (level == 2) {
            level_tv.setText("Intermediate Level");
        } else {
            level_tv.setText("Final Level");
        }

        startTest = findViewById(R.id.startTestBtn);
        startTest.setOnClickListener(listener);
//        btn_decrement = findViewById(R.id.btn_decrement);
//        btn_decrement.setOnClickListener(listener);
//        btn_increment = findViewById(R.id.btn_increment);
//        btn_increment.setOnClickListener(listener);
        questions_counter = findViewById(R.id.questions_counter);
        questions_counter.setText(10 + "");
        testTime = findViewById(R.id.testTime);
        testTime.setText(10 + "");

    }

    private void loadQuestions() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading Test...", true);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        Map model = new HashMap();
        model.put("classId", userDetails.getUserClass());
        model.put("userId", userDetails.getUserId());
        model.put("subjectId", subjectId);
        model.put("topicId", topicId);
        if (level == -1) {
            model.put("questionComplexity", null);
        } else {
            model.put("questionComplexity", level);
        }
        model.put("noOfQuestions", noOfQuestions);

        System.out.print(model);

        ObjectMapper mapper = new ObjectMapper();

        Log.d("loadQuestions: ", "QuestionsRequest");

        try {
            String data = mapper.writeValueAsString(model);

            System.out.println("url " + App.GENERATE_NEW_TEST_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.GENERATE_NEW_TEST_URL, new JSONObject(data),

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
                                JSONObject dataObj = new JSONObject(obj.getString("data"));
                                testId = dataObj.getLong("testId");
                                JSONArray questionsJsonArray = new JSONArray(dataObj.getString("questionsList"));
                                ArrayList<TestPlayQuestionModel> testPlayQuestionModelArrayList = new ArrayList<>();
                                TestPlayQuestionModel testPlayQuestionModel;
                                ArrayList<ChoicesModel> choicesModelList;

                                for (int i = 0; i < questionsJsonArray.length(); i++) {
                                    JSONObject questionsJSONObject = questionsJsonArray.getJSONObject(i);
                                    testPlayQuestionModel = new TestPlayQuestionModel();

                                    testPlayQuestionModel.setQuestionId(questionsJSONObject.getLong("id"));
                                    testPlayQuestionModel.setQuestionNo(i + 1);
                                    if (!questionsJSONObject.isNull("className")) {
                                        testPlayQuestionModel.setClassName(questionsJSONObject.getString("className"));
                                    }
                                    if (!questionsJSONObject.isNull("classDescription")) {
                                        testPlayQuestionModel.setClassDescription(questionsJSONObject.getString("classDescription"));
                                    }
                                    testPlayQuestionModel.setSubjectName(questionsJSONObject.getString("subjectName"));
                                    testPlayQuestionModel.setSubjectDescription(questionsJSONObject.getString("subjectDescription"));
                                    testPlayQuestionModel.setTopicName(questionsJSONObject.getString("topicName"));
                                    testPlayQuestionModel.setTopicDescription(questionsJSONObject.getString("topicDescription"));
                                    testPlayQuestionModel.setQuestion(questionsJSONObject.getString("question"));
                                    testPlayQuestionModel.setQuestionComplexity(questionsJSONObject.getInt("questionComplexity"));
                                    //testPlayQuestionModel.setQuestionType(questionsJSONObject.getInt("questionType"));
                                    testPlayQuestionModel.setQuestionType(0);

                                    JSONArray choicesJSonArray = new JSONArray(questionsJSONObject.getString("choiceList"));
                                    choicesModelList = new ArrayList<>();
                                    ChoicesModel choice;

                                    for (int j = 0; j < choicesJSonArray.length(); j++) {
                                        JSONObject choiceJsonObject = choicesJSonArray.getJSONObject(j);
                                        choice = new ChoicesModel();

                                        choice.setChoiceId(choiceJsonObject.getInt("id"));
                                        System.out.println(choiceJsonObject.getInt("id"));
                                        choice.setChoice(choiceJsonObject.getString("choice"));
                                        choice.setChoiceText(choiceJsonObject.getString("choiceText"));

                                        choicesModelList.add(choice);
                                    }
                                    testPlayQuestionModel.setChoices(choicesModelList);

                                    JSONArray answerJSonArray = new JSONArray(questionsJSONObject.getString("answer"));
                                    ArrayList<Integer> answerList = new ArrayList<>();

                                    for (int j = 0; j < answerJSonArray.length(); j++) {
                                        try {
                                            Float floatVal = Float.valueOf(answerJSonArray.getString(j)).floatValue();
                                            answerList.add(floatVal.intValue());

                                        } catch (NumberFormatException e) {
                                            String option = answerJSonArray.getString(j).toUpperCase(); // Convert to uppercase for case-insensitivity
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

                                    testPlayQuestionModel.setAnswer(answerList);

                                    testPlayQuestionModelArrayList.add(testPlayQuestionModel);

                                }

                                System.out.println(testPlayQuestionModelArrayList);
                                dataSource.test.clearQuestions();
                                dataSource.test.clearChoices();
                                dataSource.test.addQuestions(testId, testPlayQuestionModelArrayList);

                                Toast.makeText(TestInstructionsActivity.this, " " + "Test Ready", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TestInstructionsActivity.this, PlayActivity2.class);
                                intent.putExtra("subject", subject);
                                intent.putExtra("subjectId", subjectId);
                                intent.putExtra("topicId", topicId);
                                intent.putExtra("topic", topic);
                                intent.putExtra("level", level);//TODO
                                intent.putExtra("questionsCount", noOfQuestions);
                                intent.putExtra("testId", testId);
                                startActivity(intent);
                                finish();

                            } else if (status.equals("false")) {
                                Toast.makeText(TestInstructionsActivity.this, " " + message, Toast.LENGTH_SHORT).show();
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
            System.out.println(jsonObjReq);
            //jsonObjReq.setTag(TAG);  // Adding request to request queue
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}