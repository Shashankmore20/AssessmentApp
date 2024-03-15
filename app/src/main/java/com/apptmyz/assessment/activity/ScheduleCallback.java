package com.apptmyz.assessment.activity;

import com.apptmyz.assessment.model.TargetModel;
import com.apptmyz.assessment.model.TestScheduleModel;

import java.util.ArrayList;

public interface ScheduleCallback {
    void onSchedulesLoaded(ArrayList<TestScheduleModel> scheduleList);
}
