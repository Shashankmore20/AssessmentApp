package com.apptmyz.assessment.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.apptmyz.assessment.utils.CalendarUtils.formatData;
import static com.apptmyz.assessment.utils.CalendarUtils.formatTime;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.apptmyz.assessment.utils.CalendarUtils;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayActivity2 extends AppCompatActivity {

    UserDetails userDetails;
    DataSource dataSource;
    Long subjectId, topicId;
    String subject, topic;
    int level;
    int questionsCount;

    TextView question_text, progressTimer, noofquestions;
    RecyclerView Choices_rv;

    ArrayList<TestPlayQuestionModel> testPlayQuestionModels;

    Button saveNextBtn, clearBtn, submitBtn, reviewBtn, previousBtn, editBtn, editSubmitBtn;
    ArrayList<String> questionNums;
    EditText descriptionBox;
    LocalDate testDate;
    LocalTime testStartTime;
    LocalTime testEndTime;
    Long Duration;
    Long testId;

    int selectedQuestionIdx = 0;
    int NewSelectedQuestionIdx = 0;
    long currentDuration;

    //for calculating time taken to solve each question
    long tempQuestionStartTime;

    private CountDownTimer countDownTimer;

    ChoicesAdapter choicesAdapter;
    QuestionsNumsAdapter questionsNumsAdapter;

    // Counter to keep track of the current question number
    int currentQuestionNumber = 0;
    LinearProgressIndicator simpleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        subjectId = intent.getLongExtra("subjectId", 0);
        topic = intent.getStringExtra("topic");
        topicId = intent.getLongExtra("topicId", 0);
        level = intent.getIntExtra("level", 0);
        testId = intent.getLongExtra("testId", 0);
        questionsCount = intent.getIntExtra("questionsCount", 0);
        dataSource = new DataSource(getApplicationContext());
        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        getQuestions();

        View reviewLayout = findViewById(R.id.review_Layout);
        CardView cardQuestion = findViewById(R.id.questionsCard);

        progressTimer = findViewById(R.id.progressTimer);

        long duration = TimeUnit.MINUTES.toMillis(testPlayQuestionModels.size());
        tempQuestionStartTime = duration;

        testStartTime = LocalTime.now();
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                currentDuration = l;
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
                        , TimeUnit.MILLISECONDS.toMinutes(l)
                        , TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                progressTimer.setText(sDuration);
            }

            @Override
            public void onFinish() {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity2.this);
                builder.setTitle("Time Up!!!")
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                endTest();
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }.start();

        testDate = LocalDate.now();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.saveNextBtn:
                        setQuestionDuration();
                        tempQuestionStartTime = currentDuration;
                        if (selectedQuestionIdx < testPlayQuestionModels.size() - 1)
                            selectedQuestionIdx++;
                        else {
                            selectedQuestionIdx = 0;
                        }
                        testPlayQuestionModels.get(selectedQuestionIdx).setBookMarked(false);
                        questionsNumsAdapter.notifyDataSetChanged();
                        simpleProgressBar.setProgress(selectedQuestionIdx);
                        Log.d("reviewQuestions", String.valueOf(testPlayQuestionModels.get(selectedQuestionIdx).isBookMarked()));
                        setQuestion(selectedQuestionIdx);
                        updatePreviousButtonState();
                        break;
                    case R.id.previousBtnNew:
                        Log.d("btnClick", "PreviousBtnClick");
                        if (selectedQuestionIdx > 0) {
                            selectedQuestionIdx--;
                            simpleProgressBar.setProgress(selectedQuestionIdx);
                            setQuestion(selectedQuestionIdx);
                            updatePreviousButtonState();
                        }
                        break;
                    case R.id.clearBtn:
                        clearQuestionData(selectedQuestionIdx);
                        setQuestion(selectedQuestionIdx);
                        break;
                    case R.id.reviewBtn:
                        Log.d("btnclick", "review");
                        markForReview();
                        showNextQuestion();
                        break;
                    case R.id.submitBtn:
                        setQuestionDuration();
                        tempQuestionStartTime = currentDuration;
                        if (selectedQuestionIdx < testPlayQuestionModels.size() - 1)
                            selectedQuestionIdx++;
                        else {
                            selectedQuestionIdx = 0;
                        }
                        simpleProgressBar.setProgress(selectedQuestionIdx);
                        setQuestion(selectedQuestionIdx);
                        updatePreviousButtonState();
                        reviewLayout.setVisibility(View.VISIBLE);
                        cardQuestion.setVisibility(View.GONE);
                        break;
                    case R.id.editBtn_review:
                        Log.d("btnClick", "EditBtnClick");
//                            selectedQuestionIdx--;
                        simpleProgressBar.setProgress(selectedQuestionIdx);
                        setQuestion(selectedQuestionIdx);
                        reviewLayout.setVisibility(View.GONE);
                        cardQuestion.setVisibility(View.VISIBLE);
                        break;
                    case R.id.submitBtnn:
                        setQuestionDuration();
                        tempQuestionStartTime = currentDuration;
                        if (selectedQuestionIdx < testPlayQuestionModels.size() - 1)
                            selectedQuestionIdx++;
                        else {
                            selectedQuestionIdx = 0;
                        }
                        simpleProgressBar.setProgress(selectedQuestionIdx);
                        setQuestion(selectedQuestionIdx);
//                        updatePreviousButtonState();
//                        reviewLayout.setVisibility(View.VISIBLE);
//                        cardQuestion.setVisibility(View.GONE);
                        endTest();
                        break;
                }
            }
        };

//        question_no_rv = findViewById(R.id.question_no_rv);
        Choices_rv = findViewById(R.id.Choices_rv);
        question_text = findViewById(R.id.question_text);
        descriptionBox = findViewById(R.id.descriptionBox);
        descriptionBox.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                testPlayQuestionModels.get(selectedQuestionIdx).setUserDescriptionAns(s.toString());
            }
        });

        saveNextBtn = findViewById(R.id.saveNextBtn);
        saveNextBtn.setOnClickListener(listener);
        previousBtn = findViewById(R.id.previousBtnNew);
        previousBtn.setOnClickListener(listener);
//        clearBtn = findViewById(R.id.clearBtn);
//        clearBtn.setOnClickListener(listener);
        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(listener);
        reviewBtn = findViewById(R.id.reviewBtn);
        reviewBtn.setOnClickListener(listener);
        editBtn = findViewById(R.id.editBtn_review);
        editBtn.setOnClickListener(listener);
        editSubmitBtn = findViewById(R.id.submitBtnn);
        editSubmitBtn.setOnClickListener(listener);
        noofquestions = findViewById(R.id.noofquestions);
        //setting data for first Question
        setQuestion(selectedQuestionIdx);
        simpleProgressBar.setMax(testPlayQuestionModels.size());

        RecyclerView reviewRecyclerView = findViewById(R.id.Review_rv);
        ReviewAnswersAdapter reviewAdapter = new ReviewAnswersAdapter(this, testPlayQuestionModels, choicesAdapter);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

    }

    private void updatePreviousButtonState() {

        Button previousBtn = findViewById(R.id.previousBtnNew);

        if (selectedQuestionIdx == 0) {
            previousBtn.setEnabled(false); // Disable the button
        } else {
            previousBtn.setEnabled(true); // Enable the button
        }
    }

    private void markForReview() {
        testPlayQuestionModels.get(selectedQuestionIdx).setBookMarked(true);
        questionsNumsAdapter.notifyDataSetChanged();
    }

    private void showNextQuestion() {

        setQuestionDuration();
        tempQuestionStartTime = currentDuration;
        if (selectedQuestionIdx < testPlayQuestionModels.size() - 1) {
            selectedQuestionIdx++;
        } else {
            selectedQuestionIdx = 0;
        }
        simpleProgressBar.setProgress(selectedQuestionIdx);
        setQuestion(selectedQuestionIdx);
        updatePreviousButtonState();
    }

    public void setQuestionDuration() {
        long totalDuration = testPlayQuestionModels.get(selectedQuestionIdx).getDuration() + Math.abs(currentDuration - tempQuestionStartTime);
        testPlayQuestionModels.get(selectedQuestionIdx).setDuration(totalDuration);
    }

    public void endTest() {

        countDownTimer.cancel();
        testEndTime = LocalTime.now();

        TestModel testModel = new TestModel();
        testModel.setTestId(testId);
        testModel.setUserId(userDetails.getUserId());
        testModel.setTestDate(CalendarUtils.formatData(testDate).toString());
        testModel.setTestDuration(currentDuration);
        testModel.setStartTime(CalendarUtils.formatTime(testStartTime).toString());
        testModel.setEndTime(CalendarUtils.formatTime(testEndTime).toString());
        testModel.setTestComplexity(level);//TODO
        testModel.setSubjectId(subjectId);
        testModel.setTopicId(topicId);
        testModel.setTotalQuestions(questionsCount);

        int totalCorrectAns = 0;
        int totalWrongAns = 0;
        int totalUnAnswered = 0;
        long totalDuration = 0;

        ArrayList<TestQuestionsModel> testQuestionsModelArrayList = new ArrayList<>();
        TestQuestionsModel testQuestionsModel;
        for (int i = 0; i < testPlayQuestionModels.size(); i++) {
            TestPlayQuestionModel model = testPlayQuestionModels.get(i);
            testQuestionsModel = new TestQuestionsModel();

            testQuestionsModel.setQuestionId(model.getQuestionId());
            testQuestionsModel.setUserTestId(testId);
            testQuestionsModel.setDuration(null);
            testQuestionsModel.setStartTime(null);
            testQuestionsModel.setEndTime(null);
            testQuestionsModel.setDuration(model.getDuration());
            totalDuration += model.getDuration();

            ArrayList<Integer> temp = new ArrayList<>();//incrementing userAns by 1, because id is greater than position by 1
            for (int j = 0; j < model.getUserAnswer().size(); j++) {
                temp.add(model.getUserAnswer().get(j) + 1);
            }
            String uAns = dataSource.test.arrayToString(temp);
            testQuestionsModel.setUserAnswer(uAns);
            if (temp.size() == 0) {
                testQuestionsModel.setUserAnswer(null);
            }
            Log.e("User Ans: ", uAns);

            model.setUserAnswer(temp);

            if (model.getQuestionType() == 0) {


                if (model.getUserAnswer().size() != 0) {
                    if (model.getUserAnswer().equals(model.getAnswer())) {
                        testQuestionsModel.setAnswerStatus(1);
                        totalCorrectAns++;
                    } else {
                        testQuestionsModel.setAnswerStatus(0);
                        totalWrongAns++;
                    }
                } else {
                    totalUnAnswered++;
                }

            }
            testQuestionsModelArrayList.add(testQuestionsModel);
        }

        testModel.setAvgDuration(totalDuration / testPlayQuestionModels.size());
        testModel.setTotalCorrectAnswers(totalCorrectAns);
        testModel.setTotalWrongAnswers(totalWrongAns + totalUnAnswered);
        testModel.setTestQuestionsList(testQuestionsModelArrayList);

//        System.out.println(testModel);
        sendTest(testModel);

        Intent intent = new Intent(getApplicationContext(), CompleteActivity2.class);
        intent.putExtra("totalCorrectAns", totalCorrectAns);
        intent.putExtra("totalWrongAns", totalWrongAns);
        intent.putExtra("totalUnAnswered", totalUnAnswered);
        intent.putExtra("avgDuration", testModel.getAvgDuration());
        intent.putExtra("subject", subject);
        intent.putExtra("level", level);
        intent.putExtra("topic", topic);
        intent.putExtra("testId", testId);
        intent.putExtra("subjectId", subjectId);
        startActivity(intent);
        finish();
    }

    public void sendTest(TestModel model) {
        RequestQueue queue = Volley.newRequestQueue(this);

        ObjectMapper mapper = new ObjectMapper();

        try {
            String data = mapper.writeValueAsString(model);
            Log.d("sendTest: ", "test");
            System.out.println(data);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST
                    ,
                    App.SUBMIT_TEST_URL, new JSONObject(data),

                    response -> {
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                Toast.makeText(PlayActivity2.this, " " + message + "Test Submitted", Toast.LENGTH_SHORT).show();

                            } else if (status.equals("failed")) {
                                Toast.makeText(PlayActivity2.this, " " + "message", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PlayActivity2.this, "Test Loading Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
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
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

    }

    public void clearQuestionData(int questionNoIdx) {
        if (testPlayQuestionModels.get(questionNoIdx).getQuestionType() == 0) {
            testPlayQuestionModels.get(questionNoIdx).setUserAnswer(new ArrayList<>());
        } else if (testPlayQuestionModels.get(questionNoIdx).getQuestionType() == 1) {
            testPlayQuestionModels.get(questionNoIdx).setUserAnswer(new ArrayList<>());
        } else {
        }
    }

    public void setQuestion(int questionNoIdx) {
        descriptionBox.setVisibility(View.GONE);
        Choices_rv.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            question_text.setText(Html.fromHtml(testPlayQuestionModels.get(selectedQuestionIdx).getQuestion(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            question_text.setText(Html.fromHtml(testPlayQuestionModels.get(selectedQuestionIdx).getQuestion()));
        }
        questionsNumsAdapter = new QuestionsNumsAdapter(this, testPlayQuestionModels);
        noofquestions.setText("QUESTION " + (Integer.valueOf(selectedQuestionIdx) + 1) + " OF " + (Integer.valueOf(testPlayQuestionModels.size())));
        if (testPlayQuestionModels.get(questionNoIdx).getQuestionType() != 2) {
            descriptionBox.setVisibility(View.GONE);
            Choices_rv.setVisibility(View.VISIBLE);
            choicesAdapter = new ChoicesAdapter(this, testPlayQuestionModels.get(questionNoIdx));
            Choices_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            Choices_rv.setAdapter(choicesAdapter);

            if ((Integer.valueOf(selectedQuestionIdx) + 1) == testPlayQuestionModels.size()) {
                saveNextBtn.setVisibility(View.GONE);
                submitBtn.setVisibility(View.VISIBLE);
            } else {
                saveNextBtn.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.GONE);
            }

            choicesAdapter.notifyDataSetChanged();
        } else {
            Choices_rv.setVisibility(View.GONE);
            descriptionBox.setText(testPlayQuestionModels.get(questionNoIdx).getUserDescriptionAns());
            descriptionBox.setVisibility(View.VISIBLE);
        }
    }

    public void getQuestions() {
        testPlayQuestionModels = dataSource.test.getQuestions();
    }

    public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder> {
        Context context;
        TestPlayQuestionModel testPlayQuestionModel;
        int questionType;
        ArrayList<Integer> userChoices;

        public ChoicesAdapter(Context context, @NonNull TestPlayQuestionModel model) {
            this.context = context;
            this.testPlayQuestionModel = model;
            this.questionType = model.getQuestionType();
            userChoices = model.getUserAnswer();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (testPlayQuestionModel.getQuestionType() == 0) {
                View view = LayoutInflater.from(context).inflate(R.layout.radio_button_card, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.check_box_card, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ChoicesAdapter.ViewHolder holder, int position) {
            if (questionType == 0) {
                int color;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.choice_tv2.setText(Html.fromHtml(testPlayQuestionModel.getChoices().get(position).getChoiceText(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.choice_tv2.setText(Html.fromHtml(testPlayQuestionModel.getChoices().get(position).getChoiceText()));
                }
                if ((testPlayQuestionModel.getUserAnswer().contains(position))) { //id  = 1, pos-0
                    holder.radioBtnChoices_rl.setBackgroundResource(R.drawable.card_selected_back);

                } else {
                    holder.radioBtnChoices_rl.setBackgroundResource(R.drawable.card_border);
                }

            } else {
                holder.choice_tv.setText(testPlayQuestionModel.getChoices().get(position).getChoiceText());
                if (testPlayQuestionModel.getUserAnswer().contains(testPlayQuestionModel.getChoices().get(position).getChoiceId())) {
                    holder.checkBox.setChecked(true);
                }
            }
        }

        @Override
        public int getItemCount() {
            return testPlayQuestionModel.getChoices().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout checkBoxChoices_rl;
            CheckBox checkBox;
            TextView choice_tv;
            CardView card_questions;
            RelativeLayout radioBtnChoices_rl;
            TextView choice_tv2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                if (questionType == 0) {
                    radioBtnChoices_rl = itemView.findViewById(R.id.radioBtnChoices_rl);
                    choice_tv2 = itemView.findViewById(R.id.choice_tv2);

                    radioBtnChoices_rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<Integer> tempUserAns = new ArrayList<>();
                            tempUserAns.add(getAdapterPosition());
                            radioBtnChoices_rl.setBackgroundResource(R.drawable.card_selected_back);
                            testPlayQuestionModel.setUserAnswer(tempUserAns);
                            choicesAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    checkBoxChoices_rl = itemView.findViewById(R.id.checkBoxChoices_rl);
                    checkBox = itemView.findViewById(R.id.checkBox);
                    checkBox.setClickable(false);
                    choice_tv = itemView.findViewById(R.id.choice_tv);

                    checkBoxChoices_rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (checkBox.isChecked()) {
                                int index = testPlayQuestionModel.getUserAnswer().indexOf(testPlayQuestionModel.getChoices().get(getAdapterPosition()).getChoiceId());
                                testPlayQuestionModel.getUserAnswer().remove(index);
                                checkBox.setChecked(false);
                            } else {
                                testPlayQuestionModel.getUserAnswer().add(testPlayQuestionModel.getChoices().get(getAdapterPosition()).getChoiceId());
                                checkBox.setChecked(true);
                            }
                        }
                    });

                }
            }

        }
    }

    public class QuestionsNumsAdapter extends RecyclerView.Adapter<QuestionsNumsAdapter.ViewHolder> {

        Context context;
        List<TestPlayQuestionModel> models;

        public QuestionsNumsAdapter(Context context, List<TestPlayQuestionModel> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.question_no_card, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.questionNum.setText(models.get(position).getQuestionNo() + "");

            if (testPlayQuestionModels.get(position).isBookMarked()) {
                holder.bookmark_iv.setVisibility(View.VISIBLE);
            }
            if (selectedQuestionIdx == position) {
                holder.relative_layout.setBackgroundColor(Color.parseColor("#7BE1E8"));
            } else {
                holder.relative_layout.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView questionNum;
            ImageView bookmark_iv;
            RelativeLayout relative_layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                questionNum = itemView.findViewById(R.id.question_no);
                relative_layout = itemView.findViewById(R.id.relative_layout);
                bookmark_iv = itemView.findViewById(R.id.bookmark_iv);

                relative_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setSingleSelection(getAdapterPosition());
                    }
                });
            }

            private void setSingleSelection(int adapterPosition) {
                if (adapterPosition == RecyclerView.NO_POSITION) return;
                setQuestionDuration();
                notifyItemChanged(selectedQuestionIdx);
                tempQuestionStartTime = currentDuration;
                selectedQuestionIdx = adapterPosition;
                notifyItemChanged(selectedQuestionIdx);
                setQuestion(adapterPosition);
            }
        }
    }

    public class ReviewAnswersAdapter extends RecyclerView.Adapter<ReviewAnswersAdapter.ViewHolder> {

        private Context context;
        private List<TestPlayQuestionModel> testPlayQuestionModels;
        private ChoicesAdapter choicesAdapter;
        private int selectedPosition = RecyclerView.NO_POSITION;

        public ReviewAnswersAdapter(Context context, List<TestPlayQuestionModel> testPlayQuestionModels, ChoicesAdapter choicesAdapter) {
            this.context = context;
            this.testPlayQuestionModels = testPlayQuestionModels;
            this.choicesAdapter = choicesAdapter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_review_answers, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TestPlayQuestionModel questionModel = testPlayQuestionModels.get(position);

            holder.questionNumber.setText(String.valueOf(questionModel.getQuestionNo()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.question.setText(Html.fromHtml(questionModel.getQuestion(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.question.setText(Html.fromHtml(questionModel.getQuestion()));
            }

            ArrayList<Integer> userAnswer = questionModel.getUserAnswer();
            if (userAnswer != null && userAnswer.size() > 0) {
                String userAnswerText = "Your Answer: ";
                for (Integer choiceIndex : userAnswer) {
                    userAnswerText += (choiceIndex + 1) + ", "; // Adding 1 because choices start from 0
                }
                holder.userAnswer.setText(userAnswerText.substring(0, userAnswerText.length() - 2));
                holder.exclaim.setVisibility(View.INVISIBLE);

            } else {
                holder.userAnswer.setText("Your Answer: Marked For Review");
                holder.exclaim.setVisibility(View.VISIBLE);
            }

            if (questionModel.isBookMarked() == true) {
                holder.exclaim.setVisibility(View.VISIBLE);
            } else {
                holder.exclaim.setVisibility(View.INVISIBLE);
            }
            Log.d("reviewQuestions", String.valueOf(questionModel.isBookMarked()));

            holder.itemView.setOnClickListener(view -> {
                selectedQuestionIdx = holder.getAdapterPosition();
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                Log.d("position", String.valueOf(selectedQuestionIdx));
            });

            if (selectedPosition == position) {
                holder.linearr.setBackgroundResource(R.drawable.selected_review_border);
            } else {
                holder.linearr.setBackgroundResource(0); // Set to 0 to clear the Background Resource.
            }
        }

        @Override
        public int getItemCount() {
            return testPlayQuestionModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView question;
            TextView userAnswer;
            TextView questionNumber;
            ImageView exclaim;
            LinearLayout linearr;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                question = itemView.findViewById(R.id.review_question);
                userAnswer = itemView.findViewById(R.id.review_answer);
                questionNumber = itemView.findViewById(R.id.review_question_no);
                exclaim = itemView.findViewById(R.id.review_exclaim);
                linearr = itemView.findViewById(R.id.linearr);
            }
        }
    }
}