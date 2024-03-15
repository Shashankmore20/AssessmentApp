package com.apptmyz.assessment.activity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.adapter.SubjectAdapter;
import com.apptmyz.assessment.config.CustomHelper;
import com.apptmyz.assessment.databinding.ActivityCreateTestBinding;
//import com.apptmyz.assessment.model.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateTestActivity extends AppCompatActivity {

    private CustomHelper customHelper;
    ActivityCreateTestBinding binding;

    ExpandableListView expandableListView;
    SubjectAdapter subjectAdapter;
    List<String> subjectTitleList;
    HashMap<String, List<String>> subjectDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        binding = ActivityCreateTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeUI();
        expandableList();



    }

    private void expandableList() {
        expandableListView = findViewById(R.id.expandableListViewSample);
       // subjectDetailList = Subject.getData();
        subjectTitleList = new ArrayList<>(subjectDetailList.keySet());
        subjectAdapter = new SubjectAdapter(this, subjectTitleList, subjectDetailList);
        expandableListView.setAdapter(subjectAdapter);

        // This method is called when the group is expanded
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            //Toast.makeText(getApplicationContext(), expandableTitleList.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
        });

        // This method is called when the group is collapsed
        expandableListView.setOnGroupCollapseListener(groupPosition -> {
            //Toast.makeText(getApplicationContext(), expandableTitleList.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
        });

        // This method is called when the child in any group is clicked
        // via a toast method, it is shown to display the selected child item as a sample
        // we may need to add further steps according to the requirements
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
           /* Toast.makeText(getApplicationContext(), expandableTitleList.get(groupPosition)
                    + " -> "
                    + expandableDetailList.get(
                    expandableTitleList.get(groupPosition)).get(
                    childPosition), Toast.LENGTH_SHORT
            ).show();*/
            customHelper = new CustomHelper(CreateTestActivity.this);
            customHelper.getQuestion();
            return false;
        });
    }


    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());

    }
}