package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class TopicSummaryModel {

    Long topicId;
    Long subjectId;
    int totalQuestions;
    int totalQuestionsAttempted;
    int totalCorrectQuestions;
    double averagePercentage;
    Long averageDuration;
    int totalAttemptedTests;
    ArrayList<TestSummaryModel> testSummaryModelArrayList;

    @Override
    public String toString() {
        return "TopicSummaryModel{" +
                "topicId=" + topicId +
                ", subjectId=" + subjectId +
                ", totalQuestions=" + totalQuestions +
                ", totalQuestionsAttempted=" + totalQuestionsAttempted +
                ", totalCorrectQuestions=" + totalCorrectQuestions +
                ", averagePercentage=" + averagePercentage +
                ", averageDuration=" + averageDuration +
                ", totalAttemptedTests=" + totalAttemptedTests +
                ", testSummaryModelArrayList=" + testSummaryModelArrayList +
                '}';
    }

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

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalQuestionsAttempted() {
        return totalQuestionsAttempted;
    }

    public void setTotalQuestionsAttempted(int totalQuestionsAttempted) {
        this.totalQuestionsAttempted = totalQuestionsAttempted;
    }

    public int getTotalCorrectQuestions() {
        return totalCorrectQuestions;
    }

    public void setTotalCorrectQuestions(int totalCorrectQuestions) {
        this.totalCorrectQuestions = totalCorrectQuestions;
    }

    public double getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(double averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public Long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(Long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public int getTotalAttemptedTests() {
        return totalAttemptedTests;
    }

    public void setTotalAttemptedTests(int totalAttemptedTests) {
        this.totalAttemptedTests = totalAttemptedTests;
    }

    public ArrayList<TestSummaryModel> getTestSummaryModelArrayList() {
        return testSummaryModelArrayList;
    }

    public void setTestSummaryModelArrayList(ArrayList<TestSummaryModel> testSummaryModelArrayList) {
        this.testSummaryModelArrayList = testSummaryModelArrayList;
    }
}
