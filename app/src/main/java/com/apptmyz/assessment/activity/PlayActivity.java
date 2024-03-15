package com.apptmyz.assessment.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.ContestDataBaseHelper;
import com.apptmyz.assessment.database.TestDatabaseHelper;
import com.apptmyz.assessment.databinding.ActivityPlayBinding;
import com.apptmyz.assessment.helper.CircleTimer;
import com.apptmyz.assessment.helper.Constant;
import com.apptmyz.assessment.model.Common;
import com.apptmyz.assessment.model.Contest;
import com.apptmyz.assessment.model.Question;
import com.apptmyz.assessment.model.QuestionModel;
import com.apptmyz.assessment.model.TimerModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends AppCompatActivity {
    ActivityPlayBinding binding;
    private String response;
    private List<QuestionModel> arrayList = new ArrayList<>();

    final static long INTERVAL = 1000; //interval 1 second in milisecond
    final static long TIMEOUT = 120000; // 2 mint time out per question in milisecton
    int progressValue = 0;
    CountDownTimer mCountDown, currentTimter;
    int index = 0, score = 0, thisQuestion = 0, totalQuestion, correctAnwer;
    ProgressBar progressBar;
    ImageView question_image;
    TextView btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestionNum, question_text, timerSecond, questionNo;
    int timer = 0, timerOld, startTimer = 0;
    ArrayList<TimerModel> timerModelArrayList = new ArrayList<>();
    private String TempAnswer = null;
    private String getButtonName = "";
    private TextView question_time, test_time;
    private Long usertestid;
    private String currentAnswer;
    private TestDatabaseHelper testDatabaseHelper;
    private String testdate, starttime;
    private ImageView backQtn, nextQtn;
    public static CircleTimer progressTimer;
    private RelativeLayout a_layout, b_layout, c_layout, d_layout;
    private int totalTimer = 0;
    private long TOTAL_QUESTION_TIMEOUT;
    ContestDataBaseHelper contestDataBaseHelper;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_play);
        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getData();
        timerSecond = findViewById(R.id.timerSecond);
        txtScore = findViewById(R.id.txtScore);
        txtQuestionNum = findViewById(R.id.txtTotalQuestion);
        question_text = findViewById(R.id.question_text);

        question_time = findViewById(R.id.question_time);
        test_time = findViewById(R.id.test_time);
        //question_image = findViewById(R.id.question_image);
        Collections.shuffle(Common.list_question);
        progressBar = findViewById(R.id.progressBar);
        questionNo = findViewById(R.id.questionNo);
        progressTimer = findViewById(R.id.progressTimer);

        btnA = findViewById(R.id.btnAnswerA);
        btnB = findViewById(R.id.btnAnswerB);
        btnC = findViewById(R.id.btnAnswerC);
        btnD = findViewById(R.id.btnAnswerD);
        a_layout = findViewById(R.id.a_layout);
        b_layout = findViewById(R.id.b_layout);
        c_layout = findViewById(R.id.c_layout);
        d_layout = findViewById(R.id.d_layout);

        backQtn = findViewById(R.id.backQtn);
        nextQtn = findViewById(R.id.nextQtn);

        initializeUI();
        optionClick();
        totlaQuestionTimer();
        currentQuestionTimer();
    }

    private void optionClick() {
        btnA.setOnClickListener(view -> {
            getButtonName = getResources().getResourceEntryName(view.getId());
            TempAnswer = btnA.getText().toString();
            setButtonColor(view);
        });

        btnB.setOnClickListener(view -> {
            getButtonName = getResources().getResourceEntryName(view.getId());
            TempAnswer = btnB.getText().toString();
            setButtonColor(view);
        });
        btnC.setOnClickListener(view -> {
            getButtonName = getResources().getResourceEntryName(view.getId());
            TempAnswer = btnC.getText().toString();
            setButtonColor(view);
        });

        btnD.setOnClickListener(view -> {
            getButtonName = getResources().getResourceEntryName(view.getId());
            TempAnswer = btnD.getText().toString();
            setButtonColor(view);
        });

        nextQtn.setOnClickListener(view -> {
            //index++;
            int temp = index + 1;
            if (thisQuestion + 1 == totalQuestion) {
                //nextQtn.setText("SUBMIT"); ic_baseline_login_24
                //nextQtn.setImageResource(R.drawable.ic_baseline_refresh_24);
            }

            if (temp == Contest.list_question.size()) {
                clickValue(getButtonName);
            } else {
                contestDataBaseHelper = new ContestDataBaseHelper(this);
                question = contestDataBaseHelper.getQuestionByID(Contest.list_question.get(temp).getTestquestionid());
                String userAnswer = question.getUserAnswer();
                Log.d("INDEX", String.valueOf(temp));
                Log.d("UUSSS", String.valueOf(userAnswer));

                if (userAnswer != null) {
                    if (userAnswer.equals("A") || userAnswer.equals("B") || userAnswer.equals("C") || userAnswer.equals("D")) {
                        Log.d("UUSSS111", userAnswer);
                        showQuestion(++index);
                        UserAnswerDisabled(question);
                    }
                } else {
                    btnA.setEnabled(true);
                    btnB.setEnabled(true);
                    btnC.setEnabled(true);
                    btnD.setEnabled(true);
                    clickValue(getButtonName);
                    setButtonColor(view);
                }

            }
        });

        backQtn.setOnClickListener(view -> {
            if (index > 0) {
                thisQuestion = thisQuestion - 2;
                question = contestDataBaseHelper.getQuestionByID(Contest.list_question.get(index).getTestquestionid());
                Log.d("BACK_INDEX", String.valueOf(index));
                showQuestion(--index);
                Log.d("BACK_INDEX_TEST", String.valueOf(question));
                UserAnswerDisabled(question);
            } else {
                Toast.makeText(this, "First Question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UserAnswerDisabled(Question question) {
        currentTimter.cancel();
        if (question.getUserAnswer() != null) {
            if (question.getUserAnswer().trim().equals("A")) {
                a_layout.setBackgroundResource(R.drawable.selected);
                b_layout.setBackgroundResource(R.drawable.notselected);
                c_layout.setBackgroundResource(R.drawable.notselected);
                d_layout.setBackgroundResource(R.drawable.notselected);

            } else if (question.getUserAnswer().trim().equals("B")) {
                a_layout.setBackgroundResource(R.drawable.notselected);
                b_layout.setBackgroundResource(R.drawable.selected);
                c_layout.setBackgroundResource(R.drawable.notselected);
                d_layout.setBackgroundResource(R.drawable.notselected);


            } else if (question.getUserAnswer().trim().equals("C")) {
                a_layout.setBackgroundResource(R.drawable.notselected);
                b_layout.setBackgroundResource(R.drawable.notselected);
                c_layout.setBackgroundResource(R.drawable.selected);
                d_layout.setBackgroundResource(R.drawable.notselected);


            } else if (question.getUserAnswer().trim().equals("D")) {
                a_layout.setBackgroundResource(R.drawable.notselected);
                b_layout.setBackgroundResource(R.drawable.notselected);
                c_layout.setBackgroundResource(R.drawable.notselected);
                d_layout.setBackgroundResource(R.drawable.selected);

            }
        } else {
            a_layout.setBackgroundResource(R.drawable.notselected);
            b_layout.setBackgroundResource(R.drawable.notselected);
            c_layout.setBackgroundResource(R.drawable.notselected);
            d_layout.setBackgroundResource(R.drawable.notselected);

        }
        btnA.setEnabled(false);
        btnB.setEnabled(false);
        btnC.setEnabled(false);
        btnD.setEnabled(false);
    }

    private void setButtonColor(View view) {
        if (view.getId() == R.id.btnAnswerA) {
            a_layout.setBackgroundResource(R.drawable.selected);
            b_layout.setBackgroundResource(R.drawable.notselected);
            c_layout.setBackgroundResource(R.drawable.notselected);
            d_layout.setBackgroundResource(R.drawable.notselected);

        } else if (view.getId() == R.id.btnAnswerB) {
            a_layout.setBackgroundResource(R.drawable.notselected);
            b_layout.setBackgroundResource(R.drawable.selected);
            c_layout.setBackgroundResource(R.drawable.notselected);
            d_layout.setBackgroundResource(R.drawable.notselected);


        } else if (view.getId() == R.id.btnAnswerC) {
            a_layout.setBackgroundResource(R.drawable.notselected);
            b_layout.setBackgroundResource(R.drawable.notselected);
            c_layout.setBackgroundResource(R.drawable.selected);
            d_layout.setBackgroundResource(R.drawable.notselected);

        } else if (view.getId() == R.id.btnAnswerD) {
            a_layout.setBackgroundResource(R.drawable.notselected);
            b_layout.setBackgroundResource(R.drawable.notselected);
            c_layout.setBackgroundResource(R.drawable.notselected);
            d_layout.setBackgroundResource(R.drawable.selected);

        } else {
            a_layout.setBackgroundResource(R.drawable.notselected);
            b_layout.setBackgroundResource(R.drawable.notselected);
            c_layout.setBackgroundResource(R.drawable.notselected);
            d_layout.setBackgroundResource(R.drawable.notselected);

        }

    }

    private void clickValue(String getButtonName) {
        currentTimter.cancel();
        mCountDown.cancel();
        timerOld = timer;
        timer = 0;
        //strat question
        String correctVal;
        if (getButtonName.equalsIgnoreCase("btnAnswerA")) {
            correctVal = "A";
        } else if (getButtonName.equalsIgnoreCase("btnAnswerB")) {
            correctVal = "B";
        } else if (getButtonName.equalsIgnoreCase("btnAnswerC")) {
            correctVal = "C";
        } else if (getButtonName.equalsIgnoreCase("btnAnswerD")) {
            correctVal = "D";
        } else {
            correctVal = "W";
        }
        //Toast.makeText(this, "" + getButtonName, Toast.LENGTH_SHORT).show();
        // set time duration
        //Common.list_question.get(index).setUserAnswer(correctVal);
        // Common.list_question.get(index).setDuration((long) timerOld);
        question = contestDataBaseHelper.getQuestionByID(Contest.list_question.get(index).getTestquestionid());
        contestDataBaseHelper = new ContestDataBaseHelper(this);
        contestDataBaseHelper.updateQuestion(Contest.list_question.get(index).getTestquestionid(), correctVal, String.valueOf(timerOld));

        TimerModel timerModel = new TimerModel();
        timerModel.setQuestionId(String.valueOf(question.getQuestionid()));
        timerModel.setScoretime(String.valueOf(timerOld));

        if (!timerModelArrayList.contains(timerModel)) {
            timerModelArrayList.add(timerModel);
            totalTimer += timerOld;
            //timerModelArrayList.add(timerModel);
            SharedPreferences s_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = s_pref.edit();
            //edit.putString("key","value");
            edit.putString(String.valueOf(question.getQuestionid()), String.valueOf(correctVal));
            edit.apply();
        }
        Log.d("VALLLA", timerModelArrayList.toString());

        if (correctVal.equalsIgnoreCase(question.getAnswer().trim())) {
            Contest.list_question.get(index).setResult(1);
            score += 10;
            correctAnwer++;
            showQuestion(++index);

        } else {
            Contest.list_question.get(index).setResult(0);
            showQuestion(++index);

        }

        txtScore.setText(String.format("%d", score));
    }

    private void showQuestion(int index) {

        if (index < totalQuestion) {
            String testquestionid = Contest.list_question.get(index).getTestquestionid();
            contestDataBaseHelper = new ContestDataBaseHelper(this);
            question = contestDataBaseHelper.getQuestionByID(testquestionid);

            thisQuestion++;
            txtQuestionNum.setText(String.format("%d/%d", thisQuestion, totalQuestion));
            questionNo.setText(String.format("%d", thisQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            question_text.setText(Html.fromHtml(question.getQuestion()));

            btnA.setText(Html.fromHtml(question.getChoice1()));
            btnB.setText(Html.fromHtml(question.getChoice2()));
            btnC.setText(Html.fromHtml(question.getChoice3()));
            btnD.setText(Html.fromHtml(question.getChoice4()));
            mCountDown.start();
            currentTimter.start();

        } else {
            //Convert Object data to json array
            JsonArray questionList = new Gson().toJsonTree(arrayList).getAsJsonArray();
            //Log.d("jsonaray",questionList.toString());
            // testDatabaseHelper = new TestDatabaseHelper(this);
            // testDatabaseHelper.insertData(String.valueOf(usertestid), "MYUSERID" + usertestid, testdate, starttime, questionList.toString());
            contestDataBaseHelper = new ContestDataBaseHelper(this);
            contestDataBaseHelper.updateContestStatus(String.valueOf(usertestid), "Complete", App.getTimeFromLong(startTimer));
            //Log.d("FFFF", String.valueOf(Contest.list_question));
            Intent intent = new Intent(this, CompleteActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnwer);
            dataSend.putLong("usertestid", usertestid);
            intent.putExtra("response", String.valueOf(response));
            dataSend.putString("QTIMER", String.valueOf(timerModelArrayList));
            dataSend.putInt("totalTimer", totalTimer);
            dataSend.putString("timerSecond", String.valueOf(startTimer)); //new
            intent.putExtra("testquestionid_list", (Serializable) Contest.list_question);//new
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    private void currentQuestionTimer() {
        currentTimter = new CountDownTimer(TIMEOUT, INTERVAL) {
            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished;
                String text = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                // question_time.setText("Question: " + text);
                question_time.setText(text);
                //leftTime = millisUntilFinished;
                //   System.out.println("===  timer  " + millisUntilFinished);
                int progress = (int) (millisUntilFinished / 1000);

                if (progressTimer == null)
                    progressTimer = findViewById(R.id.progressTimer);
                else
                    progressTimer.setCurrentProgress(progress);
                //when left last 5 second we show progress color red
                if (millisUntilFinished <= 6000)
                    progressTimer.SetTimerAttributes(Color.RED, Color.RED);
                else
                    progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_COLOR));
            }

            public void onFinish() {
                currentTimter.cancel();
                //Common.list_question.get(index).setDuration(Long.valueOf(2 * 60000L));
                showQuestion(++index);
            }
        }.start();
    }

    private void totlaQuestionTimer() {
        TOTAL_QUESTION_TIMEOUT = TIMEOUT * Contest.list_question.size(); // 1-question-time * total question;

        new CountDownTimer(TOTAL_QUESTION_TIMEOUT, INTERVAL) {
            public void onTick(long millisUntilFinished) {
                startTimer++;
                long ms = millisUntilFinished;
                String text = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                test_time.setText("Total: " + text);

            }

            public void onFinish() {
                contestDataBaseHelper = new ContestDataBaseHelper(PlayActivity.this);
                contestDataBaseHelper.updateQuestion(Contest.list_question.get(index).getTestquestionid(), "", String.valueOf(Long.valueOf(2 * 60000L)));
                //currentQuestion.setDuration(Long.valueOf(2 * 60000L));
                //nextSubmit.setText("Submit");
                // completed = true;
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        totalQuestion = Contest.list_question.size();

        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue += 120;
                timer++;
                //timerSecond.setText(String.valueOf(timer));
                timerSecond.setText("Timer: " + App.getTimeFromLong(startTimer));
            }

            @Override
            public void onFinish() {
                contestDataBaseHelper = new ContestDataBaseHelper(PlayActivity.this);
                contestDataBaseHelper.updateQuestion(Contest.list_question.get(index).getTestquestionid(), "", String.valueOf(Long.valueOf(2 * 60000L)));
                //Common.list_question.get(index).setDuration(Long.valueOf(2 * 60000L));
                timerOld = timer;
                timer = 0;
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }

    private void getData() {
        if (Contest.list_question.size() > 0) {
            Contest.list_question.clear();
        }
        arrayList = (List<QuestionModel>) getIntent().getSerializableExtra("testquestionid_list");
        String dd = getIntent().getStringExtra("usertestid");
        usertestid = Long.parseLong(dd);
        Contest.list_question.addAll(arrayList);
       /* if (Common.list_question.size() > 0) {
            Common.list_question.clear();
        }
        response = getIntent().getStringExtra("response");
        try {
            JSONObject obj = new JSONObject(response.toString());
            String status = obj.getString("status");
            if (status.equals("true")) {
                JSONObject obj1 = new JSONObject(obj.getString("data"));
                // Log.d("jsonArray=>>>", obj1.getString("usertestid"));
                usertestid = obj1.getLong("usertestid");
                testdate = obj1.getString("testdate");
                starttime = obj1.getString("starttime");

                JSONArray jr = obj1.getJSONArray("questions");
                Type listType = new TypeToken<List<Question>>() {
                }.getType();

                arrayList = new Gson().fromJson(String.valueOf(jr), listType);
                // Question question = new Question();
                //  arrayList.add(question);

                Common.list_question.addAll(arrayList);

                //Log.d("QUESTIONS: ", String.valueOf(arrayList));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> finish());

    }
}