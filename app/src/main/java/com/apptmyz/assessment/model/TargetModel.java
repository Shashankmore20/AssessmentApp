package com.apptmyz.assessment.model;

public class TargetModel {
    private Integer id;
    private Long userId;
    private String startDate;
    private String endDate;
    private Long subjectId;
    private Long topicId;
    private Integer testCount;
    private Integer repeatingType; // select the type of repeat i.e., daily or on a specific day, week or month
    private Integer repeatingDay; // set the repeating day
    private Integer repeatingWeek; // set the repeating week
    private Integer repeatingMonth; // set the repeating month
    private String targetName;
    private Integer completedTarget;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompletedTarget() {
        return completedTarget;
    }

    public void setCompletedTarget(Integer completedTarget) {
        this.completedTarget = completedTarget;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getTestCount() {
        return testCount;
    }

    public void setTestCount(Integer testCount) {
        this.testCount = testCount;
    }

    public Integer getRepeatingType() {
        return repeatingType;
    }

    public void setRepeatingType(Integer repeatingType) {
        this.repeatingType = repeatingType;
    }

    public Integer getRepeatingDay() {
        return repeatingDay;
    }

    public void setRepeatingDay(Integer repeatingDay) {
        this.repeatingDay = repeatingDay;
    }

    public Integer getRepeatingWeek() {
        return repeatingWeek;
    }

    public void setRepeatingWeek(Integer repeatingWeek) {
        this.repeatingWeek = repeatingWeek;
    }

    public Integer getRepeatingMonth() {
        return repeatingMonth;
    }

    public void setRepeatingMonth(Integer repeatingMonth) {
        this.repeatingMonth = repeatingMonth;
    }
}
