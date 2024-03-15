package com.apptmyz.assessment.fragment;

import static com.apptmyz.assessment.utils.CalendarUtils.daysInMonthArray;
import static com.apptmyz.assessment.utils.CalendarUtils.monthYearFromDate;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.CalendarAdapter;
import com.apptmyz.assessment.utils.CalendarUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarScheduleFragment extends Fragment/*  implements CalendarAdapter.OnItemListener*/{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ImageButton forwardBtn, backBtn;

    ImageButton closeDialogBtn;

    FloatingActionButton fab;
    RelativeLayout addTaskLayout;

    TextView fromtimeTv, toTimeTv, taskDateTv, taskDayTv;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_schedule, container, false);

//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()) {
//                    case R.id.backBtn:
//                        previousMonthAction(view);
//                        break;
//                    case R.id.forwardBtn:
//                        nextMonthAction(view);
//                        break;
//                    case R.id.closeDialogBtn:
//                        closeAddTaskDialog();
//                        break;
//                    case R.id.fab:
//                        openNewTaskDialog();
//                        break;
//                    case R.id.fromTimeTv:
//                    case R.id.toTimeTv:
//                        openTimePicker(view);
//                        break;
//                    case R.id.taskDateTv:
//                        openDatePicker();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//
//        CalendarUtils.selectedDate = LocalDate.now();
//
//        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
//        monthYearText = view.findViewById(R.id.monthYearTv);
//        setMonthView();
//
//        forwardBtn = view.findViewById(R.id.forwardBtn);
//        forwardBtn.setOnClickListener(listener);
//        backBtn = view.findViewById(R.id.backBtn);
//        backBtn.setOnClickListener(listener);
//
//        fab = view.findViewById(R.id.fab);
//        fab.setOnClickListener(listener);
//        addTaskLayout = view.findViewById(R.id.addTaskLayout);
//        addTaskLayout.setVisibility(View.GONE);
//        closeDialogBtn = (ImageButton) view.findViewById(R.id.closeDialogBtn);
//        closeDialogBtn.setOnClickListener(listener);
//
//        fromtimeTv = view.findViewById(R.id.fromTimeTv);
//        fromtimeTv.setOnClickListener(listener);
//        toTimeTv = view.findViewById(R.id.toTimeTv);
//        toTimeTv.setOnClickListener(listener);
//
//        taskDateTv = view.findViewById(R.id.taskDateTv);
//        taskDayTv = view.findViewById(R.id.taskDayTv);
//        taskDayTv.setText("(" + LocalDate.now().getDayOfWeek().toString() + ")");
//
//        int d = LocalDate.now().getDayOfMonth();
//        int m = LocalDate.now().getMonthValue();
//        int y = LocalDate.now().getYear();
//
//        taskDateTv.setText(d + "-" + m + "-" + y);
//        taskDateTv.setOnClickListener(listener);

        return view;
    }

//    private void openDatePicker(){
//        final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
//                new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                                          int monthOfYear, int dayOfMonth) {
//
//                        taskDateTv.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, monthOfYear,dayOfMonth-1);
//                        int week_number=gregorianCalendar.get(Calendar.DAY_OF_WEEK);
//                        if(week_number == 1) taskDayTv.setText("("+ "MONDAY" + ")");
//                        if(week_number == 2) taskDayTv.setText("("+ "TUESDAY" + ")");
//                        if(week_number == 3) taskDayTv.setText("("+ "WEDNESDAY" + ")");
//                        if(week_number == 4) taskDayTv.setText("("+ "THURSDAY" + ")");
//                        if(week_number == 5) taskDayTv.setText("("+ "FRIDAY" + ")");
//                        if(week_number == 6) taskDayTv.setText("("+ "SATURDAY" + ")");
//                        if(week_number == 7) taskDayTv.setText("("+ "SUNDAY" + ")");
//
//                    }
//                }, mYear, mMonth, mDay);
//        datePickerDialog.show();
//    }
//
//    private void openTimePicker(View v){
//        final Calendar c = Calendar.getInstance();
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
//                new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//                        String am_pm = (hourOfDay < 12) ? "am" : "am";
//                        String hour = String.format("%02d",(hourOfDay > 12)? hourOfDay-12: hourOfDay);
//                        String min = String.format("%02d",minute);
//                        if(v == fromtimeTv)
//                            fromtimeTv.setText(hour + ":" + minute + " " + am_pm);
//                        else
//                            toTimeTv.setText(hour + ":" + minute + " " + am_pm);
//                    }
//                }, mHour, mMinute, false);
//        timePickerDialog.show();
//    }
//
//    private void closeAddTaskDialog(){
//        addTaskLayout.setVisibility(View.GONE);
//    }
//
//    private void openNewTaskDialog(){
//        addTaskLayout.setVisibility(View.VISIBLE);
//    }
//
//    private void setMonthView() {
//        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
//        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);
//
//        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
//        calendarRecyclerView.setLayoutManager(layoutManager);
//        calendarRecyclerView.setAdapter(calendarAdapter);
//    }
//
//    public void previousMonthAction(View view) {
//        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
//        setMonthView();
//    }
//
//    public void nextMonthAction(View view) {
//        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
//        setMonthView();
//    }
//
//    @Override
//    public void onItemClick(int position, LocalDate date) {
//        if(date != null)
//        {
//            CalendarUtils.selectedDate = date;
//            setMonthView();
//        }
//    }
}