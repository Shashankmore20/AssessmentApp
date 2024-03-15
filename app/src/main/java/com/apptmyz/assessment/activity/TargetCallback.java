package com.apptmyz.assessment.activity;

import com.apptmyz.assessment.model.TargetModel;

import java.util.ArrayList;

public interface TargetCallback {
    void onTargetsLoaded(ArrayList<TargetModel> targetList);
}
