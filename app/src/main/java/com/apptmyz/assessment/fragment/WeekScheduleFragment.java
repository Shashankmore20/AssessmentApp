package com.apptmyz.assessment.fragment;

import static com.apptmyz.assessment.utils.CalendarUtils.daysInMonthArray;
import static com.apptmyz.assessment.utils.CalendarUtils.daysInWeekArray;
import static com.apptmyz.assessment.utils.CalendarUtils.monthYearFromDate;
import static com.apptmyz.assessment.utils.CalendarUtils.selectedDate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.CalendarAdapter;
import com.apptmyz.assessment.activity.LoginActivity;
import com.apptmyz.assessment.activity.RefreshableFragment;
import com.apptmyz.assessment.activity.ScheduleCallback;
import com.apptmyz.assessment.adapter.ScheduleAdapter;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.ScheduleHelper;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.DashboardModel;
import com.apptmyz.assessment.model.ScheduleModel;
import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.model.TargetModel;
import com.apptmyz.assessment.model.TestScheduleModel;
import com.apptmyz.assessment.model.TopicModel;
import com.apptmyz.assessment.utils.CalendarUtils;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeekScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener, RefreshableFragment, ScheduleCallback {

    private static LocalDate dateSelect;
    RelativeLayout addTaskLayout;

    ConstraintLayout calendarLayout;
    RecyclerView dayScheduleList;

    ArrayList<SubjectModel> subjectsList;
    ArrayList<String> subjectNameList;
    EditText date, time;
    UserDetails userDetails;
    ImageView imgDate, imgTime;
    ArrayList<TopicModel> topicList;
    ArrayList<String> topicNameList;
    private DataSource dataSource;
    AutoCompleteTextView subjects;

    TextView NoDataAvailable;
    long selectedSubjectIID = 0;
    long selectedSubjectIIDDashboard = 0;
    private ArrayAdapter<String> arrayAdapter_topics;
    private ArrayAdapter<String> arrayAdapter_subjects;
    private DashboardModel dashboardModel;
    FloatingActionButton fab;
    private TextView monthYearText;
    private RecyclerView weekRecyclerView, monthRecyclerView;
    private ListView eventListView;
    RecyclerView weekScheduleList;
    ImageButton forwardBtn, backBtn;
    Button saveTaskBtn, closeDialogBtn;

    TextView titleTv, descTv;

    TextView fromtimeTv, toTimeTv, taskDateTv, taskDayTv;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private Switch weekSwitch;

    ScheduleHelper scheduleHelper;

    WeekScheduleAdapter weekScheduleAdapter;

    ArrayList<ScheduleModel> scheduleModels;

    Long userId = Long.valueOf(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_schedule, container, false);
        CalendarUtils.selectedDate = LocalDate.now();
        dateSelect = LocalDate.now();

        scheduleHelper = new ScheduleHelper(getContext());//TODO
        Log.e( "onCreateView: ", "created");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.backBtn:
                        if(weekSwitch.isChecked())
                            previousWeekAction(view);
                        else
                            previousMonthAction(view);
                        break;
                    case R.id.forwardBtn:
                        if(weekSwitch.isChecked())
                            nextWeekAction(view);
                        else
                            nextMonthAction(view);
                        break;
                    case R.id.fab:
                        openNewTaskDialog();
                        break;
                    case R.id.closeDialogBtn:
                        closeAddTaskDialog();
                        break;
                    case R.id.fromTimeTv:
                        openTimePicker(view);
                        break;
                    case R.id.toTimeTv:
                        openTimePicker(view);
                        break;
                    case R.id.taskDateTv:
                        openDatePicker();
                        break;
                    case R.id.saveTaskBtn:
                        checkValidation();
                    default:
                        break;
                }
            }
        };

        dayScheduleList = view.findViewById(R.id.dayScheduleList);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(listener);
        weekRecyclerView = view.findViewById(R.id.weekRecyclerView);
        weekRecyclerView.setVisibility(View.GONE);
        monthRecyclerView = view.findViewById(R.id.monthRecyclerView);
        monthRecyclerView.setVisibility(View.VISIBLE);

        date = view.findViewById(R.id.EdtStartDatee);
        time = view.findViewById(R.id.EdtTime);
        imgDate = view.findViewById(R.id.startDateCale);
        imgTime = view.findViewById(R.id.timeIcon);

        weekSwitch = view.findViewById(R.id.switchBtn);

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog();
            }
        });
        weekSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weekRecyclerView.setVisibility(View.VISIBLE);
                    monthRecyclerView.setVisibility(View.GONE);
                } else {
                    weekRecyclerView.setVisibility(View.GONE);
                    monthRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        calendarLayout = view.findViewById(R.id.calendarLayout);

        monthYearText = view.findViewById(R.id.monthYearTV);
        NoDataAvailable = view.findViewById(R.id.NoDataAvailable);
        //eventListView = view.findViewById(R.id.eventListView);

        //scheduleModels = scheduleHelper.getDaySchedule(CalendarUtils.selectedDate);
        scheduleModels = scheduleHelper.getDaySchedule(dateSelect);

        Log.e("onCreateView: ", "Data");
        System.out.println(scheduleModels);
        weekScheduleList = view.findViewById(R.id.weekScheduleList);
        weekScheduleAdapter = new WeekScheduleAdapter(getContext(), scheduleModels );//TODO
        weekScheduleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        weekScheduleList.setAdapter(weekScheduleAdapter);
        weekScheduleAdapter.notifyDataSetChanged();

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(listener);
        forwardBtn = view.findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(listener);

        addTaskLayout = view.findViewById(R.id.addTaskLayout);
        addTaskLayout.setVisibility(View.GONE);
        closeDialogBtn = (Button) view.findViewById(R.id.closeDialogBtn);
        closeDialogBtn.setOnClickListener(listener);

        fromtimeTv = view.findViewById(R.id.fromTimeTv);
        fromtimeTv.setText(CalendarUtils.formattedTime(LocalTime.now()));
        fromtimeTv.setOnClickListener(listener);
        toTimeTv = view.findViewById(R.id.toTimeTv);
        toTimeTv.setText(CalendarUtils.formattedTime(LocalTime.now()));
        toTimeTv.setOnClickListener(listener);

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

        taskDateTv = view.findViewById(R.id.taskDateTv);
        taskDayTv = view.findViewById(R.id.taskDayTv);
        taskDayTv.setText("(" + LocalDate.now().getDayOfWeek().toString() + ")");

        int d = LocalDate.now().getDayOfMonth();
        int m = LocalDate.now().getMonthValue();
        int y = LocalDate.now().getYear();

        taskDateTv.setText(d + "-" + m + "-" + y);
        taskDateTv.setOnClickListener(listener);

        titleTv = view.findViewById(R.id.titleTv);
        descTv = view.findViewById(R.id.descTv);

        saveTaskBtn = view.findViewById(R.id.saveTaskBtn);
        saveTaskBtn.setOnClickListener(listener);

        AutoCompleteTextView subject = view.findViewById(R.id.subjects_spinnerDaily);
        AutoCompleteTextView topic = view.findViewById(R.id.topic_spinnerDaily);

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

        dayScheduleList = view.findViewById(R.id.dayScheduleList);
        dayScheduleList.setLayoutManager(new LinearLayoutManager(getContext()));

        userId = userDetails.getUserId();


        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDateOfMonth = calendar.getTime();
        String firstDateOfMonthString = dateFormat.format(firstDateOfMonth);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDateOfMonth = calendar.getTime();
        String lastDateOfMonthString = dateFormat.format(lastDateOfMonth);

        System.out.println("First Date of the Month: " + firstDateOfMonthString);
        System.out.println("Last Date of the Month: " + lastDateOfMonthString);

        getSchedule(getContext(), userId, firstDateOfMonthString,lastDateOfMonthString, new ScheduleCallback() {
            @Override
            public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {

                DataSource dataSource = new DataSource(getContext());
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
                dayScheduleList.setAdapter(adapter);
            }
        });

        setWeekView();
        setMonthView();

        return view;
    }

    private void setMonthView() {
        Log.e("check setMonthView: ", CalendarUtils.selectedDate.toString());
//        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
//        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        monthYearText.setText(monthYearFromDate(dateSelect));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(dateSelect);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        monthRecyclerView.setLayoutManager(layoutManager);
        monthRecyclerView.setAdapter(calendarAdapter);
    }

    private void setWeekView() {
        Log.e("check setWeekView: ", selectedDate.toString());
//        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
//        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        monthYearText.setText(monthYearFromDate(dateSelect));
        ArrayList<LocalDate> days = daysInWeekArray(dateSelect);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        weekRecyclerView.setLayoutManager(layoutManager);
        weekRecyclerView.setAdapter(calendarAdapter);
    }

    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        dateSelect = dateSelect.minusMonths(1);
        setMonthView();
        refreshAdapters();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(dateSelect.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDateOfMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDateOfMonth = calendar.getTime();

        // Format the dates to the required format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String firstDateOfMonthString = dateFormat.format(firstDateOfMonth);
        String lastDateOfMonthString = dateFormat.format(lastDateOfMonth);

        System.out.println("First Date of the Month: " + firstDateOfMonthString);
        System.out.println("Last Date of the Month: " + lastDateOfMonthString);


        getSchedule(getContext(), userId, firstDateOfMonthString,lastDateOfMonthString, new ScheduleCallback() {
            @Override
            public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {

                DataSource dataSource = new DataSource(getContext());
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
                dayScheduleList.setAdapter(adapter);

            }
        });
    }

    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        dateSelect = dateSelect.plusMonths(1);
        setMonthView();
        refreshAdapters();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(dateSelect.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDateOfMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDateOfMonth = calendar.getTime();

        // Format the dates to the required format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String firstDateOfMonthString = dateFormat.format(firstDateOfMonth);
        String lastDateOfMonthString = dateFormat.format(lastDateOfMonth);

        System.out.println("First Date of the Month: " + firstDateOfMonthString);
        System.out.println("Last Date of the Month: " + lastDateOfMonthString);


        getSchedule(getContext(), userId, firstDateOfMonthString,lastDateOfMonthString, new ScheduleCallback() {
            @Override
            public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {

                DataSource dataSource = new DataSource(getContext());
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
                dayScheduleList.setAdapter(adapter);

            }
        });
    }

    private SubjectModel findSubjectByName(String subjectName) {
        for (SubjectModel subject : subjectsList) {
            if (subject.getSubjectName().equals(subjectName)) {
                return subject;
            }
        }
        return null;
    }

//    @Override
//    public void onItemClick(int position, LocalDate date) {
//        if(date != null)
//        {
//            CalendarUtils.selectedDate = date;
//            setMonthView();
//        }
//    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        dateSelect = date;

        //Log.e( "onItemClick: ", date.toString());
        if(weekSwitch.isChecked())
            setWeekView();
        else
            setMonthView();
        refreshAdapters();
    }

    private void saveNewTask(){
        Log.e( "saveNewTask: ", "test");
        String title = titleTv.getText().toString();
        String desc = descTv.getText().toString();

        String[] temp2 = taskDateTv.getText().toString().split("-");
        String taskDate = LocalDate.of(Integer.parseInt(temp2[2]), Integer.parseInt(temp2[1]), Integer.parseInt(temp2[0])).toString();

        String[] temp = fromtimeTv.getText().toString().split(" ");
        LocalTime fromTime = LocalTime.parse(temp[0]);

        String[] temp1 = toTimeTv.getText().toString().split(" ");
        LocalTime toTime = LocalTime.parse(temp1[0]);

        String assignedBy = "student";
        ScheduleModel scheduleModel = new ScheduleModel(title, desc, taskDate, fromTime, toTime, false, assignedBy);
        scheduleHelper.insertValues(scheduleModel);

        titleTv.setText("");
        descTv.setText("");
        taskDateTv.setText(selectedDate.toString());
        fromtimeTv.setText("00:00");
        toTimeTv.setText("00:00");

        addTaskLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.VISIBLE);

        refreshAdapters();
    }

    private void refreshAdapters(){
        System.out.println("check refresh Adapter" + CalendarUtils.selectedDate);
        scheduleModels = scheduleHelper.getDaySchedule(CalendarUtils.selectedDate);
        scheduleModels = scheduleHelper.getDaySchedule(dateSelect);
        weekScheduleAdapter = new WeekScheduleAdapter(getContext(), scheduleModels );//TODO
        weekScheduleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        weekScheduleList.setAdapter(weekScheduleAdapter);
        weekScheduleAdapter.notifyDataSetChanged();
    }

    private void openDatePicker(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        System.out.println(d);
                        taskDateTv.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        System.out.println(taskDateTv.getText());
                        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, monthOfYear,dayOfMonth-1);
                        int week_number=gregorianCalendar.get(Calendar.DAY_OF_WEEK);
                        if(week_number == 1) taskDayTv.setText("("+ "MONDAY" + ")");
                        if(week_number == 2) taskDayTv.setText("("+ "TUESDAY" + ")");
                        if(week_number == 3) taskDayTv.setText("("+ "WEDNESDAY" + ")");
                        if(week_number == 4) taskDayTv.setText("("+ "THURSDAY" + ")");
                        if(week_number == 5) taskDayTv.setText("("+ "FRIDAY" + ")");
                        if(week_number == 6) taskDayTv.setText("("+ "SATURDAY" + ")");
                        if(week_number == 7) taskDayTv.setText("("+ "SUNDAY" + ")");

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    //TO OPEN THE DATE DIALOG AND SET TO EDIT TEXT
    public void openDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        EditText tvStartDate = requireView().findViewById(R.id.EdtStartDatee);
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

    //TO OPEN THE TIME DIALOG AND SET TO EDIT TEXT
    public void openTimePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Format the selected time as "hh:mm:ss"
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hourOfDay, minute, 0);

                        // Assuming you have an EditText for displaying the selected time
                        EditText tvStartTime = requireView().findViewById(R.id.EdtTime);
                        tvStartTime.setText(selectedTime);
                    }
                },
                hour, minute, true // true for 24-hour format
        );

        timePickerDialog.show();
    }

    private void openTimePicker(View v){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String hour = (hourOfDay > 12)? (hourOfDay-12)+"": hourOfDay+"";
                        String t = CalendarUtils.formattedTime(LocalTime.of(hourOfDay,minute));

                        if(v == toTimeTv)
                            toTimeTv.setText(t);
                        else
                            fromtimeTv.setText(t);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void openNewTaskDialog(){
        addTaskLayout.setVisibility(View.VISIBLE);
        calendarLayout.setVisibility(View.GONE);
    }

    private void closeAddTaskDialog(){
        addTaskLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.VISIBLE);
    }

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        dateSelect = selectedDate.minusWeeks(1);
        setWeekView();
        refreshAdapters();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        dateSelect = selectedDate.plusWeeks(1);
        setWeekView();
        refreshAdapters();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addSchedule() {
        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Wait...", true);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        TestScheduleModel model = new TestScheduleModel();

        // Search for the data
        EditText etDate = requireView().findViewById(R.id.EdtStartDatee);
        EditText etTime = requireView().findViewById(R.id.EdtTime);
        EditText etTargetNameDaily = requireView().findViewById(R.id.titleTv);
        String DateText = etDate.getText().toString();
        String TimeText = etTime.getText().toString();
        String Title = etTargetNameDaily.getText().toString();

        // Logging to check if everything is being set
        Log.d("MyApp", "StartDate in addTarget: " + DateText + TimeText);
        Log.d("MyApp", "EndDate in addTarget: " + TimeText);
        Log.d("MyApp", "TargetName in addTarget: " + Title);


        // Set Data to the Model
        model.setUserId(userDetails.getUserId());
        model.setScheduledDateAndTime(DateText + " " + TimeText);
        model.setTitle(Title);

        // SUBJECTS
        AutoCompleteTextView subjectsAutoCompleteTextView = requireView().findViewById(R.id.subjects_spinnerDaily);
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
        AutoCompleteTextView topicAutoCompleteTextView = requireView().findViewById(R.id.topic_spinnerDaily);
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

        model.setIsCompleted(null);
        model.setStartDate(null);
        model.setEndDate(null);

        System.out.println();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            Log.e("Schedule Model ", "Schedules");
            System.out.println(data);
            System.out.println("url " + App.ADD_SCHEDULE_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.ADD_SCHEDULE_URL, new JSONObject(data),

                    response -> {
                        progressDialog.dismiss();
                        closeAddTaskDialog();
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
                closeAddTaskDialog();
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

    private void checkValidation() {
        EditText setTarget = requireView().findViewById(R.id.titleTv);
        EditText setDate = requireView().findViewById(R.id.EdtStartDatee);
        EditText setEndDate = requireView().findViewById(R.id.EdtTime);
        EditText setSubject = requireView().findViewById(R.id.subjects_spinnerDaily);
        EditText setTopic = requireView().findViewById(R.id.topic_spinnerDaily);

        String setTargetText = setTarget.getEditableText().toString().trim();
        String setDateText = setDate.getEditableText().toString().trim();
        String setEndDatetText = setEndDate.getEditableText().toString().trim();
        String setSubjectText = setSubject.getEditableText().toString().trim();
        String setTopicText = setTopic.getEditableText().toString().trim();

        if (TextUtils.isEmpty(setTargetText) || TextUtils.isEmpty(setDateText) || TextUtils.isEmpty(setEndDatetText) || TextUtils.isEmpty(setSubjectText) || TextUtils.isEmpty(setTopicText)) {
            Toast.makeText(getContext(), "Set the Target Details Please", Toast.LENGTH_SHORT).show();
        } else {
            addSchedule();
        }
    }

    public interface DeleteCallback {
        void onDeleteSuccess();
        void onDeleteFailed(String errorMessage);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList) {
        DataSource dataSource = new DataSource(getContext());
        ScheduleAdapter adapter = new ScheduleAdapter(getContext(),scheduleList,dataSource);
        dayScheduleList.setAdapter(adapter);
    }

    public void getSchedule(Context context, Long userId, String startDate, String endDate, ScheduleCallback callback) {

        ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Loading Data...", true);
        dialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        try {

//            String url = App.GET_SCHEDULE_URL;
            System.out.println("url " + App.GET_SCHEDULE_URL);

            JSONObject requestBody = new JSONObject();
            requestBody.put("userId", userId);
            requestBody.put("startDate", startDate);
            requestBody.put("endDate", endDate);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, App.GET_SCHEDULE_URL, requestBody,
                    response -> {
                        dialog.dismiss();
                        System.out.println(requestBody);
                        try {
                            // to json object to extract data from it
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                ArrayList<TestScheduleModel> scheduleList = new ArrayList<>();

                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);

                                    TestScheduleModel scheduleModel = new TestScheduleModel();

//                                    noSchedules.setVisibility(View.GONE);
                                    NoDataAvailable.setVisibility(View.INVISIBLE);
                                    dayScheduleList.setVisibility(View.VISIBLE);
                                    scheduleModel.setId(jsonObject.getInt("id"));
                                    scheduleModel.setUserId(jsonObject.getLong("userId"));
                                    scheduleModel.setStartDate(jsonObject.isNull("startDate") ? null : jsonObject.getString("startDate"));
                                    scheduleModel.setEndDate(jsonObject.isNull("endDate") ? null : jsonObject.getString("endDate"));
                                    scheduleModel.setSubjectId(jsonObject.getLong("subjectId"));
                                    scheduleModel.setTopicId(jsonObject.isNull("topicId")? null : jsonObject.getLong("topicId"));
                                    scheduleModel.setTitle(jsonObject.getString("title"));
//                                    scheduleModel.setScheduledDateAndTime(jsonObject.getString("scheduledDateAndTime"));
                                    scheduleModel.setIsCompleted(jsonObject.isNull("isCompleted") ? null : jsonObject.getInt("completedTarget"));

                                    // Formatting the Date
                                    String scheduledDate = jsonObject.getString("scheduledDateAndTime").split(" ")[0];
                                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("d MMMM, yyyy ");
                                    try {
                                        Date date = inputDateFormat.parse(scheduledDate);
                                        String formattedDate = outputDateFormat.format(date);
                                        scheduleModel.setScheduledDateAndTime(formattedDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    // Formatting the Time
                                    String scheduledTime = jsonObject.getString("scheduledDateAndTime").split(" ")[1];
                                    SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a");
                                    try {
                                        Date time = inputTimeFormat.parse(scheduledTime);
                                        String formattedTime = outputTimeFormat.format(time);
                                        scheduleModel.setScheduledDateAndTime(scheduleModel.getScheduledDateAndTime() + " " + formattedTime);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                    Log.d("Target ID", String.valueOf(scheduleModel.getId()));
                                    Log.d("Target Name", scheduleModel.getTitle());
                                    scheduleList.add(scheduleModel);

                                }
                                callback.onSchedulesLoaded(scheduleList);
                                DataSource dataSource = new DataSource(getContext());
                                ScheduleAdapter adapter = new ScheduleAdapter(context,scheduleList,dataSource);
                                dayScheduleList.setAdapter(adapter);
                            } else if (status.equals("failed")) {
//                                noSchedules.setVisibility(View.VISIBLE);
                                ArrayList<TestScheduleModel> scheduleList = new ArrayList<>();
                                DataSource dataSource = new DataSource(getContext());
                                ScheduleAdapter adapter = new ScheduleAdapter(context,scheduleList,dataSource);
                                adapter.clearData();
                                dayScheduleList.setVisibility(View.GONE);

                            } else {

//                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                NoDataAvailable.setVisibility(View.VISIBLE);
                                ArrayList<TestScheduleModel> scheduleList = new ArrayList<>();
                                DataSource dataSource = new DataSource(getContext());
                                ScheduleAdapter adapter = new ScheduleAdapter(context,scheduleList,dataSource);
                                adapter.clearData();
//                                noSchedules.setVisibility(View.VISIBLE);
                                dayScheduleList.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }, error -> {

                dialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(getContext())
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
                    System.out.println();
                    return headers;
                }
            };
            Log.e("Targets: ", "response");
//            System.out.println(jsonObjReq);
            requestQueue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WeekScheduleAdapter extends RecyclerView.Adapter<WeekScheduleAdapter.ViewHolder>{

        Context context;
        List<ScheduleModel> models;

        public WeekScheduleAdapter(Context context, List<ScheduleModel> models) {
            this.context = context;
            this.models = models;
            System.out.println(models);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.work_cell, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeekScheduleAdapter.ViewHolder holder, int position) {
            System.out.println(models.get(position));
            holder.fromTimeTv.setText(models.get(position).getFromTime().toString());
            holder.toTimeTv.setText(models.get(position).getToTime().toString());
            holder.workTitleTv.setText(models.get(position).getTitle());
            holder.workDescTv.setText(models.get(position).getDescp());
            System.out.println("teacher" + models.get(position).getDescp());
            if(models.get(position).getAssignedBy().equals("teacher")){
                holder.teacherImage.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView fromTimeTv, toTimeTv, workTitleTv, workDescTv;
            ImageButton teacherImage;

            public ViewHolder(@NonNull View itemView){
                super(itemView);

                fromTimeTv = itemView.findViewById(R.id.fromTimeTv);
                toTimeTv = itemView.findViewById(R.id.toTimeTv);
                workTitleTv = itemView.findViewById(R.id.workTitleTv);
                workDescTv = itemView.findViewById(R.id.workDescTv);
                teacherImage = itemView.findViewById(R.id.teacherImg);
            }
        }
    }

    public static void deleteSchedule(Context context, Integer scheduleId, TodayScheduleFragment.DeleteCallback callback) {
        ProgressDialog dialog = ProgressDialog.show(context, "", "Deleting Target...", true);
        dialog.show();

        DataSource dataSource = new DataSource(context);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            String url = App.DELETE_SCHEDULE_URL.replace("{id}", String.valueOf(scheduleId));

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        dialog.dismiss();
                        try {
                            // Handle success response
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");

                            if (status.equals("true")) {
                                callback.onDeleteSuccess();
                            } else if (status.equals("failed")) {
                                callback.onDeleteFailed(message);
                            } else {
                                callback.onDeleteFailed("Deleting target failed");
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        dialog.dismiss();
                        // Handle error response
                        callback.onDeleteFailed("Error deleting target");
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };

            requestQueue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}