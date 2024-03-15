package com.apptmyz.assessment.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleModel {

    String title;
    String descp;
    String date;
    LocalTime fromTime;
    LocalTime toTime;
    Boolean taskStatus;
    String assignedBy;

    public ScheduleModel(String title, String descp, String date, LocalTime fromTime, LocalTime toTime, Boolean taskStatus, String assignedBy) {
        this.title = title;
        this.descp = descp;
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.taskStatus = taskStatus;
        this.assignedBy = assignedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public Boolean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "ScheduleModel{" +
                "title='" + title + '\'' +
                ", descp='" + descp + '\'' +
                ", date='" + date + '\'' +
                ", fromTime=" + fromTime +
                ", toTime=" + toTime +
                ", taskStatus=" + taskStatus +
                ", assignedBy='" + assignedBy + '\'' +
                '}';
    }
}
