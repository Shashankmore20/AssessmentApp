package com.apptmyz.assessment.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.adapter.MyContestAdapter;
import com.apptmyz.assessment.database.ContestDataBaseHelper;
import com.apptmyz.assessment.model.ContestListModel;

import java.util.ArrayList;
import java.util.List;

public class MyContestActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MyContestAdapter mAdapter;
    //private List<UserTestClass> arrayList = new ArrayList<>();
    private List<ContestListModel> arrayList = new ArrayList<>();
    private ProgressBar progressBar, loadBar;
    LinearLayoutManager manager;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contest);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.empty_view);

        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        mAdapter = new MyContestAdapter(this, arrayList);

        initializeUI();
        MyTest();

    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_test));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        // set toolbar title and back button
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void MyTest() {
        //TestDatabaseHelper db = new TestDatabaseHelper(this);
        ContestDataBaseHelper contestDataBaseHelper = new ContestDataBaseHelper(this);

        arrayList.clear();
        arrayList.addAll(contestDataBaseHelper.getAllContestList());
        recyclerView.setAdapter(new MyContestAdapter(this, arrayList));
        // Toast.makeText(getContext(), "Offline mode enable", Toast.LENGTH_SHORT).show();

        if (arrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}