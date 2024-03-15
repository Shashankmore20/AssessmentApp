package com.apptmyz.assessment.model;

public class DashboardModel {
	private Long userId;
	private Integer classId;
	private Long subjectId;
	private String state;
	private Long totalScored;
	private Long totalMarks;
	private Integer totalClassCount;
	private Long totalStateCount;
	private Long totalCountryCount;
	private Long userClassWiseRanking; 
	private Long userStateWiseRanking;
	private Long userCountryWiseRanking;
	private TestModel recentTest;
	
	public Integer getTotalClassCount() {
		return totalClassCount;
	}
	public void setTotalClassCount(Integer totalClassCount) {
		this.totalClassCount = totalClassCount;
	}
	public Long getTotalCountryCount() {
		return totalCountryCount;
	}
	public void setTotalCountryCount(Long totalCountryCount) {
		this.totalCountryCount = totalCountryCount;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getClassId() {
		return classId;
	}
	public void setClassId(Integer classId) {
		this.classId = classId;
	}
	public Long getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getTotalScored() {
		return totalScored;
	}
	public void setTotalScored(Long totalScored) {
		this.totalScored = totalScored;
	}
	public Long getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(Long totalMarks) {
		this.totalMarks = totalMarks;
	}
	public Long getUserClassWiseRanking() {
		return userClassWiseRanking;
	}
	public Long getTotalStateCount() {
		return totalStateCount;
	}
	public void setTotalStateCount(Long totalStateCount) {
		this.totalStateCount = totalStateCount;
	}
	public void setUserClassWiseRanking(Long userClassWiseRanking) {
		this.userClassWiseRanking = userClassWiseRanking;
	}
	public Long getUserStateWiseRanking() {
		return userStateWiseRanking;
	}
	public void setUserStateWiseRanking(Long userStateWiseRanking) {
		this.userStateWiseRanking = userStateWiseRanking;
	}
	public Long getUserCountryWiseRanking() {
		return userCountryWiseRanking;
	}
	public void setUserCountryWiseRanking(Long userCountryWiseRanking) {
		this.userCountryWiseRanking = userCountryWiseRanking;
	}
	public TestModel getRecentTest() {
		return recentTest;
	}
	public void setRecentTest(TestModel recentTest) {
		this.recentTest = recentTest;
	}



}
