package com.apptmyz.assessment.config;

import android.text.format.DateUtils;

import com.apptmyz.assessment.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    public static String APP_NAME = String.valueOf(R.string.app_name);

    //public static final String LOGIN_URL = "https://j-sme.com/jupiterapi2/api/login/authentication";
    public static final String MENTOR_LIST = "https://j-sme.com/jupiterapi2/api/mentors/approved/page/1/size/50";

    //public static final String BASE_URL = "http://104.237.10.23:8080/assess/";
    //public static final String LOGIN_URL = BASE_URL + "api/auth/signin";
    //public static final String REGISTER_URL = BASE_URL + "api/auth/signup";
    //public static final String GENERATE_NEW_TEST_URL = BASE_URL + "users/tests/new";
    //public static final String UPDATE_TEST_URL = BASE_URL + "users/tests/";
    //public static String lastErrMsg;

    //-------------------------------------------------------------------------------------

    //New URL's

    public static final String BASE_URL = "http://104.237.10.23:8080/assessment-user-test/";

    public static final String LOGIN_URL = BASE_URL + "api/auth/signin";
    public static final String REGISTER_URL = BASE_URL + "api/auth/signup";
    public static final String STATES_URL = BASE_URL + "api/master/states/all";
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "api/auth/change-password";
    public static final String EDIT_PROFILE_URL = BASE_URL + "api/user/edit";
    public static final String GENERATE_NEW_TEST_URL = BASE_URL + "api/questions/filter";
    public static final String SUBJECTS_TOPICS_URL = BASE_URL + "api/master/getSubjectAndTopic/classId/";//TODO

    public static final String COMPLEXITY_MASTER_URL = BASE_URL + "api/master/get/complexity";

    //TARGETS
    public static final String ADD_TARGETS_URL = BASE_URL + "api/target";
    public static final String GET_TARGETS_URL = BASE_URL + "api/target/{type}/user/{userId}";
    public static final String DELETE_TARGETS_URL = BASE_URL + "api/delete/target/{id}";
    //

    //SCHEDULER
    public static final String ADD_SCHEDULE_URL = BASE_URL + "api/scheduler";
    public static final String DELETE_SCHEDULE_URL = BASE_URL + "api/delete/scheduler/{id}";
    public static final String GET_SCHEDULE_URL = BASE_URL + "api/get/scheduler"; //This is a POST Method send userId, StartDate and EndDate
    //

    public static final String DASHBOARD_URL = BASE_URL + "api/reports/dashboard";//TODO

    public static final String TEST_SOLUTIONS = BASE_URL + "api/test/";
    //api/test/141/questions

    public static final String SUBMIT_TEST_URL = BASE_URL + "api/test/submit";

    public static final String TOPIC_SUMMARY_URL = BASE_URL + "api/test/summary";

    //-----------------------------------------------------------------------------------------------
    public static final long AVERAGE_MONTH_IN_MILLIS = DateUtils.DAY_IN_MILLIS * 30;

    public static String getTimeFromLong(long timeInMilliseconds) {
        String mytime = "";
      /*  long minute = (timeInMilliseconds / (1000 * 60)) % 60;
        long hour = (timeInMilliseconds / (1000 * 60 * 60)) % 24;
        mytime = String.format("%02d:%02d", hour, minute);*/

        long hours = timeInMilliseconds / 3600;
        long minutes = (timeInMilliseconds % 3600) / 60;
        long seconds = timeInMilliseconds % 60;
        mytime = String.format("%02d:%02d", minutes, seconds);
        return mytime;
    }

    public static String dateFun(String time) {
        //String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        //String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        //2020-07-07T12:39:00
        // String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getRelationTime(String srcDate) {
        long time = getDateInMillis(srcDate);

        final long now = new Date().getTime();
        final long delta = now - time;
        long resolution;
        if (delta <= DateUtils.MINUTE_IN_MILLIS) {
            resolution = DateUtils.SECOND_IN_MILLIS;
        } else if (delta <= DateUtils.HOUR_IN_MILLIS) {
            resolution = DateUtils.MINUTE_IN_MILLIS;
        } else if (delta <= DateUtils.DAY_IN_MILLIS) {
            resolution = DateUtils.HOUR_IN_MILLIS;
        } else if (delta <= DateUtils.WEEK_IN_MILLIS) {
            resolution = DateUtils.DAY_IN_MILLIS;
        } else if (delta <= AVERAGE_MONTH_IN_MILLIS) {
            return (int) (delta / DateUtils.WEEK_IN_MILLIS) + " weeks(s) ago";
        } else if (delta <= DateUtils.YEAR_IN_MILLIS) {
            return (int) (delta / AVERAGE_MONTH_IN_MILLIS) + " month(s) ago";
        } else {
            return (int) (delta / DateUtils.YEAR_IN_MILLIS) + " year(s) ago";
        }
        return DateUtils.getRelativeTimeSpanString(time, now, resolution).toString();
    }

    public static long getDateInMillis(String srcDate) {
        //String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        //String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat desiredFormat = new SimpleDateFormat(inputPattern);

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            // Log.d("Exception while parsing date. " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }


}
