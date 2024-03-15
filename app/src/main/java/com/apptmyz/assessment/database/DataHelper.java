package com.apptmyz.assessment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "assessment";

    private static DataHelper instance;

    public DataHelper(Context context) {
        super(context, DataHelper.DATABASE_NAME, null,
                DataHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_SUBJECTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_TEST_QUESTIONS_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHOICES_TABLE);
        //sqLiteDatabase.execSQL(CREATE_USER_DETAILS_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPLEXITY_TABLE);
        sqLiteDatabase.execSQL(DataHelper.SHARED_PREF_DATABASE_CREATE);
        sqLiteDatabase.execSQL(CREATE_TOPICS_TABLE);
        sqLiteDatabase.execSQL(CREATE_STATES_TABLE);
        sqLiteDatabase.execSQL(CREATE_DAYS_TABLE);
        sqLiteDatabase.execSQL(CREATE_WEEKS_TABLE);
        sqLiteDatabase.execSQL(CREATE_MONTHS_TABLE);
        sqLiteDatabase.execSQL(CREATE_TOPIC_SUMMARY_TABLE);
        sqLiteDatabase.execSQL(CREATE_TESTS_SUMMARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static synchronized DataHelper getHelper(Context context) {
        if (instance == null)
            instance = new DataHelper(context);
        return instance;
    }

    /**
     * ************* SHARED_PREFERENCES **************
     **/
    public static final String SHARED_PREF_TABLE_NAME = "sharedpreferences";
    public final static String SHARED_PREF_KEY_ID = "SHARED_PREF_KEY_ID";
    public static final String SHARED_PREF_COLUMN_KEY = "SHARED_KEY";
    public static final String SHARED_PREF_COLUMN_VALUE = "SHARED_VALUE";

    public static final String SHARED_PREF_DATABASE_CREATE = "create table if not exists "
            + SHARED_PREF_TABLE_NAME + "(" + SHARED_PREF_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + SHARED_PREF_COLUMN_KEY
            + " text not null, " + SHARED_PREF_COLUMN_VALUE
            + " text not null);";

    public static final String SHARED_PREF_DATABASE_CREATE_IF_NOT_EXISTS = "create table if not exists "
            + SHARED_PREF_TABLE_NAME + "(" + SHARED_PREF_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + SHARED_PREF_COLUMN_KEY
            + " text not null, " + SHARED_PREF_COLUMN_VALUE
            + " text not null);";

    public static final String[] SHARED_PREF_COLUMNS = {
            SHARED_PREF_COLUMN_KEY, SHARED_PREF_COLUMN_VALUE};

//    // USER_DETAILS--------------------------------------------------------------------
//
//    public static final String USER_TABLE_NAME = "userData";
//    public static final String USER_ID = "user_id";
//    public static final String USER_FIRST_NAME = "user_first_name";
//    public static final String USER_LAST_NAME = "user_last_name";
//    public static final String USER_LOGIN_NAME = "user_login_name";
//    public static final String USER_EMAILID = "user_emailId";
//    public static final String USER_DOB = "user_dob";
//    public static final String USER_GENDER = "user_gender";
//    public static final String USER_PROFILE_PICTURE = "user_profile_picture";
//    public static final String USER_CONTACT_NUMBER = "user_contact_number";
//    public static final String USER_CLASS = "user_class";
//
//    String CREATE_USER_DETAILS_TABLE =
//            "CREATE TABLE " + USER_TABLE_NAME + "("
//                    + USER_ID + " BIGINT,"
//                    + USER_FIRST_NAME + " TEXT,"
//                    + USER_LAST_NAME + " TEXT,"
//                    + USER_LOGIN_NAME + " TEXT,"
//                    + USER_EMAILID + " TEXT,"
//                    + USER_PROFILE_PICTURE + " TEXT,"
//                    + USER_DOB + " DATE,"
//                    + USER_GENDER + " TEXT,"
//                    + USER_CONTACT_NUMBER + " TEXT,"
//                    + USER_CLASS + " INTEGER" + ")";

    //States_table
    public static final String STATES_TABLE_NAME = "states";
    public static final String STATE_ID = "state_id";
    public static final String STATE_CODE = "state_code";
    public static final String STATE_NAME = "state_name";

    String CREATE_STATES_TABLE =
            "CREATE TABLE " + STATES_TABLE_NAME + "("
                    + STATE_ID + " INTEGER,"
                    + STATE_CODE + " TEXT,"
                    + STATE_NAME + " TEXT" + ")";

    //complexity table

    public static final String COMPLEXITY_TABLE_NAME = "COMPLEXITY_TABLE_NAME";
    public static final String COMPLEXITY_ID = "COMPLEXITY_ID";
    public static final String COMPLEXITY_NAME = "COMPLEXITY_NAME";
    String CREATE_COMPLEXITY_TABLE =
            "CREATE TABLE " + COMPLEXITY_TABLE_NAME + "("
                    + COMPLEXITY_ID + " BIGINT,"
                    + COMPLEXITY_NAME + " TEXT" + ")";
    //Subjects_table
    public static final String SUBJECTS_TABLE_NAME = "subjects";
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String SUBJECT_DESCRIPTION = "subject_description";
    public static final String SUBJECT_IMAGE = "SUBJECT_IMAGE";


    String CREATE_SUBJECTS_TABLE =
            "CREATE TABLE " + SUBJECTS_TABLE_NAME + "("
                    + SUBJECT_ID + " BIGINT,"
                    + SUBJECT_NAME + " TEXT,"
                    + SUBJECT_IMAGE + " TEXT,"
                    + SUBJECT_DESCRIPTION + " TEXT" + ")";



    // DAYS_TABLE
    public static final String DAYS_TABLE_NAME = "days";
    public static final String DAY_ID = "day_id";
    public static final String DAY_NAME = "day_name";
    String CREATE_DAYS_TABLE =
            "CREATE TABLE " + DAYS_TABLE_NAME + "("
                    + DAY_ID + " BIGINT,"
                    + DAY_NAME + " TEXT" + ")";

    // WEEKS_TABLE
    public static final String WEEKS_TABLE_NAME = "weeks";
    public static final String WEEK_ID = "week_id";
    public static final String WEEK_NAME = "week_name";
    String CREATE_WEEKS_TABLE =
            "CREATE TABLE " + WEEKS_TABLE_NAME + "("
                    + WEEK_ID + " BIGINT,"
                    + WEEK_NAME + " TEXT" + ")";

    // MONTHS_TABLE
    public static final String MONTHS_TABLE_NAME = "months";
    public static final String MONTH_ID = "month_id";
    public static final String MONTH_NAME = "month_name";
    String CREATE_MONTHS_TABLE =
            "CREATE TABLE " + MONTHS_TABLE_NAME + "("
                    + MONTH_ID + " BIGINT,"
                    + MONTH_NAME + " TEXT" + ")";
    //Topics_table
    public static final String TOPICS_TABLE_NAME = "topics";

    public static final String TOPICS_ID = "topic_id";
    public static final String TOPIC_SUBJECT_ID = "topic_subject_id";
    public static final String TOPIC_NAME = "topic_name";
    public static final String TOPIC_DESCRIPTION = "topic_description";

    String CREATE_TOPICS_TABLE =
            "CREATE TABLE " + TOPICS_TABLE_NAME + "("
                    + TOPICS_ID + " BIGINT,"
                    + TOPIC_SUBJECT_ID + " BIGINT,"
                    + TOPIC_NAME + " TEXT,"
                    + TOPIC_DESCRIPTION + " TEXT" + ")";

    //Topic_Summary_Table
    public static final String TOPIC_SUMMARY_TABLE_NAME = "topic_summary";

    public static final String SUMMARY_TOPIC_ID = "summary_topic_id";
    public static final String SUMMARY_SUBJECT_ID = "summary_subject_id";
    public static final String SUMMARY_TOTAL_QUESTIONS = "summary_total_questions";
    public static final String SUMMARY_TOTAL_ATTEMPTED_QUESTIONS = "summaray_total_attempted_questions";
    public static final String SUMMARY_TOTAL_CORRECT_QUESTIONS = "summary_total_correct_questions";
    public static final String SUMMARY_AVERAGE_PERCENTAGE = "summary_average_percentage";
    public static final String SUMMARY_AVERAGE_DURATION = "summary_average_duration";
    public static final String SUMMARY_TOTAL_ATTEMTED_TESTS = "summary_total_attempted_tests";

    String CREATE_TOPIC_SUMMARY_TABLE =
            "CREATE TABLE " + TOPIC_SUMMARY_TABLE_NAME + "("
                    + SUMMARY_TOPIC_ID + " BIGINT,"
                    + SUMMARY_SUBJECT_ID + " BIGINT,"
                    + SUMMARY_TOTAL_QUESTIONS + " INTEGER,"
                    + SUMMARY_TOTAL_ATTEMPTED_QUESTIONS + " INTEGER,"
                    + SUMMARY_TOTAL_CORRECT_QUESTIONS + " INTEGER,"
                    + SUMMARY_AVERAGE_PERCENTAGE + " DOUBLE,"
                    + SUMMARY_AVERAGE_DURATION + " BIGINT,"
                    + SUMMARY_TOTAL_ATTEMTED_TESTS + " INTEGER" + ")";

    //Tests_table
    public static String TESTS_SUMMARY_TABLE_NAME = "tests";

    public static final String TEST_ID = "test_id";
    public static final String TEST_DATE = "test_date";
    public static final String TEST_DURATION = "test_duration";
    public static final String TEST_START_TIME = "test_start_time";
    public static final String TEST_END_TIME = "test_end_time";
    public static final String TEST_COMPLEXITY = "test_complexity";
    public static final String TEST_SUBJECT_ID = "test_subject_id";
    public static final String TEST_TOPIC_ID = "test_topic_id";
    public static final String TEST_TOTAL_QUESTIONS = "test_total_questions";
    public static final String TEST_TOTAL_CORRECT_ANSWERS = "test_total_correct_answers";
    public static final String TEST_TOTAL_WRONG_ANSWERS = "test_total_wrong_answers";
    public static final String TEST_AVG_DURATION = "test_avg_duration";

    String CREATE_TESTS_SUMMARY_TABLE =
            "CREATE TABLE " + TESTS_SUMMARY_TABLE_NAME + "("
                    + TEST_ID + " BIGINT,"
                    + TEST_DATE + " INTEGER,"
                    + TEST_DURATION + " BIGINT,"
                    + TEST_START_TIME + " TEXT,"
                    + TEST_END_TIME + " TEXT,"
                    + TEST_COMPLEXITY + " INTEGER,"
                    + TEST_SUBJECT_ID + " BIGINT,"
                    + TEST_TOPIC_ID + " BIGINT,"
                    + TEST_TOTAL_QUESTIONS + " INTEGER,"
                    + TEST_TOTAL_CORRECT_ANSWERS + " INTEGER,"
                    + TEST_TOTAL_WRONG_ANSWERS + " INTEGER,"
                    + TEST_AVG_DURATION + " BIGINT" + ")";


    //Questions_table-----------------------------------------------------------------------

    public static final String TEST_QUESTIONS_TABLE_NAME = "test_questions";

    public static final String QUESTIONS_TEST_ID = "questions_test_no";
    public static final String QUESTIONS_QUESTION_NO = "questions_no";
    public static final String QUESTIONS_QUESTION_ID = "questions_question_id";
    public static final String QUESTIONS_CLASS_NAME = "questions_class_name";
    public static final String QUESTIONS_CLASS_DESCRIPTION = "questions_class_description";
    public static final String QUESTIONS_SUBJECT_NAME = "questions_subject_name";
    public static final String QUESTIONS_SUBJECT_DESCRIPTION = "questions_subject_description";
    public static final String QUESTIONS_TOPIC_NAME = "questions_topic_name";
    public static final String QUESTIONS_TOPIC_DESCRIPTION = "questions_topic_description";
    public static final String QUESTIONS_QUESTION = "questions_questions";
    public static final String QUESTIONS_QUESTION_FILES = "questions_question_files";
    public static final String QUESTIONS_QUESTION_COMPLEXITY = "question_complexity";
    public static final String QUESTIONS_QUESTION_TYPE = "question_type";
    public static final String QUESTIONS_ANSWER = "question_answer";

    String CREATE_TEST_QUESTIONS_TABLE =
            "CREATE TABLE " + TEST_QUESTIONS_TABLE_NAME + "("
                    + QUESTIONS_TEST_ID + " BIGINT,"
                    + QUESTIONS_QUESTION_NO + " INTEGER,"
                    + QUESTIONS_QUESTION_ID + " INTEGER,"
                    + QUESTIONS_CLASS_NAME + " TEXT,"
                    + QUESTIONS_CLASS_DESCRIPTION + " TEXT,"
                    + QUESTIONS_SUBJECT_NAME + " TEXT,"
                    + QUESTIONS_SUBJECT_DESCRIPTION + " TEXT,"
                    + QUESTIONS_TOPIC_NAME + " TEXT,"
                    + QUESTIONS_TOPIC_DESCRIPTION + " TEXT,"
                    + QUESTIONS_QUESTION + " TEXT,"
                    + QUESTIONS_QUESTION_COMPLEXITY + " INTEGER,"
                    + QUESTIONS_ANSWER + " TEXT,"
                    + QUESTIONS_QUESTION_TYPE + " INTEGER" + ")";


    //Question_choices_table-----------------------------------------------------------------

    public static final String CHOICES_TABLE = "choices_table";

    public static final String CHOICES_QUESTION_ID = "choices_questionId";
    public static final String CHOICES_CHOICE_ID = "choices_choiceId";
    public static final String CHOICES_CHOICE = "choices_choice";
    public static final String CHOICES_CHOICE_TEXT = "choices_choice_text";
    public static final String CHOICES_CHOICE_FILES = "choices_choice_files";

    String CREATE_CHOICES_TABLE =
            "CREATE TABLE " + CHOICES_TABLE + "("
                    + CHOICES_QUESTION_ID + " BIGINT,"
                    + CHOICES_CHOICE_ID + " BIGINT,"
                    + CHOICES_CHOICE + " TEXT,"
                    + CHOICES_CHOICE_TEXT + " TEXT" + ")";
}
