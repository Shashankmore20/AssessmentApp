package com.apptmyz.assessment.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.ContestDataBaseHelper;
import com.apptmyz.assessment.fragment.ReviewFragment;
import com.apptmyz.assessment.model.Contest;
import com.apptmyz.assessment.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    public ImageView prev, next;
    public List<QuestionModel> reviewsList = new ArrayList<>();
    public Toolbar toolbar;
    TextView questionNo;
    String from, question;
    ContestDataBaseHelper contestDataBaseHelper;
    //List<QuestionModel> listWrongAns = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initializeUI();
        from = getIntent().getStringExtra("from");
        question = getIntent().getStringExtra("question");

        if (from.equals("regular")) {
            reviewsList.addAll(Contest.list_question);
        } else if (from.equals("wrongAns")) {

            for (int i = 0; i < Contest.list_question.size(); i++) {
                if (Contest.list_question.get(i).getResult() == 0) {
                    reviewsList.add(Contest.list_question.get(i));
                    //Log.d("TESTTSTS", String.valueOf(listWrongAns));
                }
            }
            // reviewsList.addAll(Contest.list_question);
        } else {
            reviewsList.clear(); //clear data already in cache
           /* Type listType = new TypeToken<List<Question>>() {
            }.getType();
            reviewsList = new Gson().fromJson(String.valueOf(question), listType);*/
            Log.d("DDDD", question);
            contestDataBaseHelper = new ContestDataBaseHelper(this);
            reviewsList = contestDataBaseHelper.getAllQuestionList(question);
            reviewsList.addAll(Contest.list_question);
        }

        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        questionNo = findViewById(R.id.questionNo);


        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), reviewsList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                questionNo.setText(((position + 1) + "/" + reviewsList.size()));
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        questionNo.setText(((viewPager.getCurrentItem() + 1) + "/" + reviewsList.size()));
        prev.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));
        next.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));



    }


    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.review));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //startActivity(intent);
            finish();
        });
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public List<QuestionModel> questionList;

        public ViewPagerAdapter(FragmentManager fm, List<QuestionModel> questionList) {
            super(fm);
            this.questionList = questionList;
        }

        @Override
        public Fragment getItem(int position) {
            return ReviewFragment.newInstance(position, questionList);
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

    }

}