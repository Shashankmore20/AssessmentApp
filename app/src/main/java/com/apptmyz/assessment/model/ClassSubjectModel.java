package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class ClassSubjectModel {
    private Long id;
    private Integer activeFlag;
    private Integer classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Integer curriculumId;
    private Integer schoolId;
    private ArrayList<SubjectModel> subjectList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Integer curriculumId) {
        this.curriculumId = curriculumId;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public ArrayList<SubjectModel> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(ArrayList<SubjectModel> subjectList) {
        this.subjectList = subjectList;
    }
}
