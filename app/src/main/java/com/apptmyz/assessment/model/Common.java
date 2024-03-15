package com.apptmyz.assessment.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class Common implements Serializable {
    public static String CategoryId;
    public static String CategoryName;
    public static List<Question> list_question = new ArrayList<>();
    public static final String STR_PUSH = "pushNotification";
}
