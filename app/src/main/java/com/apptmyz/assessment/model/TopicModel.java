package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class TopicModel {
    Long topicId;
    Long subjectId;
    String topicName;
    String topicDescp;
    ArrayList<Integer> difficultyLevel;//1-Beginner, 2-Intermediate, 3-Final

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDescp() {
        return topicDescp;
    }

    public void setTopicDescp(String topicDescp) {
        this.topicDescp = topicDescp;
    }

    public ArrayList<Integer> getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(ArrayList<Integer> difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
