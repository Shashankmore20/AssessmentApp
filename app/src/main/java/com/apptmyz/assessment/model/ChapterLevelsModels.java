package com.apptmyz.assessment.model;

import java.util.ArrayList;

public class ChapterLevelsModels {
    int chapterNo;
    String chapterName;
    String chapterDescp;
    ArrayList<Integer> difficultyLevel;//1-Beginner, 2-Intermediate, 3-Final



    public int getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(int chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public ArrayList<Integer> getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(ArrayList<Integer> difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

}
