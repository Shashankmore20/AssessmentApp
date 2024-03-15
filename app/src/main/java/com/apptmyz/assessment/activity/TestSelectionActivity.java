package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.model.ComplexityModel;

import java.util.ArrayList;

public class TestSelectionActivity extends AppCompatActivity {

    ArrayList<ComplexityModel> levelsArrayList;
    String subject, topic;
    Long subjectId, topicId;

    TextView subject_tv, topic_tv;
    ListView level_lv;

    ImageView home;
    DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selection);

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        subjectId = intent.getLongExtra("subjectId", 0);
        topic = intent.getStringExtra("topic");
        topicId = intent.getLongExtra("topicId", 0);
        home = findViewById(R.id.iv_home);
        dataSource = new DataSource(getApplicationContext());
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // initializeUI();

        levelsArrayList = dataSource.complexity.getComplexityList();

        subject_tv = findViewById(R.id.subject_tv);
        topic_tv = findViewById(R.id.topic_tv);
        level_lv = findViewById(R.id.level_lv);

        subject_tv.setText(subject);
        topic_tv.setText(topic);

        LevelAdapter adapter = new LevelAdapter(this, levelsArrayList);
        level_lv.setAdapter(adapter);


    }

    public class LevelAdapter extends ArrayAdapter<ComplexityModel> {

        private final Activity context;
        ArrayList<ComplexityModel> levelsList;

        public LevelAdapter(Activity context, ArrayList<ComplexityModel> levelsList) {
            super(context, R.layout.topic_row, levelsList);

            this.context = context;
            this.levelsList = levelsList;

        }

        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.topic_row, null, true);

            holder = new ViewHolder();
            holder.topic_tv = (TextView) rowView.findViewById(R.id.topicName_tv);

            holder.topic_tv.setText(levelsList.get(position).getLevel());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), TestInstructionsActivity.class);
                    intent.putExtra("subject", subject);
                    intent.putExtra("subjectId", subjectId);
                    intent.putExtra("topicId", topicId);
                    intent.putExtra("topic", topic);
                    intent.putExtra("level", levelsList.get(position).getId());
                    startActivity(intent);
                }
            });

            return rowView;
        }
    }

    class ViewHolder {
        TextView topic_tv;
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());
    }

}