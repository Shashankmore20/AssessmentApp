package com.apptmyz.assessment.model;

import java.util.List;

public class TestModel {
    private Long testId;
    private Long userId;
    private String testDate;
    private Long testDuration;
    private String 	startTime;
    private Long avgDuration;
    private Long Duration;
    private String endTime;
    private Integer testComplexity;
    private Long subjectId;
    private Long topicId;
    private int attemptedQuestions;
    private Integer totalQuestions;
    private Integer totalCorrectAnswers;
    private Integer totalWrongAnswers;
    private List<TestQuestionsModel> testQuestionsList;
    private float avgPercentage;

    @Override
    public String toString() {
        return "TestModel{" +
                "testId=" + testId +
                ", userId=" + userId +
                ", testDate='" + testDate + '\'' +
                ", testDuration=" + testDuration +
                ", startTime='" + startTime + '\'' +
                ", avgDuration=" + avgDuration +
                ", endTime='" + endTime + '\'' +
                ", testComplexity=" + testComplexity +
                ", subjectId=" + subjectId +
                ", topicId=" + topicId +
                ", totalQuestions=" + totalQuestions +
                ", totalCorrectAnswers=" + totalCorrectAnswers +
                ", totalWrongAnswers=" + totalWrongAnswers +
                ", testQuestionsList=" + testQuestionsList +
                '}';
    }

    public Long getDuration() {
        return Duration;
    }

    public void setDuration(Long duration) {
        Duration = duration;
    }

    public float getAvgPercentage() {
        return avgPercentage;
    }

    public void setAvgPercentage(float avgPercentage) {
        this.avgPercentage = avgPercentage;
    }

    public int getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public void setAttemptedQuestions(int attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }

    public Long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Long avgDuration) {
        this.avgDuration = avgDuration;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getTestDate() {
        return testDate;
    }
    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }
    public Long getTestDuration() {
        return testDuration;
    }
    public void setTestDuration(Long testDuration) {
        this.testDuration = testDuration;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public Integer getTestComplexity() {
        return testComplexity;
    }
    public void setTestComplexity(Integer testComplexity) {
        this.testComplexity = testComplexity;
    }
    public Long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
    public Long getTopicId() {
        return topicId;
    }
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    public Integer getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }
    public void setTotalCorrectAnswers(Integer totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }
    public Integer getTotalWrongAnswers() {
        return totalWrongAnswers;
    }
    public void setTotalWrongAnswers(Integer totalWrongAnswers) {
        this.totalWrongAnswers = totalWrongAnswers;
    }
    public List<TestQuestionsModel> getTestQuestionsList() {
        return testQuestionsList;
    }
    public void setTestQuestionsList(List<TestQuestionsModel> testQuestionsList) {
        this.testQuestionsList = testQuestionsList;
    }
}
