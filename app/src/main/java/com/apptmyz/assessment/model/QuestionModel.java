package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class QuestionModel implements Serializable {
    private String testquestionid;
    private int result;

    public QuestionModel() {
    }

    public QuestionModel(String testquestionid) {
        this.testquestionid = testquestionid;
    }

    public QuestionModel(String testquestionid, int result) {
        this.testquestionid = testquestionid;
        this.result = result;
    }

    public String getTestquestionid() {
        return testquestionid;
    }

    public void setTestquestionid(String testquestionid) {
        this.testquestionid = testquestionid;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "testquestionid='" + testquestionid + '\'' +
                ", result=" + result +
                '}';
    }
}
