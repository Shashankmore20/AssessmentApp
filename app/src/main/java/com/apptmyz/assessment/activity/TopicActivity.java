package com.apptmyz.assessment.activity;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.fragment.LearnFragment;
import com.apptmyz.assessment.fragment.TestsFragment;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity {

    private DataSource dataSource;
    UserDetails userDetails;
    TopicSummaryModel topicSummaryModel;

    TabLayout tabLayout;
    ViewPager2 viewPager;

    TextView topic_tv;

    ImageView iv_home;
    Long subjectId;
    String subject;
    Long topicId;
    String topic;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_new);

        dataSource = new DataSource(this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        topicSummaryModel = new TopicSummaryModel();

        Intent intent = getIntent();
        subjectId = intent.getLongExtra("subjectId", 0);
        subject = intent.getStringExtra("subject");
        topicId = intent.getLongExtra("topicId", 0);
        topic = intent.getStringExtra("topic");
        iv_home = findViewById(R.id.iv_home);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Tests"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("Learn"), 1);

        topic_tv = findViewById(R.id.topic_tv);
        topic_tv.setText(topic);
        viewPager = findViewById(R.id.vPager);

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TopicTabAdapter topicTabAdapter = new TopicTabAdapter(TopicActivity.this);
        viewPager.setAdapter(topicTabAdapter);


        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0)
                    tab.setText("Tests");
//                Intent intent = new Intent(getContext(), TodayScheduleFragment.class);
//                startActivity(intent);
                if (position == 1)
                    tab.setText("Learn");
//                Intent intent1 = new Intent(getContext(), WeekScheduleFragment.class);
//                startActivity(intent1);
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                RefreshableFragment selectedFragment = topicTabAdapter.getFragment(tab.getPosition());
                if (selectedFragment != null) {
                    selectedFragment.refresh();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.rg_display_type);
        radioGroup.setOnCheckedChangeListener(changeListener);

        viewPager.setCurrentItem(0);

    }

    private RadioGroup.OnCheckedChangeListener changeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if (checkedId == R.id.rb_test) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }
    };

    class TopicTabAdapter extends FragmentStateAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        public TopicTabAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            Bundle b = new Bundle();
            b.putString("subject", subject);
            b.putLong("subjectId", subjectId);
            b.putString("topic", topic);
            b.putLong("topicId", topicId);

            Fragment frag_new = null;

            if (position == 0) frag_new = new TestsFragment();
            if (position == 1) frag_new = new LearnFragment();

            frag_new.setArguments(b);

            return frag_new;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        public RefreshableFragment getFragment(int position) {
            if (position < fragments.size()) {
                return (RefreshableFragment) fragments.get(position);
            } else {
                Log.e("TopicAdapter", "Index out of bounds: " + position);
                return null;
            }
        }
    }

}