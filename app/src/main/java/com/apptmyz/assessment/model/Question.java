package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Date;

@Keep
public class Question implements Serializable {
    private Long testquestionid, duration, questionid;
    private Integer questionComplexity, subjectId, topicId, classId;

    private Date starttime, endtime;
    private String userAnswer, question, choice1, choice2, choice3, choice4, answer, questionFiles, choice1Files,
            choice2Files, choice3Files, choice4Files;

    public Question() {
    }

    private Long usertestid;

    public Question(Long usertestid, Long testquestionid, Long questionid,
                    String question, Long duration, String userAnswer,
                    String choice1, String choice2, String choice3, String choice4, String answer) {
        this.usertestid = usertestid;
        this.questionid = questionid;
        this.testquestionid = testquestionid;
        this.question = question;
        this.duration = duration;
        this.userAnswer = userAnswer;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answer = answer;
    }

    public Long getTestquestionid() {
        return testquestionid;
    }

    public void setTestquestionid(Long testquestionid) {
        this.testquestionid = testquestionid;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Long getQuestionid() {
        return questionid;
    }

    public void setQuestionid(Long questionid) {
        this.questionid = questionid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestionComplexity() {
        return questionComplexity;
    }

    public void setQuestionComplexity(Integer questionComplexity) {
        this.questionComplexity = questionComplexity;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestionFiles() {
        return questionFiles;
    }

    public void setQuestionFiles(String questionFiles) {
        this.questionFiles = questionFiles;
    }

    public String getChoice1Files() {
        return choice1Files;
    }

    public void setChoice1Files(String choice1Files) {
        this.choice1Files = choice1Files;
    }

    public String getChoice2Files() {
        return choice2Files;
    }

    public void setChoice2Files(String choice2Files) {
        this.choice2Files = choice2Files;
    }

    public String getChoice3Files() {
        return choice3Files;
    }

    public void setChoice3Files(String choice3Files) {
        this.choice3Files = choice3Files;
    }

    public String getChoice4Files() {
        return choice4Files;
    }

    public void setChoice4Files(String choice4Files) {
        this.choice4Files = choice4Files;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }


    @Override
    public String toString() {
        return "Question{" +
                "testquestionid=" + testquestionid +
                ", duration=" + duration +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                ", userAnswer='" + userAnswer + '\'' +
                ", questionid=" + questionid +
                ", question='" + question + '\'' +
                ", questionComplexity=" + questionComplexity +
                ", choice1='" + choice1 + '\'' +
                ", choice2='" + choice2 + '\'' +
                ", choice3='" + choice3 + '\'' +
                ", choice4='" + choice4 + '\'' +
                ", answer='" + answer + '\'' +
                ", questionFiles='" + questionFiles + '\'' +
                ", choice1Files='" + choice1Files + '\'' +
                ", choice2Files='" + choice2Files + '\'' +
                ", choice3Files='" + choice3Files + '\'' +
                ", choice4Files='" + choice4Files + '\'' +
                ", subjectId=" + subjectId +
                ", topicId=" + topicId +
                ", classId=" + classId +
                '}';
    }
}
