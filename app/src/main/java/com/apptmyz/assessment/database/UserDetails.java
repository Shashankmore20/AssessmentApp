package com.apptmyz.assessment.database;

public class UserDetails {
    long userId;
    String firstName;
    String lastName;
    String loginName;
    String emailId;
    String dob;
    String gender;
    int stateId;
    String contactNumber;
    int userClass;
    String password;
    int roleId;
    String profilePicture;
    private static final UserDetails ourInstance = new UserDetails();
    public static UserDetails getInstance() {
        return ourInstance;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", stateId=" + stateId +
                ", contactNumber='" + contactNumber + '\'' +
                ", userClass=" + userClass +
                ", password='" + password + '\'' +
                ", roleId=" + roleId +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserDetails() { }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getUserClass() {
        return userClass;
    }

    public void setUserClass(int userClass) {
        this.userClass = userClass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public static UserDetails getOurInstance() {
        return ourInstance;
    }
}
