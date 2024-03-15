package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class TestPlayQuestionModel {

    private boolean attempted;
    private boolean bookMarked;

    private int questionNo;
    private Long questionId;

    private long duration;

    private String className;
    private String classDescription;
    private String subjectName;
    private String subjectDescription;
    private String topicName;
    private String topicDescription;

    private String question;

    private int questionType;//0- single, 1- multi-choose, 2- descriptive

    private ArrayList<ChoicesModel> choices;

    //----------------------------------------------------------------------
    private ArrayList<Integer> answer;

    //for 0 - single
    // eg:- arraylist = {1}
    //for 1 - multi-choose
    // eg:- arraylist = {1, 2, 3}
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    private ArrayList<Integer> userAnswer;

    //for 0 - single
    // eg:- arraylist = {1}
    //for 1 - multi-choose
    // eg:- arraylist = {1, 2, 3}
    //-----------------------------------------------------------------------

    private int questionComplexity;

    //for description answer
    private String userDescriptionAns;

    @Override
    public String toString() {
        return "TestPlayQuestionModel{" +
                "questionNo=" + questionNo +
                ", questionId=" + questionId +
                ", duration=" + duration +
                ", className='" + className + '\'' +
                ", classDescription='" + classDescription + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectDescription='" + subjectDescription + '\'' +
                ", topicName='" + topicName + '\'' +
                ", topicDescription='" + topicDescription + '\'' +
                ", question='" + question + '\'' +
                ", questionType=" + questionType +
                ", choices=" + choices +
                ", answer=" + answer +
                ", userAnswer=" + userAnswer +
                ", questionComplexity=" + questionComplexity +
                ", userDescriptionAns='" + userDescriptionAns + '\'' +
                '}';
    }

    public boolean isAttempted() {
        return attempted;
    }

    public void setAttempted(boolean attempted) {
        this.attempted = attempted;
    }

    public boolean isBookMarked() {
        return bookMarked;
    }

    public void setBookMarked(boolean bookMarked) {
        this.bookMarked = bookMarked;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long avgDuration) {
        this.duration = avgDuration;
    }

    public ArrayList<Integer> getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(ArrayList<Integer> userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public ArrayList<ChoicesModel> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<ChoicesModel> choices) {
        this.choices = choices;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public int getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getUserDescriptionAns() {
        return userDescriptionAns;
    }

    public void setUserDescriptionAns(String userDescriptionAns) {
        this.userDescriptionAns = userDescriptionAns;
    }

    public ArrayList<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<Integer> answer) {
        this.answer = answer;
    }

    public int getQuestionComplexity() {
        return questionComplexity;
    }

    public void setQuestionComplexity(int questionComplexity) {
        this.questionComplexity = questionComplexity;
    }
}
