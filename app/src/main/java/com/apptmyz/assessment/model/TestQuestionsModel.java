package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class TestQuestionsModel {
    private Long questionId;
    private String question;

    private Long userTestId;
    private Long duration;
    private String startTime;
    private String endTime;
    private String userAnswer;

    private ArrayList<Integer> userAnswerList;
    private ArrayList<ChoicesModel> choicesList;
    private ArrayList<Integer> answerList;
    private int questionComplexity;
    private int questionType;
    private Integer answerStatus;

//    @Override
//    public String toString() {
//        return "TestQuestionsModel{" +
//                "questionId=" + questionId +
//                ", userTestId=" + userTestId +
//                ", duration=" + duration +
//                ", startTime='" + startTime + '\'' +
//                ", endTime='" + endTime + '\'' +
//                ", answer='" + answer + '\'' +
//                ", answerStatus=" + answerStatus +
//                '}';
//    }


    @Override
    public String toString() {
        return "TestQuestionsModel{" +
                "questionId=" + questionId +
                ", question='" + question + '\'' +
                ", userTestId=" + userTestId +
                ", duration=" + duration +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", answer='" + userAnswer + '\'' +
                ", userAnswerList=" + userAnswerList +
                ", choicesList=" + choicesList +
                ", answerList=" + answerList +
                ", questionComplexity=" + questionComplexity +
                ", questionType=" + questionType +
                ", answerStatus=" + answerStatus +
                '}';
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public ArrayList<Integer> getUserAnswerList() {
        return userAnswerList;
    }

    public void setUserAnswerList(ArrayList<Integer> userAnswerList) {
        this.userAnswerList = userAnswerList;
    }

    public ArrayList<Integer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(ArrayList<Integer> answerList) {
        this.answerList = answerList;
    }

    public int getQuestionComplexity() {
        return questionComplexity;
    }

    public void setQuestionComplexity(int questionComplexity) {
        this.questionComplexity = questionComplexity;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<ChoicesModel> getChoicesList() {
        return choicesList;
    }

    public void setChoicesList(ArrayList<ChoicesModel> choicesList) {
        this.choicesList = choicesList;
    }

    public Long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    public Long getUserTestId() {
        return userTestId;
    }
    public void setUserTestId(Long userTestId) {
        this.userTestId = userTestId;
    }
    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
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
    public String getUserAnswer() {
        return userAnswer;
    }
    public void setUserAnswer(String answer) {
        this.userAnswer = answer;
    }
    public Integer getAnswerStatus() {
        return answerStatus;
    }
    public void setAnswerStatus(Integer answerStatus) {
        this.answerStatus = answerStatus;
    }

}
