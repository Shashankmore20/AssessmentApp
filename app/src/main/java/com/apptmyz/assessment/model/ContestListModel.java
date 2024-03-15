package com.apptmyz.assessment.model;

public class ContestListModel {
    private String usertestid, testdate, testduration, starttesttime, endtesttime, title,timestamp;

    public ContestListModel() {
    }

    public String getUsertestid() {
        return usertestid;
    }

    public void setUsertestid(String usertestid) {
        this.usertestid = usertestid;
    }

    public String getTestdate() {
        return testdate;
    }

    public void setTestdate(String testdate) {
        this.testdate = testdate;
    }

    public String getTestduration() {
        return testduration;
    }

    public void setTestduration(String testduration) {
        this.testduration = testduration;
    }

    public String getStarttesttime() {
        return starttesttime;
    }

    public void setStarttesttime(String starttesttime) {
        this.starttesttime = starttesttime;
    }

    public String getEndtesttime() {
        return endtesttime;
    }

    public void setEndtesttime(String endtesttime) {
        this.endtesttime = endtesttime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
