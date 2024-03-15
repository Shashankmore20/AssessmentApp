package com.apptmyz.assessment.activity;

import static com.apptmyz.assessment.utils.CalendarUtils.daysInMonthArray;
import static com.apptmyz.assessment.utils.CalendarUtils.monthYearFromDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.utils.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        CalendarUtils.selectedDate = LocalDate.now();
        initWidgets();
        setMonthView();

    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTv);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth =
                daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

//    public void previousMonthAction(View view)
//    {
//        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
//        setMonthView();
//    }

//    public void nextMonthAction(View view)
//    {
//        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
//        setMonthView();
//    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    public void weeklyAction(View view)
    {
        //startActivity(new Intent(this, WeekViewActivity.class));
    }
}