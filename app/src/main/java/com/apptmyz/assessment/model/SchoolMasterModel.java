package com.apptmyz.assessment.model;

import java.util.ArrayList;
import java.util.List;

public class SchoolMasterModel {

    Integer id;
    String schoolName;
    String schoolCode;
    String schoolAddress;
    String city;
    String country;
    Integer activeFlag;
    String state;
    ArrayList<CurriculumModel> curriculums;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CurriculumModel> getCurriculums() {
        return curriculums;
    }

    public void setCurriculums(ArrayList<CurriculumModel> curriculums) {
        this.curriculums = curriculums;
    }
}
