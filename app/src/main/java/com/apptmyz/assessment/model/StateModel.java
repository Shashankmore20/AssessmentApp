package com.apptmyz.assessment.model;

public class StateModel {

    int id;
    String stateCode;
    String stateName;

    @Override
    public String toString() {
        return stateName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
