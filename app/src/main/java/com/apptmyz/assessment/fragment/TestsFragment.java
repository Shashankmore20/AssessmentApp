package com.apptmyz.assessment.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.MainActivity2;
import com.apptmyz.assessment.activity.NotificationActivity;
import com.apptmyz.assessment.activity.SubjectActivity;
import com.apptmyz.assessment.activity.TakenTestsActivity;
import com.apptmyz.assessment.activity.TestSelectionActivity;
import com.apptmyz.assessment.activity.TopicActivity;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.ComplexityModel;
import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestsFragment extends Fragment {

    DataSource dataSource;
    UserDetails userDetails;
    TopicSummaryModel topicSummaryModel;

    float TotalQuestion = 0;

    ArrayList<ComplexityModel> levelsArrayList;

    String subject, topic;
    Long subjectId, topicId;
    float avg_performance;

    TextView perfomance_tv, crtAnsCount_tv, totalTests_tv, avgTime_tv, noData;
    ProgressBar perfomance_bar;
    Button your_tests_btn, take_test_btn;
    Spinner levels_sp;

    Spinner subjects;
    ArrayList<SubjectModel> arrayList_subjects;

    DataSharedPref dataSharedPref;
    PieChart pieChart;
    int totalTestAttempt;
    int levelid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        Bundle b = getArguments();

        subject = b.getString("subject");
        topic = b.getString("topic");
        subjectId = b.getLong("subjectId");
        topicId = b.getLong("topicId");

        dataSource = new DataSource(getContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        topicSummaryModel = new TopicSummaryModel();
        pieChart = view.findViewById(R.id.pieChartNew);

        System.out.println(topicSummaryModel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (view.getId()) {
                    case R.id.your_tests_btn:
                        intent = new Intent(getContext(), TakenTestsActivity.class);
                        intent.putExtra("subjectId", subjectId);
                        intent.putExtra("subject", subject);
                        intent.putExtra("topicId", topicId);
                        intent.putExtra("topic", topic);
                        startActivity(intent);
                        break;

                    case R.id.take_test_btn:
                        intent = new Intent(getContext(), TestSelectionActivity.class);
                        intent.putExtra("subjectId", subjectId);
                        intent.putExtra("subject", subject);
                        intent.putExtra("topicId", topicId);
                        intent.putExtra("topic", topic);
                        startActivity(intent);
                        break;
                }

            }
        };

        perfomance_bar = view.findViewById(R.id.perfomance_bar);
        perfomance_tv = view.findViewById(R.id.perfomance_tv);
        crtAnsCount_tv = view.findViewById(R.id.crtAnsCount_tv);
        totalTests_tv = view.findViewById(R.id.totalTests_tv);
        avgTime_tv = view.findViewById(R.id.avgTime_tv);
        levels_sp = view.findViewById(R.id.levels_sp);

        noData = view.findViewById(R.id.NoDataTest);
        your_tests_btn = view.findViewById(R.id.your_tests_btn);
        your_tests_btn.setOnClickListener(listener);
        take_test_btn = view.findViewById(R.id.take_test_btn);
        take_test_btn.setOnClickListener(listener);

        levelsArrayList = dataSource.complexity.getComplexityList();

        subjects = view.findViewById(R.id.subjects_spinner);

        arrayList_subjects = dataSource.subjects.getSubjects();
        ArrayAdapter<SubjectModel> arrayAdapter_subjects = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList_subjects);
        subjects.setAdapter(arrayAdapter_subjects);
        subjects.setSelection(getPosition());
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TopicSummaryModel temp = new TopicSummaryModel();
                subjectId = arrayList_subjects.get(position).getId();
                temp = dataSource.topicTestsSummary.getTestsSummary(levelid, subjectId.intValue());
                setStats(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<ComplexityModel> arrayAdapter_levels = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, levelsArrayList);
        levels_sp.setAdapter(arrayAdapter_levels);

        levels_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TopicSummaryModel temp = new TopicSummaryModel();
                levelid = levelsArrayList.get(i).getId();
                temp = dataSource.topicTestsSummary.getTestsSummary(levelid, subjectId.intValue());
                setStats(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    public int getPosition() {
        int position = 0;

        for (SubjectModel d : arrayList_subjects) {
            if (subjectId == d.getId()) {
                return position;
            }
            position = position + 1;
        }
        return 0;
    }

    public void setStats(TopicSummaryModel temp) {

        if (temp.getTotalQuestions() != 0) {
            perfomance_bar.setProgress(temp.getTotalCorrectQuestions() * 100 / temp.getTotalQuestions());
            TotalQuestion = temp.getTotalQuestions();
            avg_performance = (temp.getTotalCorrectQuestions() * 100 / temp.getTotalQuestions());
            perfomance_tv.setText(avg_performance + "%");
            crtAnsCount_tv.setText(temp.getTotalCorrectQuestions() + "/" + temp.getTotalQuestions());
            noData.setVisibility(View.INVISIBLE);
        } else {
            perfomance_bar.setProgress(0);
            avg_performance = (0);
            perfomance_tv.setText(avg_performance + "%");
            crtAnsCount_tv.setText(0 + "/" + 0);
            noData.setVisibility(View.VISIBLE);
        }

        totalTests_tv.setText(temp.getTotalAttemptedTests() + "");
        totalTestAttempt = temp.getTotalAttemptedTests();
        Log.d("tottests", String.valueOf(totalTestAttempt));

        //Calculating avg time from milliSeconds

        long minutes = (temp.getAverageDuration() / 1000) / 60;
        long seconds = (temp.getAverageDuration() / 1000) % 60;

        avgTime_tv.setText(minutes + "m " + seconds + "s");

        setPieChart(temp.getTotalCorrectQuestions(), temp.getTotalAttemptedTests());
//        setPieChart(20, 15);
    }

    private void setPieChart(int correct, int wrong) {
        //PIE CHART CODE
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(correct, "Questions Correct"));
        entries.add(new PieEntry(wrong, "Tests Attempted"));

        if (entries.isEmpty()) {
            pieChart.setNoDataText("No data");
            pieChart.setNoDataTextColor(Color.BLACK);
            pieChart.setNoDataTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            float deliveredValue = (entries.get(0).getValue() / TotalQuestion) * 100; // Get the value of "Delivered"
            Log.d("totalQuestions", String.valueOf(TotalQuestion));
            Log.d("entries 0", String.valueOf(entries.get(0)));
            Log.d("deliv Value", String.valueOf(deliveredValue));
            // Calculate total value
            int totalValue = 0;
            for (PieEntry entry : entries) {
                totalValue += (int) entry.getValue();
            }
            int[] customColors = {getResources().getColor(R.color.btn_color), getResources().getColor(R.color.appcolor)};

            // Create a dataset with the data
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setDrawValues(false);
            dataSet.setColors(customColors);  // Set colors for each entry
            // Create PieData object and set it to the chart
            PieData pieData = new PieData(dataSet);
            pieData.setValueFormatter(new PercentFormatter(pieChart));  // Set the percentage formatter
            pieChart.setData(pieData);
            pieChart.setDrawHoleEnabled(true);
            boolean isHoleEnabled = pieChart.isDrawHoleEnabled();
            // Log the result or use it in your logic
//        Log.d("HoleEnabled", "Is Hole Enabled: " + isHoleEnabled);
            // Set chart description
            pieChart.getDescription().setEnabled(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.setUsePercentValues(false);
            pieChart.setEntryLabelTextSize(0f);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.animateY(2000);
            pieChart.animateX(2000);
            pieChart.setDrawCenterText(false);

//      Set center text
            String centerText = String.valueOf((int) deliveredValue);
            Log.d("centerText", centerText + "%");
            pieChart.setCenterText(centerText + "%");
            pieChart.setCenterTextSize(20f);
            if (!Float.isNaN(deliveredValue) && totalTestAttempt != 0) {
                pieChart.setDrawCenterText(true);
            } else {
                pieChart.setDrawCenterText(false);
            }
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
            // Refresh the chart
            pieChart.invalidate();

            //END OF PIECHART CODE
        }
    }
}