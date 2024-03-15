package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Keep
public class UserTest implements Serializable {

    private Long usertestid;

    private Date testdate;

    private Long testduration;

    private Date starttime;

    private Date endtime;

    private Integer testcomplexity;

    private Integer subjectid;

    private Integer topicid;

    private List<Question> questions;

    public UserTest() {
    }

    public UserTest(Long usertestid, Date testdate, Long testduration, Date starttime, Date endtime, Integer testcomplexity, Integer subjectid, Integer topicid, List<Question> questions) {
        this.usertestid = usertestid;
        this.testdate = testdate;
        this.testduration = testduration;
        this.starttime = starttime;
        this.endtime = endtime;
        this.testcomplexity = testcomplexity;
        this.subjectid = subjectid;
        this.topicid = topicid;
        this.questions = questions;
    }

    public Long getUsertestid() {
        return usertestid;
    }

    public void setUsertestid(Long usertestid) {
        this.usertestid = usertestid;
    }

    public Date getTestdate() {
        return testdate;
    }

    public void setTestdate(Date testdate) {
        this.testdate = testdate;
    }

    public Long getTestduration() {
        return testduration;
    }

    public void setTestduration(Long testduration) {
        this.testduration = testduration;
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

    public Integer getTestcomplexity() {
        return testcomplexity;
    }

    public void setTestcomplexity(Integer testcomplexity) {
        this.testcomplexity = testcomplexity;
    }

    public Integer getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(Integer subjectid) {
        this.subjectid = subjectid;
    }

    public Integer getTopicid() {
        return topicid;
    }

    public void setTopicid(Integer topicid) {
        this.topicid = topicid;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
