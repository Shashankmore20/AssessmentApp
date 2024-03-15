package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

@Keep
public class UserTestClass implements Serializable {
    private String usertestid, title, testdate, starttime, endtime, subjectid, topicid, testduration;
    private String questions;
    private String timestamp;

    public UserTestClass() {
    }

    public UserTestClass(String usertestid, String title, String testdate, String starttime, String questions) {
        this.usertestid = usertestid;
        this.title = title;
        this.testdate = testdate;
        this.starttime = starttime;
        this.questions = questions;
    }

    public String getUsertestid() {
        return usertestid;
    }

    public void setUsertestid(String usertestid) {
        this.usertestid = usertestid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTestdate() {
        return testdate;
    }

    public void setTestdate(String testdate) {
        this.testdate = testdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getTopicid() {
        return topicid;
    }

    public void setTopicid(String topicid) {
        this.topicid = topicid;
    }

    public String getTestduration() {
        return testduration;
    }

    public void setTestduration(String testduration) {
        this.testduration = testduration;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
