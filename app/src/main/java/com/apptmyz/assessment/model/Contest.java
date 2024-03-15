package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class Contest implements Serializable {
    public static String CategoryId;
    public static String CategoryName;
    public static List<QuestionModel> list_question = new ArrayList<>();
}
