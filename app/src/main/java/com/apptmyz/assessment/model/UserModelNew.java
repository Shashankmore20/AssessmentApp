package com.apptmyz.assessment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserModelNew {
    private Long userId;


    private String firstName;
    private String lastName;

    private String loginName;

    private String emailId;

    private String dob;

    private String gender;

    private String profilePicture;

    private String contactNumber;

    private Integer userClass;

    private Date createdOn;

    private String password;

    private Integer isActive;

    private String lastLogin;

    private Integer failedAttempts;

    private Date lastFailedLogin;

    private Integer isLocked;

    private Long roleId;

    private String status;

    private Integer stateId;

    private Integer schoolId;

    private Integer curriculumId;

    private Integer planId;

    private ArrayList<UserModelNew> childrenList;

    private ArrayList<ClassSubjectModel> classModelList;
    private String schoolName;
    private String schoolCode;

    private String classesList;  // classes with comma separation

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Integer getUserClass() {
        return userClass;
    }

    public void setUserClass(Integer userClass) {
        this.userClass = userClass;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Date getLastFailedLogin() {
        return lastFailedLogin;
    }

    public void setLastFailedLogin(Date lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }

    public Integer getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Integer curriculumId) {
        this.curriculumId = curriculumId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public ArrayList<UserModelNew> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(ArrayList<UserModelNew> childrenList) {
        this.childrenList = childrenList;
    }

    public ArrayList<ClassSubjectModel> getClassModelList() {
        return classModelList;
    }

    public void setClassModelList(ArrayList<ClassSubjectModel> classModelList) {
        this.classModelList = classModelList;
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

    public String getClassesList() {
        return classesList;
    }

    public void setClassesList(String classesList) {
        this.classesList = classesList;
    }

    @Override
    public String toString() {
        return "UserModelNew{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", userClass=" + userClass +
                ", createdOn=" + createdOn +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", lastLogin='" + lastLogin + '\'' +
                ", failedAttempts=" + failedAttempts +
                ", lastFailedLogin=" + lastFailedLogin +
                ", isLocked=" + isLocked +
                ", roleId=" + roleId +
                ", status='" + status + '\'' +
                ", stateId=" + stateId +
                ", schoolId=" + schoolId +
                ", curriculumId=" + curriculumId +
                ", planId=" + planId +
                ", childrenList=" + childrenList +
                ", classModelList=" + classModelList +
                ", schoolName='" + schoolName + '\'' +
                ", schoolCode='" + schoolCode + '\'' +
                ", classesList='" + classesList + '\'' +
                '}';
    }
}
