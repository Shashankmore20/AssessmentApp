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
import com.apptmyz.assessment.database.TestDatabaseHelper;
import com.apptmyz.assessment.databinding.ActivityContestBinding;
import com.apptmyz.assessment.helper.CircleTimer;
import com.apptmyz.assessment.helper.Constant;
import com.apptmyz.assessment.model.Common;
import com.apptmyz.assessment.model.Question;
import com.apptmyz.assessment.model.TimerModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContestActivity extends AppCompatActivity {
    ActivityContestBinding binding;
    private String response;
    private List<Question> arrayList = new ArrayList<>();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_contest);
        binding = ActivityContestBinding.inflate(getLayoutInflater());
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
                nextQtn.setImageResource(R.drawable.ic_baseline_refresh_24);
            }
            if (temp == Common.list_question.size()) {
                clickValue(TempAnswer, getButtonName);
            } else {
                String userAnswer = Common.list_question.get(temp).getUserAnswer();
                Log.d("INDEX", String.valueOf(temp));
                Log.d("UUSSS", String.valueOf(userAnswer));

                if (userAnswer != null) {
                    if (userAnswer == "A" || userAnswer == "B" || userAnswer == "C" || userAnswer == "D") {
                        Log.d("UUSSS111", String.valueOf(userAnswer));
                        showQuestion(++index);
                        UserAnswerDisabled();
                    }
                } else {
                    btnA.setEnabled(true);
                    btnB.setEnabled(true);
                    btnC.setEnabled(true);
                    btnD.setEnabled(true);
                    clickValue(TempAnswer, getButtonName);
                    setButtonColor(view);
                }

            }
        });
        backQtn.setOnClickListener(view -> {
            if (index > 0) {
                thisQuestion = thisQuestion - 2;
                Log.d("BACK_INDEX", String.valueOf(index));
                showQuestion(--index);
                UserAnswerDisabled();
            } else {
                Toast.makeText(this, "First Question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UserAnswerDisabled() {
        currentTimter.cancel();

        if (Common.list_question.get(index).getUserAnswer() != null) {
            if (Common.list_question.get(index).getUserAnswer().trim().equals("A")) {
                a_layout.setBackgroundResource(R.drawable.selected);
                b_layout.setBackgroundResource(R.drawable.notselected);
                c_layout.setBackgroundResource(R.drawable.notselected);
                d_layout.setBackgroundResource(R.drawable.notselected);

            } else if (Common.list_question.get(index).getUserAnswer().trim().equals("B")) {
                a_layout.setBackgroundResource(R.drawable.notselected);
                b_layout.setBackgroundResource(R.drawable.selected);
                c_layout.setBackgroundResource(R.drawable.notselected);
                d_layout.setBackgroundResource(R.drawable.notselected);


            } else if (Common.list_question.get(index).getUserAnswer().trim().equals("C")) {
                a_layout.setBackgroundResource(R.drawable.notselected);
                b_layout.setBackgroundResource(R.drawable.notselected);
                c_layout.setBackgroundResource(R.drawable.selected);
                d_layout.setBackgroundResource(R.drawable.notselected);


            } else if (Common.list_question.get(index).getUserAnswer().trim().equals("D")) {
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

    private void clickValue(String buttonValue, String getButtonName) {
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
        // set time duration
        Common.list_question.get(index).setUserAnswer(correctVal);
        Common.list_question.get(index).setDuration((long) timerOld);

        TimerModel timerModel = new TimerModel();
        timerModel.setQuestionId(String.valueOf(Common.list_question.get(index).getQuestionid()));
        timerModel.setScoretime(String.valueOf(timerOld));

        if (!timerModelArrayList.contains(timerModel)) {
            timerModelArrayList.add(timerModel);
            totalTimer += timerOld;
            //timerModelArrayList.add(timerModel);
            SharedPreferences s_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = s_pref.edit();
            //edit.putString("key","value");
            edit.putString(String.valueOf(Common.list_question.get(index).getQuestionid()), String.valueOf(correctVal));
            edit.apply();
        }
        Log.d("VALLLA", timerModelArrayList.toString());

        if (correctVal.equalsIgnoreCase(Common.list_question.get(index).getAnswer().trim())) {
            score += 10;
            correctAnwer++;
            showQuestion(++index);

        } else {
            showQuestion(++index);

        }

        txtScore.setText(String.format("%d", score));
    }

    private void showQuestion(int index) {

        if (index < totalQuestion) {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d/%d", thisQuestion, totalQuestion));
            questionNo.setText(String.format("%d", thisQuestion));
            progressBar.setProgress(0);
            progressValue = 0;
          /*  if (Common.list_question.get(index).getIsImageQuestion().equals("true")) {
                StorageReference gsReference = storage.getReferenceFromUrl(Common.list_question.get(index).getImage());
                Glide.with(getBaseContext())
                        .load(gsReference)
                        .into(question_image);
                question_text.setText("Q. " + Common.list_question.get(index).getQuestion());
                question_image.setVisibility(View.VISIBLE);
                //question_text.setVisibility(View.INVISIBLE);

            } else {*/
            question_text.setText(Html.fromHtml(Common.list_question.get(index).getQuestion()));
            //question_image.setVisibility(View.INVISIBLE);
            //question_text.setVisibility(View.VISIBLE);
            // }

            btnA.setText(Html.fromHtml(Common.list_question.get(index).getChoice1()));
            btnB.setText(Html.fromHtml(Common.list_question.get(index).getChoice2()));
            btnC.setText(Html.fromHtml(Common.list_question.get(index).getChoice3()));
            btnD.setText(Html.fromHtml(Common.list_question.get(index).getChoice4()));
            mCountDown.start();
            currentTimter.start();
        } else {
            //Convert Object data to json array
            JsonArray questionList = new Gson().toJsonTree(arrayList).getAsJsonArray();
            //Log.d("jsonaray",questionList.toString());
            testDatabaseHelper = new TestDatabaseHelper(this);
            testDatabaseHelper.insertData(String.valueOf(usertestid), "MYUSERID" + usertestid, testdate, starttime, questionList.toString());

            Intent intent = new Intent(this, CompleteActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnwer);
            dataSend.putLong("usertestid", usertestid);
            intent.putExtra("response", String.valueOf(response));
            dataSend.putString("QTIMER", String.valueOf(timerModelArrayList));
            dataSend.putInt("totalTimer", totalTimer);
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
                Common.list_question.get(index).setDuration(Long.valueOf(2 * 60000L));
                showQuestion(++index);
            }
        }.start();
    }

    private void totlaQuestionTimer() {
        TOTAL_QUESTION_TIMEOUT = TIMEOUT * Common.list_question.size(); // 1-question-time * total question;

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
                //currentQuestion.setDuration(Long.valueOf(2 * 60000L));
                //nextSubmit.setText("Submit");
                // completed = true;
            }
        }.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        totalQuestion = Common.list_question.size();

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
                Common.list_question.get(index).setDuration(Long.valueOf(2 * 60000L));
                timerOld = timer;
                timer = 0;
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }

    private void getData() {
        if (Common.list_question.size() > 0) {
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
        }
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> finish());

    }
}