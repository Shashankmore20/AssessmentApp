package com.apptmyz.assessment.activity;

import static java.util.Objects.isNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.apptmyz.assessment.model.TestModel;
import com.apptmyz.assessment.model.TestPlayQuestionModel;
import com.apptmyz.assessment.model.TestQuestionsModel;
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReviewActivity2 extends AppCompatActivity {

    UserDetails userDetails;
    DataSource dataSource;

    Long subjectId, topicId;
    String subject, topic;
    int level;
    int questionsCount;

    TextView question_text;
    TextView time_tv, level_tv, noofquestions;
    RecyclerView Choices_rv;

    Button previousBtn, nextBtn, closeBtn;

    TestModel testModel = new TestModel();

    ArrayList<TestQuestionsModel> testQuestionsModelArrayList;

    EditText descriptionBox;
    Long testId;

    int selectedQuestionIdx = 0;
    LinearProgressIndicator simpleProgressBar;

    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_answers);
        noofquestions = findViewById(R.id.noofquestions);

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        subjectId = intent.getLongExtra("subjectId", 0);
        topic = intent.getStringExtra("topic");
        topicId = intent.getLongExtra("topicId", 0);
        level = intent.getIntExtra("level", 0);
        testId = intent.getLongExtra("testId", 0);
        questionsCount = intent.getIntExtra("questionsCount", 0);

        dataSource = new DataSource(getApplicationContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        back = findViewById(R.id.iv_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // initializeUI();

        testModel = new TestModel();
        testQuestionsModelArrayList = new ArrayList<>();

        //getQuestions();
        new QuestionsLoadTask().execute();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.nextBtn:
                        if (selectedQuestionIdx < testQuestionsModelArrayList.size() - 1)
                            selectedQuestionIdx++;
                        else {
                            selectedQuestionIdx = 0;
                        }
                        setQuestion(selectedQuestionIdx);
                        break;
                    case R.id.previousBtn:
                        if (selectedQuestionIdx != 0) {
                            selectedQuestionIdx--;
                        }
                        setQuestion(selectedQuestionIdx);
                        break;
                    case R.id.closeBtn:
                        finish();
                        break;
                }
            }
        };

        time_tv = findViewById(R.id.time_tv);
        level_tv = findViewById(R.id.level_tv);
        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(listener);
        previousBtn = findViewById(R.id.previousBtn);
        previousBtn.setOnClickListener(listener);
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(listener);

        Choices_rv = findViewById(R.id.Choices_rv);
        question_text = findViewById(R.id.question_text);
        descriptionBox = findViewById(R.id.descriptionBox);


    }

    public void setQuestion(int questionNoIdx) {
        simpleProgressBar.setProgress(selectedQuestionIdx);

        System.out.println(testQuestionsModelArrayList.get(questionNoIdx));
        noofquestions.setText("QUESTION " + (Integer.valueOf(selectedQuestionIdx) + 1) + " OF " + (Integer.valueOf(testQuestionsModelArrayList.size())));

        if ((Integer.valueOf(selectedQuestionIdx) + 1) == testQuestionsModelArrayList.size()) {
            nextBtn.setVisibility(View.GONE);
            closeBtn.setVisibility(View.VISIBLE);
        } else {
            nextBtn.setVisibility(View.VISIBLE);
            closeBtn.setVisibility(View.GONE);
        }

        descriptionBox.setVisibility(View.GONE);
        Choices_rv.setVisibility(View.GONE);

        question_text.setText(testQuestionsModelArrayList.get(selectedQuestionIdx).getQuestion());

        if (testQuestionsModelArrayList.get(selectedQuestionIdx).getQuestionType() != 2) {
            descriptionBox.setVisibility(View.GONE);
            Choices_rv.setVisibility(View.VISIBLE);
            ChoicesAdapter checkBoxChoicesAdapter = new ChoicesAdapter(this, testQuestionsModelArrayList.get(questionNoIdx));
            Choices_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            Choices_rv.setAdapter(checkBoxChoicesAdapter);

            Long temp = testQuestionsModelArrayList.get(questionNoIdx).getDuration();
            long minutes = (temp / 1000) / 60;
            long seconds = (temp / 1000) % 60;

            time_tv.setText(minutes + "m " + seconds + "s");
            int question_level = testQuestionsModelArrayList.get(questionNoIdx).getQuestionComplexity();
            if (question_level == 1) {
                level_tv.setText("Easy");
            } else if (question_level == 2) {
                level_tv.setText("Medium");
            } else if (question_level == 3) {
                level_tv.setText("Hard");
            } else {
                level_tv.setText("Random");
            }

            checkBoxChoicesAdapter.notifyDataSetChanged();
        } else {
//            Choices_rv.setVisibility(View.GONE);
//            descriptionBox.setText(testQuestionsModelArrayList.get(questionNoIdx).getUserDescriptionAns());
//            descriptionBox.setVisibility(View.VISIBLE);
        }
    }

    class QuestionsLoadTask extends AsyncTask<String, String, Object> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ReviewActivity2.this, "", "Loading Data...", true);
            dialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            RequestQueue queue = Volley.newRequestQueue(ReviewActivity2.this);

            String url = App.TEST_SOLUTIONS + testId + "/questions";

            try {
                System.out.println("url " + url);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        response -> {

                            try {
                                // to json object to extract data from it.
                                Log.d("Response=>>>", response.toString());
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                String message = obj.getString("message");
                                String token = obj.getString("token");

                                if (status.equals("true")) {

                                    JSONObject testJsonObj = obj.getJSONObject("data");
                                    testModel.setTotalQuestions(testJsonObj.getInt("totalQuestions"));
                                    testModel.setTotalCorrectAnswers(testJsonObj.getInt("totalCorrectQuestions"));
                                    testModel.setAvgPercentage(testJsonObj.getInt("averagePercentage"));
                                    testModel.setAvgDuration(testJsonObj.getLong("averageDuration"));

                                    JSONArray questionJsonArray = testJsonObj.getJSONArray("questionsList");
                                    for (int i = 0; i < questionJsonArray.length(); i++) {
                                        JSONObject questionJsonObj = questionJsonArray.getJSONObject(i);
                                        TestQuestionsModel testQuestionsModel = new TestQuestionsModel();

                                        testQuestionsModel.setQuestionId(questionJsonObj.getLong("questionId"));
                                        testQuestionsModel.setDuration(questionJsonObj.getLong("duration"));
                                        testQuestionsModel.setStartTime(questionJsonObj.getString("startTime"));
                                        testQuestionsModel.setEndTime(questionJsonObj.getString("endTime"));

                                        if (!questionJsonObj.isNull("userAnswer")) {
                                            String[] uAns = questionJsonObj.getString("userAnswer").split(",");
                                            ArrayList<Integer> userList = new ArrayList<>();
                                            for (int temp = 0; temp < uAns.length; temp++) {
                                                userList.add(Integer.parseInt(uAns[temp]));
                                            }
                                            testQuestionsModel.setUserAnswerList(userList);
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
                                            try {
                                                answerList.add((int) Float.parseFloat(ansJsonObj.getString(temp)));
                                            } catch (NumberFormatException e) {
                                                String option = ansJsonObj.getString(temp).toUpperCase(); // Convert to uppercase for case-insensitivity
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

                                        testQuestionsModel.setAnswerList(answerList);
                                        testQuestionsModel.setQuestionComplexity(questionDetailsJsonObj.getInt("questionComplexity"));
                                        //if(questionDetailsJsonObj.getInt("questionType") != null)
                                        //testQuestionsModel.setQuestionType(questionDetailsJsonObj.getInt("questionType"));
                                        testQuestionsModel.setQuestionType(0);
                                        //System.out.println(testQuestionsModel);

                                        Log.d("size", String.valueOf(answerList.size()));
                                        testQuestionsModelArrayList.add(testQuestionsModel);

                                        //setting data for first Question
                                        setQuestion(selectedQuestionIdx);
                                        dialog.dismiss();

                                    }

                                } else if (status.equals("failed")) {

                                } else {
                                    Toast.makeText(ReviewActivity2.this, "loading subjects Failed", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    // method to handle errors.

                    if (error.toString().equals("com.android.volley.AuthFailureError")) {
                        new AlertDialog.Builder(getApplicationContext())
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
                Log.e("Test Solutions: ", "response");
                System.out.println(jsonObjReq);
                queue.add(jsonObjReq);

            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

        }
    }

    public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder> {
        Context context;
        TestQuestionsModel testQuestionsModel;
        int questionType;

        public ChoicesAdapter(Context context, @NonNull TestQuestionsModel model) {
            this.context = context;
            this.testQuestionsModel = model;
            this.questionType = model.getQuestionType();
        }

        @NonNull
        @Override
        public ChoicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (testQuestionsModel.getQuestionType() == 0) {
                View view = LayoutInflater.from(context).inflate(R.layout.radio_button_card, parent, false);
                return new ChoicesAdapter.ViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.check_box_card, parent, false);
                return new ChoicesAdapter.ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ChoicesAdapter.ViewHolder holder, int position) {
            if (questionType == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.choice_tv2.setText(Html.fromHtml(testQuestionsModel.getChoicesList().get(position).getChoiceText(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.choice_tv2.setText(Html.fromHtml(testQuestionsModel.getChoicesList().get(position).getChoiceText()));
                }
                if (testQuestionsModel.getUserAnswerList().contains(position + 1)) {
                    // holder.radioBtn.setChecked(true);
                    holder.radioBtnChoices_rl.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    holder.radioBtnChoices_rl.setBackground(getDrawable(R.drawable.red_border_bg));
                }

                if (testQuestionsModel.getAnswerList().contains(position + 1)) {
                    holder.radioBtnChoices_rl.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.appcolor)));

                    holder.radioBtnChoices_rl.setBackground(getDrawable(R.drawable.green_border_bg));

                }
            } else {
                holder.choice_tv.setText(testQuestionsModel.getChoicesList().get(position).getChoiceText());
//                if (testPlayQuestionModel.getUserAnswer().contains(testPlayQuestionModel.getChoices().get(position).getChoiceId())) {
//                    holder.checkBox.setChecked(true);
//                }
            }
        }

        @Override
        public int getItemCount() {
            return testQuestionsModel.getChoicesList().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout checkBoxChoices_rl;
            CheckBox checkBox;
            TextView choice_tv;

            RelativeLayout radioBtnChoices_rl;
            TextView choice_tv2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                if (questionType == 0) {
                    radioBtnChoices_rl = itemView.findViewById(R.id.radioBtnChoices_rl);
                    radioBtnChoices_rl.setBackgroundResource(R.drawable.card_border);
                    choice_tv2 = itemView.findViewById(R.id.choice_tv2);
                } else {
                    checkBoxChoices_rl = itemView.findViewById(R.id.checkBoxChoices_rl);
                    checkBox = itemView.findViewById(R.id.checkBox);
                    checkBox.setClickable(false);
                    choice_tv = itemView.findViewById(R.id.choice_tv);
                }
            }

        }
    }

}