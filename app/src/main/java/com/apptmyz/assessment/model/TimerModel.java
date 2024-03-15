package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Objects;

@Keep
public class TimerModel implements Serializable {

    private String questionId, scoretime;

    public TimerModel() {
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getScoretime() {
        return scoretime;
    }

    public void setScoretime(String scoretime) {
        this.scoretime = scoretime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerModel that = (TimerModel) o;
        return Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId);
    }

    @Override
    public String toString() {
        return "{" +
                "questionId='" + questionId + '\'' +
                ", scoretime='" + scoretime + '\'' +
                '}';
    }
}
