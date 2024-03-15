package com.apptmyz.assessment.model;

public class RankLevelsModel {
    String rankLevel;
    String studentRank;
    String totalStudents;
    Double percentile;

    public RankLevelsModel(String rankLevel, String studentRank, String totalStudents, Double percentile, String hexColor) {
        this.rankLevel = rankLevel;
        this.studentRank = studentRank;
        this.totalStudents = totalStudents;
        this.percentile = percentile;
        this.hexColor = hexColor;
    }

    String hexColor; // Add color field


    public String getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(String rankLevel) {
        this.rankLevel = rankLevel;
    }

    public String getStudentRank() {
        return studentRank;
    }

    public void setStudentRank(String studentRank) {
        this.studentRank = studentRank;
    }

    public String getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(String totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Double getPercentile() {
        return percentile;
    }

    public void setPercentile(Double percentile) {
        this.percentile = percentile;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }
}
