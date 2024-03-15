package com.apptmyz.assessment.fragment;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.widget.SwitchCompat;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.LoginActivity;

import com.apptmyz.assessment.activity.NotificationActivity;

import com.apptmyz.assessment.activity.ReviewTestActivity;
import com.apptmyz.assessment.activity.ViewTargets;
import com.apptmyz.assessment.adapter.RecyclerAdapterRanks;
import com.apptmyz.assessment.config.App;

import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.DashboardModel;
import com.apptmyz.assessment.model.RankLevelsModel;

import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.model.TargetModel;
import com.apptmyz.assessment.model.TestModel;
import com.apptmyz.assessment.model.TopicModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class
HomeFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context = getContext();
    TextView name_tv, class_tv, dailyTarget, weeklyTarget, monthlyTarget, viewTargets, noData, goodMA;

    UserDetails userDetails;
    PieChart pieChart;
    LottieAnimationView noDataAnim;
    ImageView notification_iv, imageGood;
    ArrayList<SubjectModel> subjectsList;
    ArrayList<String> subjectNameList;

    Button addTarget;
    ArrayList<TopicModel> topicList;
    ArrayList<String> topicNameList;
    private DataSource dataSource;
    AutoCompleteTextView subjects;
    ArrayList<String> arrayList_subjects;

    private AlertDialog dialog;
    private String selectedDay = "";

    long selectedSubjectIID = 0;
    long selectedSubjectIIDDashboard = 0;


    private ArrayAdapter<String> arrayAdapter_topics;
    private ArrayAdapter<String> arrayAdapter_subjects;
    private TextView selectedDayTextView;
    RecyclerView recycler_view, recycler;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapterRanks recyclerAdapterRanks;

    ProgressDialog getDashboardDataDialog;
    private DashboardModel dashboardModel;

    TextView scores_text, lessonForQuiz, topicForQuiz;
    private TestModel recentTest = null;
    LinearLayout recentQuizlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_new, container, false);

        viewTargets = view.findViewById(R.id.viewTargets);
        dailyTarget = view.findViewById(R.id.daily);
        weeklyTarget = view.findViewById(R.id.weekly);
        monthlyTarget = view.findViewById(R.id.monthly);
        scores_text = view.findViewById(R.id.scores);
        lessonForQuiz = view.findViewById(R.id.lessonForQuiz);
        topicForQuiz = view.findViewById(R.id.topicForQuiz);
        recentQuizlayout = view.findViewById(R.id.layout2);
        noData = view.findViewById(R.id.noData);
        noDataAnim = view.findViewById(R.id.noDataAnim);
        //Get Subjects
        dataSource = new DataSource(getContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        subjectsList = dataSource.subjects.getSubjects();
        subjectNameList = new ArrayList<>();

        for (SubjectModel subject : subjectsList) {
            subjectNameList.add(subject.getSubjectName());
        }

        topicList = dataSource.topics.getTopics(selectedSubjectIID);
        topicNameList = new ArrayList<>();

        for (TopicModel topic : topicList) {
            topicNameList.add(topic.getTopicName());
            topicNameList.add(topic.getTopicId().toString());
        }

        dataSource.days.clearAll();
        dataSource.weeks.clearAll();
        dataSource.months.clearAll();

        if (dataSource != null && dataSource.days != null) {
            dataSource.days.addDaysManually();
        }
        dataSource.weeks.addWeeksManually();
        dataSource.months.addMonthsManually();

        dailyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDailyTargetDialog();
            }
        });

        weeklyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenWeeklyTargetDialog();
            }
        });

        monthlyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMonthlyTargetDialog();
            }
        });
        viewTargets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewTargets.class);
                startActivity(intent);
            }
        });

        name_tv = view.findViewById(R.id.name_tv);
        class_tv = view.findViewById(R.id.class_tv);
        goodMA = view.findViewById(R.id.goodMA);
        imageGood = view.findViewById(R.id.imageGood);
        subjects = view.findViewById(R.id.subjects_spinner);

        LocalTime currentTime = LocalTime.now();

        // Checking the time to set the greeting accordingly
        if (currentTime.isAfter(LocalTime.NOON) && currentTime.isBefore(LocalTime.of(18, 0))) {
            goodMA.setText("Good Afternoon");
            imageGood.setImageResource(R.drawable.sun_image);
        } else if (currentTime.isBefore(LocalTime.NOON)) {
            goodMA.setText("Good Morning");
            imageGood.setImageResource(R.drawable.partly_cloudy_day_24px);
        } else {
            goodMA.setText("Good Evening");
            imageGood.setImageResource(R.drawable.outline_nights_stay_24);
        }

////        StudyFragment.SubjectsAdapter customAdapter = new StudyFragment.SubjectsAdapter(getContext(), R.layout.subject_card, subjectsList);
////        subjects.setAdapter(customAdapter);

        recycler = view.findViewById(R.id.recycler);
        pieChart = view.findViewById(R.id.pieChart);

        //NOTIFICATION ON CLICK LISTENER//
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.notification_iv:
                        Intent intent = new Intent(getContext(), NotificationActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };
        notification_iv = view.findViewById(R.id.notification_iv);
        notification_iv.setOnClickListener(listener);
        //

        name_tv.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        class_tv.setText("Class " + userDetails.getUserClass());

        ArrayAdapter<String> arrayAdapter_subjects = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, subjectNameList);
        subjects.setAdapter(arrayAdapter_subjects);
        subjects.setText(subjects.getAdapter().getItem(0).toString(), false);
        SubjectModel selectedSubjectModel = findSubjectByName(subjects.getAdapter().getItem(0).toString());
        selectedSubjectIIDDashboard = selectedSubjectModel.getId();
        subjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSubject = (String) adapterView.getItemAtPosition(position);
//                long selectedSubjectId = (long) adapterView.getItemAtPosition(position);

                // Log the selected subject
//                Log.d("Selected Subject", selectedSubject);

                SubjectModel selectedSubjectModel = findSubjectByName(selectedSubject);
                selectedSubjectIIDDashboard = selectedSubjectModel.getId();
                getDashboardData();
                Log.d("Selected Subject", "" + selectedSubjectModel.getId());


            }
        });
        subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjects.showDropDown();
            }
        });
        subjects.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    subjects.showDropDown();
                }
            }
        });

        getUser();

        return view;
    }

    private void setPieChart(int correct, int wrong) {
        //PIE CHART CODE
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(correct, "Correct"));
        entries.add(new PieEntry(wrong, "InCorrect"));

        //check
        if (entries.isEmpty()) {
            pieChart.setNoDataText("No data");
            pieChart.setNoDataTextColor(Color.BLACK);
        } else {
            float totalValue = correct + wrong;
            float deliveredValue = (entries.get(0).getValue() / recentTest.getTotalQuestions()) * 100; // Get the value of "Delivered"
            Log.d("Total Q's", recentTest.getTotalQuestions().toString());
            // Calculate total value
            for (PieEntry entry : entries) {
                totalValue += (int) entry.getValue();
            }
            int[] customColors = {0xFF85BAB3, 0xFF548B84};
            // Create a dataset with the data
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setDrawValues(false);
            dataSet.setColors(customColors);  // Set colors for each entry
            // Create PieData object and set it to the chart
            PieData pieData = new PieData(dataSet);
            pieData.setValueFormatter(new PercentFormatter(pieChart));  // Set the percentage formatter
            pieChart.setData(pieData);
            pieChart.setDrawHoleEnabled(false);
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
//      Set center text
            String centerText = String.valueOf((int) deliveredValue);
            pieChart.setCenterText(centerText + "%");
            pieChart.setCenterTextSize(13f);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
            // Refresh the chart
            pieChart.invalidate();
        }
        //END OF PIECHART CODE
    }

    private SubjectModel findSubjectByName(String subjectName) {
        for (SubjectModel subject : subjectsList) {
            if (subject.getSubjectName().equals(subjectName)) {
                return subject;
            }
        }
        return null;
    }

    private String findSubjectByID(long subjectId) {
        for (SubjectModel subject : subjectsList) {
            if (subject.getId() == subjectId) {
                Log.d("Subject", subject.getSubjectName().toString());
                topicList = dataSource.topics.getTopics(subjectId);
                return subject.getSubjectName();
            }
        }
        return null;
    }

    private String findTopicByID(long topicId) {
        for (TopicModel topic : topicList) {
            if (topic.getTopicId() == topicId) {
                Log.d("Topic", topic.getTopicName().toString());
                return topic.getTopicName();
            }
        }
        return null;
    }

    private void OpenMonthlyTargetDialog() {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.target_monthy_dialog, null);
            builder.setView(dialogView);

            AutoCompleteTextView subject = dialogView.findViewById(R.id.subjects_spinnerMonthly);
            AutoCompleteTextView topic = dialogView.findViewById(R.id.topic_spinnerMonthly);
            Button submit = dialogView.findViewById(R.id.submitBtnMonthly);
            ImageView close = dialogView.findViewById(R.id.close);
            ImageView startMonthDate = dialogView.findViewById(R.id.startMonthDateCal);
            ImageView endMonthDate = dialogView.findViewById(R.id.endMonthDateCal);
            SwitchCompat repeatDaily = dialogView.findViewById(R.id.repeatMonthlySwitch);
            SwitchCompat repeatSpecificDay = dialogView.findViewById(R.id.repeatSpecificMonthSwitch);
            TextView repeatMonth = dialogView.findViewById(R.id.repeatMonth);
            repeatMonth.setText(getMonthName(Calendar.getInstance().get(Calendar.MONTH)));
//            TextView repeatDay = dialogView.findViewById(R.id.repeatDay);
            TextView january = dialogView.findViewById(R.id.jan);
            TextView february = dialogView.findViewById(R.id.feb);
            TextView march = dialogView.findViewById(R.id.mar);
            TextView april = dialogView.findViewById(R.id.apr);
            TextView may = dialogView.findViewById(R.id.may);
            TextView june = dialogView.findViewById(R.id.jun);
            TextView july = dialogView.findViewById(R.id.jul);
            TextView august = dialogView.findViewById(R.id.aug);
            TextView september = dialogView.findViewById(R.id.sep);
            TextView october = dialogView.findViewById(R.id.oct);
            TextView november = dialogView.findViewById(R.id.nov);
            TextView december = dialogView.findViewById(R.id.dec);


            Calendar calendar = Calendar.getInstance();
            int monthOfyear = calendar.get(Calendar.MONTH);
            // Set click listeners for each day
            setMonthClickListener(january, "Jan", "January", monthOfyear == Calendar.JANUARY);
            setMonthClickListener(february, "Feb", "February", monthOfyear == Calendar.FEBRUARY);
            setMonthClickListener(march, "Mar", "March", monthOfyear == Calendar.MARCH);
            setMonthClickListener(april, "Apr", "April", monthOfyear == Calendar.APRIL);
            setMonthClickListener(may, "May", "May", monthOfyear == Calendar.MAY);
            setMonthClickListener(june, "Jun", "June", monthOfyear == Calendar.JUNE);
            setMonthClickListener(july, "Jul", "July", monthOfyear == Calendar.JULY);
            setMonthClickListener(august, "Aug", "August", monthOfyear == Calendar.AUGUST);
            setMonthClickListener(september, "Sep", "September", monthOfyear == Calendar.SEPTEMBER);
            setMonthClickListener(october, "Oct", "October", monthOfyear == Calendar.OCTOBER);
            setMonthClickListener(november, "Nov", "November", monthOfyear == Calendar.NOVEMBER);
            setMonthClickListener(december, "Dec", "December", monthOfyear == Calendar.DECEMBER);

            dialog = builder.create();
            dialog.show();

            startMonthDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePickerDialogMonthStartDate();
                }
            });

            endMonthDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePickerDialogMonthEndDate();
                }
            });
            repeatDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        // "Off" is selected, uncheck "On"
                        repeatSpecificDay.setChecked(false);
//                        Log.d("checked", "Daily button is checked");
//                        repeatDay.setVisibility(View.GONE); // Hide the TextView
                    } else {
                        // "Off" is unchecked, you can handle this case if needed
                    }
                }
            });

            repeatSpecificDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        // "On" is selected, uncheck "Off"
                        repeatDaily.setChecked(false);
                        Log.d("checked", "Specific button is checked");
//                        repeatDay.setVisibility(View.VISIBLE); // Show the TextView
                    } else {
                        // "On" is unchecked, you can handle this case if needed
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            arrayAdapter_subjects = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, subjectNameList);
            subject.setAdapter(arrayAdapter_subjects);

            subject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedSubject = (String) adapterView.getItemAtPosition(position);

                    // Log the selected subject
//                    Log.d("Selected Subject", selectedSubject);

                    SubjectModel selectedSubjectModel = findSubjectByName(selectedSubject);

                    // Update the selectedSubjectIID with the ID of the selected subject
                    if (selectedSubjectModel != null) {
                        selectedSubjectIID = selectedSubjectModel.getId();

                        // Fetch topics based on the selected subject
                        topicList = selectedSubjectModel.getTopics();
                        topicNameList.clear();

                        for (TopicModel topic : topicList) {
                            topicNameList.add(topic.getTopicName());
                        }

                        // Notify the adapter about the data change
                        arrayAdapter_topics.notifyDataSetChanged();
                    }
                }
            });
            subject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subject.showDropDown();
                }
            });
            subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        subject.showDropDown();
                    }
                }
            });

            arrayAdapter_topics = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, topicNameList);
            topic.setAdapter(arrayAdapter_topics);

            topic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedTopic = (String) adapterView.getItemAtPosition(position);

                    // Log the selected topic
//                    Log.d("Selected topic", selectedTopic);
                }
            });
            topic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topic.showDropDown();
                }
            });
            topic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        topic.showDropDown();
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkValidationMonthly();
                }
            });
        }
    }

    private void OpenWeeklyTargetDialog() {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.target_weekly_dialog, null);
            builder.setView(dialogView);

            AutoCompleteTextView subject = dialogView.findViewById(R.id.subjects_spinnerWeekly);
            AutoCompleteTextView topic = dialogView.findViewById(R.id.topic_spinnerWeekly);
            Button submit = dialogView.findViewById(R.id.submitBtnWeekly);
            ImageView close = dialogView.findViewById(R.id.close);
            ImageView startDate = dialogView.findViewById(R.id.startWeekCal);
            ImageView endDate = dialogView.findViewById(R.id.endWeekCal);
            SwitchCompat repeatDaily = dialogView.findViewById(R.id.repeatWeeklySwitch);
            SwitchCompat repeatSpecificDay = dialogView.findViewById(R.id.repeatSpecificWeekSwitch);
            TextView repeatDay = dialogView.findViewById(R.id.repeatWeek);
            repeatDay.setText(getWeekName(Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)));
//            TextView repeatDay = dialogView.findViewById(R.id.repeatDay);
            TextView week1 = dialogView.findViewById(R.id.week1);
            TextView week2 = dialogView.findViewById(R.id.week2);
            TextView week3 = dialogView.findViewById(R.id.week3);
            TextView week4 = dialogView.findViewById(R.id.week4);
//            TextView friday = dialogView.findViewById(R.id.friday);
//            TextView saturday = dialogView.findViewById(R.id.saturday);
//            TextView sunday = dialogView.findViewById(R.id.sunday);

            Calendar calendar = Calendar.getInstance();
            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
            // Set click listeners for each day
            setWeekClickListener(week1, "Week1", "Week 1", weekOfMonth == 1);
            setWeekClickListener(week2, "Week2", "Week 2", weekOfMonth == 2);
            setWeekClickListener(week3, "Week3", "Week 3", weekOfMonth == 3);
            setWeekClickListener(week4, "Week4", "Week 4", weekOfMonth == 4);
//            setDayClickListener(friday, "F", "Friday", dayOfWeek == Calendar.FRIDAY);
//            setDayClickListener(saturday, "S", "Saturday", dayOfWeek == Calendar.SATURDAY);
//            setDayClickListener(sunday, "S", "Sunday", dayOfWeek == Calendar.SUNDAY);

            dialog = builder.create();
            dialog.show();

            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePickerDialogWeekStartDate();
                }
            });

            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePickerDialogWeekEndDate();
                }
            });
            repeatDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        // "Off" is selected, uncheck "On"
                        repeatSpecificDay.setChecked(false);
                        Log.d("checked", "Daily button is checked");
//                        repeatDay.setVisibility(View.GONE); // Hide the TextView
                    } else {
                        // "Off" is unchecked, you can handle this case if needed
                    }
                }
            });

            repeatSpecificDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        // "On" is selected, uncheck "Off"
                        repeatDaily.setChecked(false);
                        Log.d("checked", "Specific button is checked");
//                        repeatDay.setVisibility(View.VISIBLE); // Show the TextView
                    } else {
                        // "On" is unchecked, you can handle this case if needed
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            arrayAdapter_subjects = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, subjectNameList);
            subject.setAdapter(arrayAdapter_subjects);

            subject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedSubject = (String) adapterView.getItemAtPosition(position);

                    // Log the selected subject
                    Log.d("Selected Subject", selectedSubject);

                    SubjectModel selectedSubjectModel = findSubjectByName(selectedSubject);

                    // Update the selectedSubjectIID with the ID of the selected subject
                    if (selectedSubjectModel != null) {
                        selectedSubjectIID = selectedSubjectModel.getId();

                        // Fetch topics based on the selected subject
                        topicList = selectedSubjectModel.getTopics();
                        topicNameList.clear();

                        for (TopicModel topic : topicList) {
                            topicNameList.add(topic.getTopicName());
                        }

                        // Notify the adapter about the data change
                        arrayAdapter_topics.notifyDataSetChanged();
                    }
                }
            });
            subject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subject.showDropDown();
                }
            });
            subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        subject.showDropDown();
                    }
                }
            });

            arrayAdapter_topics = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, topicNameList);
            topic.setAdapter(arrayAdapter_topics);

            topic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedTopic = (String) adapterView.getItemAtPosition(position);

                    // Log the selected topic
                    Log.d("Selected topic", selectedTopic);
                }
            });
            topic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topic.showDropDown();
                }
            });
            topic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        topic.showDropDown();
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkValidationWeely();
                }
            });
        }
    }

    private void OpenDailyTargetDialog() {
        Context context = getContext();
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.target_daily_dialog, null);
            builder.setView(dialogView);

            Button submit = dialogView.findViewById(R.id.submitBtnDaily);
            ImageView close = dialogView.findViewById(R.id.close);
            ImageView startDate = dialogView.findViewById(R.id.startDateCal);
            ImageView endDate = dialogView.findViewById(R.id.endDateCal);
            SwitchCompat repeatDaily = dialogView.findViewById(R.id.repeatDailySwitch);
            SwitchCompat repeatSpecificDay = dialogView.findViewById(R.id.repeatSpecificDaySwitch);
            AutoCompleteTextView subject = dialogView.findViewById(R.id.subjects_spinnerDaily);
            AutoCompleteTextView topic = dialogView.findViewById(R.id.topic_spinnerDaily);

            TextView repeatDay = dialogView.findViewById(R.id.repeatDay);
            repeatDay.setText(getDayName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
//            TextView repeatDay = dialogView.findViewById(R.id.repeatDay);
            TextView monday = dialogView.findViewById(R.id.monday);
            TextView tuesday = dialogView.findViewById(R.id.tuesday);
            TextView wednesday = dialogView.findViewById(R.id.wednesday);
            TextView thursday = dialogView.findViewById(R.id.thursday);
            TextView friday = dialogView.findViewById(R.id.friday);
            TextView saturday = dialogView.findViewById(R.id.saturday);
            TextView sunday = dialogView.findViewById(R.id.sunday);

            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // Set click listeners for each day
            setDayClickListener(monday, "M", "Monday", dayOfWeek == Calendar.MONDAY);
            setDayClickListener(tuesday, "T", "Tuesday", dayOfWeek == Calendar.TUESDAY);
            setDayClickListener(wednesday, "W", "Wednesday", dayOfWeek == Calendar.WEDNESDAY);
            setDayClickListener(thursday, "T", "Thursday", dayOfWeek == Calendar.THURSDAY);
            setDayClickListener(friday, "F", "Friday", dayOfWeek == Calendar.FRIDAY);
            setDayClickListener(saturday, "S", "Saturday", dayOfWeek == Calendar.SATURDAY);
            setDayClickListener(sunday, "S", "Sunday", dayOfWeek == Calendar.SUNDAY);

            dialog = builder.create();
            dialog.show();

            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePickerDialog();
                }
            });

            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePickerDialogEndDate();
                }
            });
            repeatDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        // "Off" is selected, uncheck "On"
                        repeatSpecificDay.setChecked(false);
                        Log.d("checked", "Daily button is checked");
//                        repeatDay.setVisibility(View.GONE); // Hide the TextView
                    } else {
                        // "Off" is unchecked, you can handle this case if needed
                    }
                }
            });

            repeatSpecificDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        // "On" is selected, uncheck "Off"
                        repeatDaily.setChecked(false);
                        Log.d("checked", "Specific button is checked");
//                        repeatDay.setVisibility(View.VISIBLE); // Show the TextView
                    } else {
                        // "On" is unchecked, you can handle this case if needed
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            arrayAdapter_subjects = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, subjectNameList);
            subject.setAdapter(arrayAdapter_subjects);

            subject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedSubject = (String) adapterView.getItemAtPosition(position);

                    // Log the selected subject
                    Log.d("Selected Subject", selectedSubject);

                    SubjectModel selectedSubjectModel = findSubjectByName(selectedSubject);

                    // Update the selectedSubjectIID with the ID of the selected subject
                    if (selectedSubjectModel != null) {
                        selectedSubjectIID = selectedSubjectModel.getId();

                        // Fetch topics based on the selected subject
                        topicList = selectedSubjectModel.getTopics();
                        topicNameList.clear();

                        for (TopicModel topic : topicList) {
                            topicNameList.add(topic.getTopicName());
                        }

                        // Notify the adapter about the data change
                        arrayAdapter_topics.notifyDataSetChanged();
                    }
                }
            });
            subject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subject.showDropDown();
                }
            });
            subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        subject.showDropDown();
                    }
                }
            });

            arrayAdapter_topics = new ArrayAdapter<>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, topicNameList);
            topic.setAdapter(arrayAdapter_topics);

            topic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedTopic = (String) adapterView.getItemAtPosition(position);

                    // Log the selected topic
                    Log.d("Selected topic", selectedTopic);
                }
            });
            topic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topic.showDropDown();
                }
            });
            topic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        topic.showDropDown();
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkValidation();
                }
            });

        }
    }

    private void setDayClickListener(TextView textView, String abbreviatedDay, String day, boolean isSelected) {
        textView.setText(abbreviatedDay);

        Log.d("Current State", "Day: " + day + ", isSelected: " + isSelected);
        if (isSelected) {
            // Set the selected state
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            textView.setTextColor(Color.WHITE);

            // Store the selected day in the global variable
            selectedDay = day;

            Log.d("select", selectedDay);

            // Update the repeatDay TextView

        } else {
            // Set the unselected state
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            textView.setTextColor(Color.GRAY);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the selection for the previously selected day
                resetDayColors();

                // Update the UI for the clicked day
                textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                textView.setTextColor(Color.WHITE);

                selectedDay = day;

                // Update the repeatDay TextView
                TextView repeatDay = dialog.findViewById(R.id.repeatDay);
                repeatDay.setText(selectedDay);

                Log.d("Selected Day", selectedDay);

                // Update the selectedDayTextView reference
//                selectedDayTextView = textView;
            }
        });
    }

    private void resetDayColors() {
        TextView monday = dialog.findViewById(R.id.monday);
        TextView tuesday = dialog.findViewById(R.id.tuesday);
        TextView wednesday = dialog.findViewById(R.id.wednesday);
        TextView thursday = dialog.findViewById(R.id.thursday);
        TextView friday = dialog.findViewById(R.id.friday);
        TextView saturday = dialog.findViewById(R.id.saturday);
        TextView sunday = dialog.findViewById(R.id.sunday);

        // Reset all days to default colors
        resetDayColor(monday);
        resetDayColor(tuesday);
        resetDayColor(wednesday);
        resetDayColor(thursday);
        resetDayColor(friday);
        resetDayColor(saturday);
        resetDayColor(sunday);
    }

    private void resetDayColor(TextView dayView) {
        dayView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        dayView.setTextColor(Color.parseColor("#858494"));
    }

    private String getDayName(int dayOfWeek) {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek - 1];
    }

    private void setMonthClickListener(TextView textView, String abbreviatedMonth, String month, boolean isSelected) {
        textView.setText(abbreviatedMonth);

        Log.d("Current State", "Day: " + month + ", isSelected: " + isSelected);
        if (isSelected) {
            // Set the selected state
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            textView.setTextColor(Color.WHITE);

            // Store the selected day in the global variable
            selectedDay = month;

            Log.d("select", selectedDay);

            // Update the repeatDay TextView

        } else {
            // Set the unselected state
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            textView.setTextColor(Color.GRAY);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the selection for the previously selected day
                resetMonthColors();

                // Update the UI for the clicked day
                textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                textView.setTextColor(Color.WHITE);

                selectedDay = month;

                // Update the repeatDay TextView
                TextView repeatMonth = dialog.findViewById(R.id.repeatMonth);
                repeatMonth.setText(selectedDay);

                Log.d("Selected Day", selectedDay);

                // Update the selectedDayTextView reference
//                selectedDayTextView = textView;
            }
        });
    }

    private void resetMonthColors() {
        TextView january = dialog.findViewById(R.id.jan);
        TextView february = dialog.findViewById(R.id.feb);
        TextView march = dialog.findViewById(R.id.mar);
        TextView april = dialog.findViewById(R.id.apr);
        TextView may = dialog.findViewById(R.id.may);
        TextView june = dialog.findViewById(R.id.jun);
        TextView july = dialog.findViewById(R.id.jul);
        TextView august = dialog.findViewById(R.id.aug);
        TextView september = dialog.findViewById(R.id.sep);
        TextView october = dialog.findViewById(R.id.oct);
        TextView november = dialog.findViewById(R.id.nov);
        TextView december = dialog.findViewById(R.id.dec);

        // Reset all days to default colors
        resetMonthColor(january);
        resetMonthColor(february);
        resetMonthColor(march);
        resetMonthColor(april);
        resetMonthColor(may);
        resetMonthColor(june);
        resetMonthColor(july);
        resetMonthColor(august);
        resetMonthColor(september);
        resetMonthColor(october);
        resetMonthColor(november);
        resetMonthColor(december);
    }

    private void resetMonthColor(TextView dayView) {
        dayView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        dayView.setTextColor(Color.parseColor("#858494"));
    }

    private String getMonthName(int Month) {
        String[] days = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return days[Month];
    }

    private void setWeekClickListener(TextView textView, String abbreviatedWeek, String week, boolean isSelected) {
        textView.setText(abbreviatedWeek);

        Log.d("Current State", "Day: " + week + ", isSelected: " + isSelected);
        if (isSelected) {
            // Set the selected state
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            textView.setTextColor(Color.WHITE);

            // Store the selected day in the global variable
            selectedDay = week;

            Log.d("select", selectedDay);

            // Update the repeatDay TextView

        } else {
            // Set the unselected state
            textView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            textView.setTextColor(Color.GRAY);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the selection for the previously selected day
                resetWeekColors();

                // Update the UI for the clicked day
                textView.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                textView.setTextColor(Color.WHITE);

                selectedDay = week;

                // Update the repeatDay TextView
                TextView repeatMonth = dialog.findViewById(R.id.repeatWeek);
                repeatMonth.setText(selectedDay);

                Log.d("Selected Day", selectedDay);

                // Update the selectedDayTextView reference
//                selectedDayTextView = textView;
            }
        });
    }

    private void resetWeekColors() {
        TextView week1 = dialog.findViewById(R.id.week1);
        TextView week2 = dialog.findViewById(R.id.week2);
        TextView week3 = dialog.findViewById(R.id.week3);
        TextView week4 = dialog.findViewById(R.id.week4);

        // Reset all days to default colors
        resetWeekColor(week1);
        resetWeekColor(week2);
        resetWeekColor(week3);
        resetWeekColor(week4);
    }

    private void resetWeekColor(TextView dayView) {
        dayView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        dayView.setTextColor(Color.parseColor("#858494"));
    }

    private String getWeekName(int Week) {
        String[] Weeks = {"Week 1", "Week 2", "Week 3", "Week 4"};
        return Weeks[Week - 1];
    }
//    //

    public void openDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = dialog.findViewById(R.id.EdtStartDate);
                        tvStartDate.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        cal.add(Calendar.MONTH, 0);// subtract 2 years from now
        cal.add(Calendar.DATE, 0);// subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 12);// add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    public void openDatePickerDialogEndDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = dialog.findViewById(R.id.EdtEndDate);
                        tvStartDate.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        cal.add(Calendar.MONTH, 0);// subtract 2 years from now
        cal.add(Calendar.DATE, 0);// subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 12);// add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    public void openDatePickerDialogMonthStartDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = dialog.findViewById(R.id.EdtMonthStartDate);
                        tvStartDate.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        cal.add(Calendar.MONTH, 0);// subtract 2 years from now
        cal.add(Calendar.DATE, 0);// subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);// add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    public void openDatePickerDialogMonthEndDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = dialog.findViewById(R.id.EdtMonthEndDate);
                        tvStartDate.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        cal.add(Calendar.MONTH, 1);// subtract 2 years from now
        cal.add(Calendar.DATE, 0);// subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 11);// add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    public void openDatePickerDialogWeekStartDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = dialog.findViewById(R.id.EdtStartWeekDate);
                        tvStartDate.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        cal.add(Calendar.MONTH, 0);// subtract 2 years from now
        cal.add(Calendar.DATE, 0);// subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);// add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    public void openDatePickerDialogWeekEndDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = dialog.findViewById(R.id.EdtEndWeekDate);
                        tvStartDate.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        cal.add(Calendar.DAY_OF_MONTH, 7);// subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);// add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    //RECYCLER DATA
    private List<RankLevelsModel> getList() {
        List<RankLevelsModel> rankLevelsModels = new ArrayList<>();
//        rankLevelsModels.add(new RankLevelsModel("Group", 11, 25, 88.56, "#88E2CE"));
//        rankLevelsModels.add(new RankLevelsModel("Section", 32, 60, 89.44, "#9087E5"));
        rankLevelsModels.add(new RankLevelsModel("Class 10", "" + dashboardModel.getUserClassWiseRanking(), "" + dashboardModel.getTotalClassCount(), 78.99, "#FF8FA2"));
        rankLevelsModels.add(new RankLevelsModel("State", "" + dashboardModel.getUserStateWiseRanking(), "" + dashboardModel.getTotalStateCount(), 75.77, "#7491F6"));
        rankLevelsModels.add(new RankLevelsModel("India", "" + dashboardModel.getUserCountryWiseRanking(), "" + dashboardModel.getTotalCountryCount(), 98.79, "#FFD54B"));

        return rankLevelsModels;
    }

    // RECYCLER VIEW
    private void setUpRecyclerView() {
        List<RankLevelsModel> data = getList();
        RecyclerAdapterRanks adapter = new RecyclerAdapterRanks(data);

        layoutManager = new GridLayoutManager(getContext(), 2);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);
    }

    private void getUser() {

        //imgProfile.setImageUrl(dataSharedPref.getSession("username"), imageLoader);
        //String greet = "Welcome back " + dataSharedPref.getSession("firstname") + "!";
        //tvName.setText(greet);
        getDashboardData();
    }

    private void addDailyTarget() {
        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Wait...", true);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        TargetModel model = new TargetModel();

        // Search for the data
        EditText etStartDateDaily = dialog.findViewById(R.id.EdtStartDate);
        EditText etEndDateDaily = dialog.findViewById(R.id.EdtEndDate);
        EditText etTargetNameDaily = dialog.findViewById(R.id.setTarget);
        EditText etTestCountDaily = dialog.findViewById(R.id.testCountDaily);
        String startDateTextDaily = etStartDateDaily.getText().toString();
        String endDateTextDaily = etEndDateDaily.getText().toString();
        String targetNameTextDaily = etTargetNameDaily.getText().toString();
        Integer testCountTextDaily;
        String testCountInput = etTestCountDaily.getText().toString();
        if (!testCountInput.isEmpty()) {
            try {
                testCountTextDaily = Integer.valueOf(testCountInput);
            } catch (NumberFormatException e) {
                // Handle the case where the input is not a valid integer
                // For example, set a default value or show an error message
                testCountTextDaily = null;
                // or show an error message
                // showToast("Invalid input. Please enter a valid number.");
            }
        } else {
            // Handle the case where the input is empty
            // For example, set a default value or show an error message
            testCountTextDaily = null;
            // or show an error message
            // showToast("Input cannot be empty. Please enter a number.");
        }

        // Logging to check if everything is being set
        Log.d("MyApp", "StartDate in addTarget: " + startDateTextDaily);
        Log.d("MyApp", "EndDate in addTarget: " + endDateTextDaily);
        Log.d("MyApp", "TargetName in addTarget: " + targetNameTextDaily);
        Log.d("MyApp", "TestCount is: " + testCountTextDaily);


        // Set Data to the Model
        model.setUserId(userDetails.getUserId());
        model.setStartDate(startDateTextDaily);
        model.setEndDate(endDateTextDaily);
        model.setTargetName(targetNameTextDaily);
        model.setTestCount(testCountTextDaily);

        // SUBJECTS
        AutoCompleteTextView subjectsAutoCompleteTextView = dialog.findViewById(R.id.subjects_spinnerDaily);
        String selectedSubject = subjectsAutoCompleteTextView.getText().toString().trim();
        int subjectPosition = subjectNameList.indexOf(selectedSubject);
        if (subjectPosition != -1) {
            Long subjectId = subjectsList.get(subjectPosition).getId();
            model.setSubjectId(subjectId);
            Log.d("MyApp", "subject " + String.valueOf(subjectId));

            // ... remaining code to send the request ...
        } else {
            Toast.makeText(getContext(), "Please select a valid Subject", Toast.LENGTH_SHORT).show();
        }

        // TOPICS
        AutoCompleteTextView topicAutoCompleteTextView = dialog.findViewById(R.id.topic_spinnerDaily);
        String selectedTopic = topicAutoCompleteTextView.getText().toString().trim();
        int topicPosition = topicNameList.indexOf(selectedTopic);
        if (topicPosition != -1) {
            Long topicId = topicList.get(topicPosition).getTopicId();
            model.setTopicId(topicId);
            Log.d("MyApp", "Topic " + String.valueOf(topicId));
            // ... remaining code to send the request ...
        } else {
            Toast.makeText(getContext(), "Please select a valid Topic", Toast.LENGTH_SHORT).show();
        }

        // REPEAT
        TextView repeatDay = dialog.findViewById(R.id.repeatDay);
        String repeatDayText = repeatDay.getText().toString().trim();
        SwitchCompat dailySwitch = dialog.findViewById(R.id.repeatDailySwitch);
        SwitchCompat specificDaySwitch = dialog.findViewById(R.id.repeatSpecificDaySwitch);
        if (dailySwitch != null && specificDaySwitch != null) {
            if (dailySwitch.isChecked()) {
                model.setRepeatingType(Constants.REPEATING_DAILY);
                Log.d("MyApp", "Switch " + Constants.REPEATING_DAILY);
            } else if (specificDaySwitch.isChecked()) {
                model.setRepeatingType(Constants.REPEATING_SPECIFIC_DAY);
                Log.d("MyApp", "Switch " + Constants.REPEATING_SPECIFIC_DAY);

                // Set model.setRepeatingDay based on repeatDayText
                if (repeatDayText == "Sunday") {
                    model.setRepeatingDay(0);
                    Log.d("MyApp", "Selected day: 0");
                } else if ("Monday".equals(repeatDayText)) {
                    model.setRepeatingDay(1);
                    Log.d("MyApp", "Selected day: 1");
                } else if ("Tuesday".equals(repeatDayText)) {
                    model.setRepeatingDay(2);
                    Log.d("MyApp", "Selected day: 2");
                } else if ("Wednesday".equals(repeatDayText)) {
                    model.setRepeatingDay(3);
                    Log.d("MyApp", "Selected day: 3");
                } else if ("Thursday".equals(repeatDayText)) {
                    model.setRepeatingDay(4);
                    Log.d("MyApp", "Selected day: 4");
                } else if ("Friday".equals(repeatDayText)) {
                    model.setRepeatingDay(5);
                    Log.d("MyApp", "Selected day: 5");
                } else if ("Saturday".equals(repeatDayText)) {
                    model.setRepeatingDay(6);
                    Log.d("MyApp", "Selected day: 6");
                } else {
                    // Handle the case when repeatDayText is not a valid day
                    Log.d("MyApp", "Invalid day: " + repeatDayText);
                }

            } else {
                model.setRepeatingType(null);
                model.setRepeatingDay(null);
            }
        } else {
            Log.e("Switches", "One or both switches not found in the layout");
        }
        model.setRepeatingMonth(null);
        model.setRepeatingWeek(null);

        System.out.println();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            Log.e("Target Model ", "Targets");
            System.out.println(data);
            System.out.println("url " + App.ADD_TARGETS_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.ADD_TARGETS_URL, new JSONObject(data),

                    response -> {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            Boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status) {
                                Toast.makeText(getContext(), " " + message + " OK", Toast.LENGTH_SHORT).show();
                            } else if (!status) {
                                Toast.makeText(getContext(), " " + message + " NOT OK", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                progressDialog.dismiss();
                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new android.app.AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                Log.e("error", String.valueOf(error));
                error.printStackTrace();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };
//            Log.e("login: ", "response");
//            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void addMonthlyTarget() {
        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Wait...", true);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        TargetModel model2 = new TargetModel();


        EditText etStartDateMonthly = dialog.findViewById(R.id.EdtMonthStartDate);
        EditText etEndDateMonthly = dialog.findViewById(R.id.EdtMonthEndDate);
        EditText etTargetNameMonthly = dialog.findViewById(R.id.setTargetMonth);
        EditText etTestCountMonthly = dialog.findViewById(R.id.testCountMonthly);
        String startDateTextMonthly = etStartDateMonthly.getText().toString();
        String endDateTextMonthly = etEndDateMonthly.getText().toString();
        String targetNameTextMonthly = etTargetNameMonthly.getText().toString();
        Integer testCountTextMonthly;
        String testCountInput = etTestCountMonthly.getText().toString();
        if (!testCountInput.isEmpty()) {
            try {
                testCountTextMonthly = Integer.valueOf(testCountInput);
            } catch (NumberFormatException e) {
                // Handle the case where the input is not a valid integer
                // For example, set a default value or show an error message
                testCountTextMonthly = null;
                // or show an error message
                // showToast("Invalid input. Please enter a valid number.");
            }
        } else {
            // Handle the case where the input is empty
            // For example, set a default value or show an error message
            testCountTextMonthly = null;
            // or show an error message
            // showToast("Input cannot be empty. Please enter a number.");
        }

        // Logging to check if everything is being set
        Log.d("MyApp", "StartDate in addTarget: " + startDateTextMonthly);
        Log.d("MyApp", "EndDate in addTarget: " + endDateTextMonthly);
        Log.d("MyApp", "TargetName in addTarget: " + targetNameTextMonthly);
        Log.d("MyApp", "TestCount is: " + testCountTextMonthly);


        // Set Data to the Model
        model2.setUserId(userDetails.getUserId());
        model2.setStartDate(startDateTextMonthly);
        model2.setEndDate(endDateTextMonthly);
        model2.setTargetName(targetNameTextMonthly);
        model2.setTestCount(testCountTextMonthly);
        Log.d("MyApp", "TestCount (after setting): " + model2.getTestCount());

        // SUBJECTS
        AutoCompleteTextView subjectsAutoCompleteTextViewMonthly = dialog.findViewById(R.id.subjects_spinnerMonthly);
        String selectedSubjectMonthly = subjectsAutoCompleteTextViewMonthly.getText().toString().trim();
        int subjectPositionMobthly = subjectNameList.indexOf(selectedSubjectMonthly);
        if (subjectPositionMobthly != -1) {
            Long subjectIdMonthly = subjectsList.get(subjectPositionMobthly).getId();
            model2.setSubjectId(subjectIdMonthly);
            Log.d("MyApp", "subject " + String.valueOf(subjectIdMonthly));

            // ... remaining code to send the request ...
        } else {
            Toast.makeText(getContext(), "Please select a valid Subject", Toast.LENGTH_SHORT).show();
        }

        // TOPICS
        AutoCompleteTextView topicAutoCompleteTextViewMonthly = dialog.findViewById(R.id.topic_spinnerMonthly);
        String selectedTopic = topicAutoCompleteTextViewMonthly.getText().toString().trim();
        int topicPositionMonthly = topicNameList.indexOf(selectedTopic);
        if (topicPositionMonthly != -1) {
            Long topicIdMonthly = topicList.get(topicPositionMonthly).getTopicId();
            model2.setTopicId(topicIdMonthly);
            Log.d("MyApp", "Topic " + String.valueOf(topicIdMonthly));
            // ... remaining code to send the request ...
        } else {
            Toast.makeText(getContext(), "Please select a valid Topic", Toast.LENGTH_SHORT).show();
        }

        // REPEAT
        TextView repeatDayMonthly = dialog.findViewById(R.id.repeatMonth);
        String repeatDayTextMonthly = repeatDayMonthly.getText().toString().trim();
        SwitchCompat MonthlySwitch = dialog.findViewById(R.id.repeatMonthlySwitch);
        SwitchCompat specificMonthSwitch = dialog.findViewById(R.id.repeatSpecificMonthSwitch);
        if (MonthlySwitch != null && specificMonthSwitch != null) {
            if (MonthlySwitch.isChecked()) {
                model2.setRepeatingType(Constants.REPEATING_ALL_MONTHS);
                Log.d("MyApp", "Switch " + Constants.REPEATING_ALL_MONTHS);
            } else if (specificMonthSwitch.isChecked()) {
                model2.setRepeatingType(Constants.REPEATING_SPECIFIC_MONTH);
                Log.d("MyApp", "Switch " + Constants.REPEATING_SPECIFIC_MONTH);

                // Set model.setRepeatingDay based on repeatDayText
                if (repeatDayTextMonthly == "January") {
                    model2.setRepeatingMonth(1);
                    Log.d("MyApp", "Selected Month: 1");
                } else if ("February".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(2);
                    Log.d("MyApp", "Selected Month: 2");
                } else if ("March".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(3);
                    Log.d("MyApp", "Selected Month: 3");
                } else if ("April".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(4);
                    Log.d("MyApp", "Selected Month: 4");
                } else if ("May".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(5);
                    Log.d("MyApp", "Selected Month: 5");
                } else if ("June".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(6);
                    Log.d("MyApp", "Selected Month: 6");
                } else if ("July".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(7);
                    Log.d("MyApp", "Selected Month: 7");
                } else if ("August".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(8);
                    Log.d("MyApp", "Selected Month: 8");
                } else if ("September".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(9);
                    Log.d("MyApp", "Selected Month: 9");
                } else if ("October".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(10);
                    Log.d("MyApp", "Selected Month: 10");
                } else if ("November".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(11);
                    Log.d("MyApp", "Selected Month: 11");
                } else if ("December".equals(repeatDayTextMonthly)) {
                    model2.setRepeatingMonth(12);
                    Log.d("MyApp", "Selected Month: 12");
                } else {
                    // Handle the case when repeatDayText is not a valid day
                    Log.d("MyApp", "Invalid day: " + repeatDayTextMonthly);
                }

            } else {
                model2.setRepeatingType(null);
                model2.setRepeatingMonth(null);
            }
        } else {
            Log.e("Switches", "One or both switches not found in the layout");
        }
        model2.setRepeatingDay(null);
        model2.setRepeatingWeek(null);

        System.out.println();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model2);
            System.out.println(data);
            Log.e("Target Model ", "Targets");
            System.out.println(data);
            System.out.println("url " + App.ADD_TARGETS_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.ADD_TARGETS_URL, new JSONObject(data),

                    response -> {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            Boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status) {
                                Toast.makeText(getContext(), " " + message + " OK", Toast.LENGTH_SHORT).show();
                            } else if (!status) {
                                Toast.makeText(getContext(), " " + message + " NOT OK", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                progressDialog.dismiss();
                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new android.app.AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                Log.e("error", String.valueOf(error));
                error.printStackTrace();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };
//            Log.e("login: ", "response");
//            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void addWeeklyTarget() {
        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Wait...", true);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        TargetModel model3 = new TargetModel();


        EditText etStartDateWeekly = dialog.findViewById(R.id.EdtStartWeekDate);
        EditText etEndDateWeekly = dialog.findViewById(R.id.EdtEndWeekDate);
        EditText etTargetNameWeekly = dialog.findViewById(R.id.setTargetWeekly);
        EditText etTestCountWeekly = dialog.findViewById(R.id.testCountWeekly);
        String startDateTextWeekly = etStartDateWeekly.getText().toString();
        String endDateTextWeekly = etEndDateWeekly.getText().toString();
        String targetNameTextWeekly = etTargetNameWeekly.getText().toString();
        Integer testCountTextWeekly;
        String testCountInput = etTestCountWeekly.getText().toString();
        if (!testCountInput.isEmpty()) {
            try {
                testCountTextWeekly = Integer.valueOf(testCountInput);
            } catch (NumberFormatException e) {
                // Handle the case where the input is not a valid integer
                // For example, set a default value or show an error message
                testCountTextWeekly = null;
                // or show an error message
                // showToast("Invalid input. Please enter a valid number.");
            }
        } else {
            // Handle the case where the input is empty
            // For example, set a default value or show an error message
            testCountTextWeekly = null;
            // or show an error message
            // showToast("Input cannot be empty. Please enter a number.");
        }

        // Logging to check if everything is being set
        Log.d("MyApp", "StartDate in addTarget: " + startDateTextWeekly);
        Log.d("MyApp", "EndDate in addTarget: " + endDateTextWeekly);
        Log.d("MyApp", "TargetName in addTarget: " + targetNameTextWeekly);
        Log.d("MyApp", "TestCount is: " + testCountTextWeekly);


        // Set Data to the Model
        model3.setUserId(userDetails.getUserId());
        model3.setStartDate(startDateTextWeekly);
        model3.setEndDate(endDateTextWeekly);
        model3.setTargetName(targetNameTextWeekly);
        model3.setTestCount(testCountTextWeekly);
        Log.d("MyApp", "TestCount (after setting): " + model3.getTestCount());

        // SUBJECTS
        AutoCompleteTextView subjectsAutoCompleteTextViewWeekly = dialog.findViewById(R.id.subjects_spinnerWeekly);
        String selectedSubjectWeekly = subjectsAutoCompleteTextViewWeekly.getText().toString().trim();
        int subjectPositionWeekly = subjectNameList.indexOf(selectedSubjectWeekly);
        if (subjectPositionWeekly != -1) {
            Long subjectIdWeekly = subjectsList.get(subjectPositionWeekly).getId();
            model3.setSubjectId(subjectIdWeekly);
            Log.d("MyApp", "subject " + String.valueOf(subjectIdWeekly));

            // ... remaining code to send the request ...
        } else {
            Toast.makeText(getContext(), "Please select a valid Subject", Toast.LENGTH_SHORT).show();
        }

        // TOPICS
        AutoCompleteTextView topicAutoCompleteTextViewWeekly = dialog.findViewById(R.id.topic_spinnerWeekly);
        String selectedTopicWeekly = topicAutoCompleteTextViewWeekly.getText().toString().trim();
        int topicPositionWeekly = topicNameList.indexOf(selectedTopicWeekly);
        if (topicPositionWeekly != -1) {
            Long topicIdMonthly = topicList.get(topicPositionWeekly).getTopicId();
            model3.setTopicId(topicIdMonthly);
            Log.d("MyApp", "Topic " + String.valueOf(topicIdMonthly));
            // ... remaining code to send the request ...
        } else {
            Toast.makeText(getContext(), "Please select a valid Topic", Toast.LENGTH_SHORT).show();
        }

        // REPEAT
        TextView repeatDayWeekly = dialog.findViewById(R.id.repeatWeek);
        String repeatDayTextWeekly = repeatDayWeekly.getText().toString().trim();
        SwitchCompat WeeklySwitch = dialog.findViewById(R.id.repeatWeeklySwitch);
        SwitchCompat specificWeekSwitch = dialog.findViewById(R.id.repeatSpecificWeekSwitch);
        if (WeeklySwitch != null && specificWeekSwitch != null) {
            if (WeeklySwitch.isChecked()) {
                model3.setRepeatingType(Constants.REPEATING_ALL_WEEKS);
                Log.d("MyApp", "Switch " + Constants.REPEATING_ALL_WEEKS);
            } else if (specificWeekSwitch.isChecked()) {
                model3.setRepeatingType(Constants.REPEATING_SPECIFIC_WEEK);
                Log.d("MyApp", "Switch " + Constants.REPEATING_SPECIFIC_WEEK);

                // Set model.setRepeatingDay based on repeatDayText
                if ("Week 1".equals(repeatDayTextWeekly)) {
                    model3.setRepeatingWeek(1);
                    Log.d("MyApp", "Selected Week: 1");
                } else if ("Week 2".equals(repeatDayTextWeekly)) {
                    model3.setRepeatingWeek(2);
                    Log.d("Week 3", "Selected Week: 2");
                } else if ("March".equals(repeatDayTextWeekly)) {
                    model3.setRepeatingWeek(3);
                    Log.d("Week 4", "Selected Week: 3");
                } else if ("April".equals(repeatDayTextWeekly)) {
                    model3.setRepeatingWeek(4);
                    Log.d("MyApp", "Selected Week: 4");
                } else {
                    // Handle the case when repeatDayText is not a valid day
                    Log.d("MyApp", "Invalid Week: " + repeatDayTextWeekly);
                }

            } else {
                model3.setRepeatingType(null);
                model3.setRepeatingWeek(null);
            }
        } else {
            Log.e("Switches", "One or both switches not found in the layout");
        }
        model3.setRepeatingDay(null);
        model3.setRepeatingMonth(null);

        System.out.println();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model3);
            System.out.println(data);
            Log.e("Target Model ", "Targets");
            System.out.println(data);
            System.out.println("url " + App.ADD_TARGETS_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.ADD_TARGETS_URL, new JSONObject(data),

                    response -> {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            Boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status) {
                                Toast.makeText(getContext(), " " + message + " OK", Toast.LENGTH_SHORT).show();
                            } else if (!status) {
                                Toast.makeText(getContext(), " " + message + " NOT OK", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                progressDialog.dismiss();
                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new android.app.AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                Log.e("error", String.valueOf(error));
                error.printStackTrace();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };
//            Log.e("login: ", "response");
//            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void getDashboardData() {
        getDashboardDataDialog = Util.getProgressDialog(getContext(), "Loading Data");

        RequestQueue queue = Volley.newRequestQueue(getContext());
        HashMap model = new HashMap();
        model.put("subjectId", selectedSubjectIIDDashboard);
        model.put("userId", userDetails.getUserId());
        model.put("classId", userDetails.getUserClass());
        ObjectMapper mapper = new ObjectMapper();
        try {
            String data = mapper.writeValueAsString(model);

            System.out.println("url " + App.DASHBOARD_URL + "-- " + data);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.DASHBOARD_URL, new JSONObject(data),

                    response -> {
                        getDashboardDataDialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");
                            String jsondata = obj.getString("data");
                            Gson gson = new Gson();
                            if (status.equals("true")) {

                                dashboardModel = gson.fromJson(jsondata, DashboardModel.class);
                                Util.logD("dashboard: " + dashboardModel.getTotalScored());
                                scores_text.setText("" + dashboardModel.getTotalScored());
                                setUpRecyclerView();
                                recentTest = dashboardModel.getRecentTest();
                                if (recentTest != null) {
                                    noData.setVisibility(View.GONE);
                                    noDataAnim.setVisibility(View.GONE);
                                    recentQuizlayout.setVisibility(View.VISIBLE);
                                    recycler.setVisibility(View.VISIBLE);

                                    lessonForQuiz.setText("" + findSubjectByID(recentTest.getSubjectId()));
                                    topicForQuiz.setText("" + findTopicByID(recentTest.getTopicId()));
                                    setPieChart(recentTest.getTotalCorrectAnswers(), recentTest.getTotalWrongAnswers());
                                } else {
                                    recentQuizlayout.setVisibility(View.GONE);
                                }

//                                ArrayList<StateModel> stateModels = new ArrayList<>();
//                                JSONArray statesJsonArray = new JSONArray(obj.getString("data"));
//
//                                for (int i = 0; i < statesJsonArray.length(); i++) {
//                                    JSONObject state = statesJsonArray.getJSONObject(i);
//
//                                    StateModel model = new StateModel();
//                                    model.setId(state.getInt("id"));
//                                    model.setStateCode(state.getString("code"));
//                                    model.setStateName(state.getString("state"));
//
//                                    stateModels.add(model);
//                                }
//
//                                dataSource.states.addStates(stateModels);

                            } else {
//                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                noData.setVisibility(View.VISIBLE);
                                noDataAnim.setVisibility(View.VISIBLE);
                                recycler.setVisibility(View.GONE);
                                scores_text.setText("-");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                getDashboardDataDialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new android.app.AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };
//            Log.e("Subjects: ", "response");
//            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkValidation() {
        EditText setTarget = dialog.findViewById(R.id.setTarget);
        EditText setDate = dialog.findViewById(R.id.EdtStartDate);
        EditText setEndDate = dialog.findViewById(R.id.EdtEndDate);
        EditText setSubject = dialog.findViewById(R.id.subjects_spinnerDaily);

        String setTargetText = setTarget.getEditableText().toString().trim();
        String setDateText = setDate.getEditableText().toString().trim();
        String setEndDatetText = setEndDate.getEditableText().toString().trim();
        String setSubjectText = setSubject.getEditableText().toString().trim();

        if (TextUtils.isEmpty(setTargetText) || TextUtils.isEmpty(setDateText) || TextUtils.isEmpty(setEndDatetText) || TextUtils.isEmpty(setSubjectText)) {
            Toast.makeText(getContext(), "Set the Target Details Please", Toast.LENGTH_SHORT).show();
        } else {
            addDailyTarget();
        }
    }

    private void checkValidationWeely() {
        EditText setTarget = dialog.findViewById(R.id.setTargetWeekly);
        EditText setDate = dialog.findViewById(R.id.EdtStartWeekDate);
        EditText setEndDate = dialog.findViewById(R.id.EdtEndWeekDate);
        EditText setSubject = dialog.findViewById(R.id.subjects_spinnerWeekly);

        String setTargetText = setTarget.getEditableText().toString().trim();
        String setDateText = setDate.getEditableText().toString().trim();
        String setEndDatetText = setEndDate.getEditableText().toString().trim();
        String setSubjectText = setSubject.getEditableText().toString().trim();

        if (TextUtils.isEmpty(setTargetText) || TextUtils.isEmpty(setDateText) || TextUtils.isEmpty(setEndDatetText) || TextUtils.isEmpty(setSubjectText)) {
            Toast.makeText(getContext(), "Set the Target Details Please", Toast.LENGTH_SHORT).show();
        } else {
            addWeeklyTarget();
        }
    }

    private void checkValidationMonthly() {
        EditText setTarget = dialog.findViewById(R.id.setTargetMonth);
        EditText setDate = dialog.findViewById(R.id.EdtMonthStartDate);
        EditText setEndDate = dialog.findViewById(R.id.EdtMonthEndDate);
        EditText setSubject = dialog.findViewById(R.id.subjects_spinnerMonthly);

        String setTargetText = setTarget.getEditableText().toString().trim();
        String setDateText = setDate.getEditableText().toString().trim();
        String setEndDatetText = setEndDate.getEditableText().toString().trim();
        String setSubjectText = setSubject.getEditableText().toString().trim();

        if (TextUtils.isEmpty(setTargetText) || TextUtils.isEmpty(setDateText) || TextUtils.isEmpty(setEndDatetText) || TextUtils.isEmpty(setSubjectText)) {
            Toast.makeText(getContext(), "Set the Target Details Please", Toast.LENGTH_SHORT).show();
        } else {
            addMonthlyTarget();
        }
    }
}