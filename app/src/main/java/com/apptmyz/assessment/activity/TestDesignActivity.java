package com.apptmyz.assessment.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.helper.AudienceProgress;
import com.apptmyz.assessment.helper.CircleImageView;

public class TestDesignActivity extends AppCompatActivity {

    CircleImageView imgProfile;
    public AudienceProgress progress;
    TextView tvName, tvRank, tvScore, tvCoin, tvTotalQue, tvCorrect, tvInCorrect, tvCorrectP, tvInCorrectP;
    String totalQues = "100";
    String correctQues = "40";
    String inCorrectQues = "60";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_design);
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        tvScore = findViewById(R.id.tvScore);
        tvCoin = findViewById(R.id.tvCoin);

        tvTotalQue = findViewById(R.id.tvAttended);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvInCorrect = findViewById(R.id.tvInCorrect);
        tvCorrectP = findViewById(R.id.tvCorrectP);
        tvInCorrectP = findViewById(R.id.tvInCorrectP);
        progress = findViewById(R.id.progress);


        float percentCorrect = (Float.parseFloat(correctQues) * 100) / Float.parseFloat(totalQues);
        float percentInCorrect = (Float.parseFloat(inCorrectQues) * 100) / Float.parseFloat(totalQues);
        int perctn = Math.round(percentCorrect);
        int perctnincorrect = Math.round(percentInCorrect);
        tvCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_green, 0, 0, 0);
        tvInCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red, 0, 0, 0);
        tvCorrectP.setText(perctn + "%");
        tvInCorrectP.setText(perctnincorrect + "%");
        progress.SetAttributesForStatistics(getApplicationContext());

        progress.setMaxProgress(Integer.parseInt(String.valueOf(100)));
        progress.setCurrentProgress(Integer.parseInt(String.valueOf(50)));

    }
}