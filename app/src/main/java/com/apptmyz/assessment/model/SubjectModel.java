package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class SubjectModel {
    Long id;
    String subjectName;
    String subjectDescription;
    String fileName;
    ArrayList<TopicModel> topics;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public ArrayList<TopicModel> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<TopicModel> topics) {
        this.topics = topics;
    }

    //    @Override
//    public String toString() {
//        return "SubjectModel{" +
//                "id=" + id +
//                ", subjectName='" + subjectName + '\'' +
//                ", subjectDescription='" + subjectDescription + '\'' +
//                ", fileName='" + fileName + '\'' +
//                ", topics=" + topics +
//                '}';
//    }
    @Override
    public String toString() {
        return subjectName;
    }
//
}
