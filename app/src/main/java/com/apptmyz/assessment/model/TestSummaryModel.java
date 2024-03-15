package com.apptmyz.assessment.model;

public class TestSummaryModel {

    Long testId;
    String testDate;
    Long testDuration;
    String startTime;
    String endTime;
    int testComplexity;
    Long subjectId;
    Long topicId;
    int totalQuestions;
    int totalCorrectAnswers;
    int totalWrongAnswers;
    Long avgDuration;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
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

    public int getTestComplexity() {
        return testComplexity;
    }

    public void setTestComplexity(int testComplexity) {
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

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public int getTotalWrongAnswers() {
        return totalWrongAnswers;
    }

    public void setTotalWrongAnswers(int totalWrongAnswers) {
        this.totalWrongAnswers = totalWrongAnswers;
    }

    public Long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Long avgDuration) {
        this.avgDuration = avgDuration;
    }

    @Override
    public String toString() {
        return "TestSummaryModel{" +
                "testId=" + testId +
                ", testDate='" + testDate + '\'' +
                ", testDuration=" + testDuration +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", testComplexity=" + testComplexity +
                ", subjectId=" + subjectId +
                ", topicId=" + topicId +
                ", totalQuestions=" + totalQuestions +
                ", totalCorrectAnswers=" + totalCorrectAnswers +
                ", totalWrongAnswers=" + totalWrongAnswers +
                ", avgDuration=" + avgDuration +
                '}';
    }
}
