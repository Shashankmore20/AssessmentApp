package com.apptmyz.assessment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.CustomHelper;
import com.apptmyz.assessment.databinding.ActivityCompleteBinding;
import com.apptmyz.assessment.model.TimerModel;
import com.apptmyz.assessment.widget.ProgressBarAnimation;

import java.util.ArrayList;
import java.util.List;

public class CompleteActivity extends AppCompatActivity {
    // Button btnTryAgain, backToHome;
    TextView txtResultScore, getTxtResultQuestion, show_progress, scoreResult, txt_result_title;
    ProgressBar progressResult;
    private double percentageCal;
    private int percentage;
    private String result;
    //result view data
    private RecyclerView recyclerView;
    // private ResultViewAdapter quizAdapter;
    // private List<Object> arrayList = new ArrayList<>();
    //private QuizeScoreHelper quizeScoreHelper;
    private String response;
    private CustomHelper customHelper;
    public AppCompatActivity activity;
    ActivityCompleteBinding binding;
    private List<TimerModel> timerModel = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_complete);
        binding = ActivityCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //quizeScoreHelper = new QuizeScoreHelper(this);
        response = getIntent().getStringExtra("response");
        Log.d("RESPONSE", response);
        show_progress = findViewById(R.id.show_progress);
        txt_result_title = findViewById(R.id.txt_result_title);

      /*  scoreResult = findViewById(R.id.scoreResult);
        txtResultScore = findViewById(R.id.txtTotalScore);
        getTxtResultQuestion = findViewById(R.id.txtTotalQuestion);*/
        progressResult = findViewById(R.id.progressResult);
        // initializeUI();
        // btnTryAgain = findViewById(R.id.btnTryAgain);
        // backToHome = findViewById(R.id.backToHome);
        //getData();

        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            String timerSecond = extra.getString("timerSecond");
            int correctAnswer = extra.getInt("CORRECT");
            int wrontAnswer = totalQuestion - correctAnswer;
            //  int quizid = extra.getInt("QUIZID");
            String qtimer = extra.getString("QTIMER");

            int totalTimer = extra.getInt("totalTimer");

            binding.right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
            binding.right.setText("" + correctAnswer);
            binding.wrong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close, 0, 0, 0);
            binding.wrong.setText("" + wrontAnswer);
            binding.tvScore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_wine_bar, 0, 0, 0);
            binding.tvScore.setText("" + score);
            binding.tvCoin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_access_time, 0, 0, 0);
            //binding.tvCoin.setText("" + timerSecond);
            binding.tvCoin.setText("" + timerSecond);

            percentageCal = ((double) correctAnswer / totalQuestion) * 100;
            percentage = (int) percentageCal;
            progressResult.setProgress(percentage, true);
            ProgressBarAnimation anim = new ProgressBarAnimation(progressResult, 0, (float) percentage);
            anim.setDuration(1000);
            progressResult.startAnimation(anim);
            progressResult.getProgressDrawable().setColorFilter(Color.CYAN, android.graphics.PorterDuff.Mode.SRC_IN);
            show_progress.setText(percentage + "%");

            //  txtResultScore.setText(String.format("SCORE : %d", percentage));
            // getTxtResultQuestion.setText(String.format("PASSED : %d/%d", correctAnswer, totalQuestion));

            if (percentage >= 90) {
                result = "Very Good";
            } else if (percentage >= 80) {
                result = "Good";
            } else if (percentage >= 60) {
                result = "Average";
            } else {
                binding.victorymsg.setText(getString(R.string.defeat));
                binding.victoryimg.setBackgroundResource(R.drawable.ic_defeat);
                txt_result_title.setText(getString(R.string.not_completed));
                result = "You need to practice";
            }
            Log.d("DDDDDD", "result=" + result + " percentage=" + percentage);
            //  scoreResult.setText(result);

          /*  if (!quizeScoreHelper.CheckIsDataAlreadyInDBorNot(String.valueOf(quizid))) {
                Log.d("save data","success");
                quizeScoreHelper.insertData(String.valueOf(quizid), "s", String.valueOf((int) percentage));
            }
            callAPI((int) percentage, quizid, totalQuestion, correctAnswer, qtimer);*/
        }


    }

    public void Home(View view) {
        Intent intent = new Intent(CompleteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void PlayAgain(View view) {
        customHelper = new CustomHelper(CompleteActivity.this);
        customHelper.getQuestion();
    }

    public void ShareScore(View view) {
        //String shareMsg = "I have finished Level No : " + Utils.RequestlevelNo + " with " + Utils.level_score + " Score in " + getString(R.string.app_name);
        // Utils.ShareInfo(scrollView, activity, shareMsg);
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

   /* private void callAPI(int score, int quizid, int totalQuestion, int correctAnswer, String qtimer) {
        int wrongAnswer = totalQuestion - correctAnswer;
        String tdata = totalQuestion + ":" + correctAnswer + ":" + wrongAnswer;
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        url = url + "?q=" + quizid + "&s=" + score + "&uid=" + uid + "&tdata=" + tdata + "&t=" + qtimer;
        StringRequest request = new StringRequest(url, response -> Log.d("response==>", response), error -> Log.d("CODE ERRORRRRR =>>>>", error.toString()));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }*/

//    private void getData() {
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        quizAdapter = new ResultViewAdapter(this, arrayList);
//        //Question question = (Question) Common.list_question;
//        arrayList.addAll(Common.list_question);
//        recyclerView.setAdapter(quizAdapter);
//    }

    public void ReviewAnswers(View view) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("from", "regular");
        startActivity(intent);
    }

    public void WrongAns(View view) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("from", "wrongAns");
        startActivity(intent);
    }
}