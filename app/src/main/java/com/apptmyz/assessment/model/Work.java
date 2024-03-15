package com.apptmyz.assessment.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Work {
    public static ArrayList<Work> worksList = new ArrayList<>();

    public static ArrayList<Work> worksForDate(LocalDate date) {
        ArrayList<Work> works = new ArrayList<>();

        for(Work work : worksList) {
            if(work.getDate().equals(date))
                works.add(work);
        }

        return works;
    }

    private String name;
    private String desc;
    private LocalDate date;
    private LocalTime fromTime;
    private LocalTime toTime;

    public Work(String name, String desc, LocalDate date, LocalTime fromTime, LocalTime toTime) {
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public static ArrayList<Work> getWorksList() {
        return worksList;
    }

    public static void setWorksList(ArrayList<Work> worksList) {
        Work.worksList = worksList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
}
