package com.apptmyz.assessment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apptmyz.assessment.model.ContestListModel;
import com.apptmyz.assessment.model.Question;
import com.apptmyz.assessment.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class ContestDataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "my_contest";

    //contest table and fields
    private static final String TABLE_CONTEST = "contest"; // table contest
    private static final String ID = "id";
    private static final String USER_TEST_ID = "usertestid";
    private static final String TEST_DATE = "testdate";
    private static final String TEST_DURATION = "testduration";
    private static final String START_TEST_TIME = "starttesttime";
    private static final String END_TEST_TIME = "endtesttime";
    private static final String TITLE = "title";
    private static final String STATUS = "status";

    //Question table and field
    private static final String TABLE_QUESTION = "question"; // table question
    private static final String TEST_QUESTION_ID = "testquestionid";
    private static final String DURATION = "duration";
    private static final String START_TIME = "starttime";
    private static final String END_TIME = "endtime";
    private static final String USER_ANSWER = "userAnswer";
    private static final String QUESTION_ID = "questionid";
    private static final String QUESTION = "question";
    private static final String QUESTION_COMPLEXITY = "questionComplexity";
    private static final String CHOICE1 = "choice1";
    private static final String CHOICE2 = "choice2";
    private static final String CHOICE3 = "choice3";
    private static final String CHOICE4 = "choice4";
    private static final String ANSWER = "answer";
    private static final String QUESTION_FILES = "questionFiles";
    private static final String CHOICE1_FILES = "choice1Files";
    private static final String CHOICE2_FILES = "choice2Files";
    private static final String CHOICE3_FILES = "choice3Files";
    private static final String CHOICE4_FILES = "choice4Files";
    private static final String SUBJECT_ID = "subjectid";
    private static final String TOPIC_ID = "topicid";
    private static final String CLASS_ID = "classId";
    private static final String RESULT = "result";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public ContestDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table SQL query
        String CREATE_QUESTION_TABLE =
                "CREATE TABLE " + TABLE_QUESTION + "("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + USER_TEST_ID + " TEXT,"
//                        + TEST_DATE + " TEXT,"
//                        + TEST_DURATION + " TEXT,"
//                        + START_TEST_TIME + " TEXT,"
//                        + END_TEST_TIME + " TEXT,"
//                        + TITLE + " TEXT,"
                        + TEST_QUESTION_ID + " TEXT," // prim
                        + DURATION + " TEXT,"
                        + START_TIME + " TEXT,"
                        + END_TIME + " TEXT,"
                        + USER_ANSWER + " TEXT,"
                        + QUESTION_ID + " TEXT,"
                        + QUESTION_COMPLEXITY + " TEXT,"
                        + CHOICE1 + " TEXT,"
                        + CHOICE2 + " TEXT,"
                        + CHOICE3 + " TEXT,"
                        + CHOICE4 + " TEXT,"
                        + ANSWER + " TEXT,"
                        + QUESTION_FILES + " TEXT,"
                        + CHOICE1_FILES + " TEXT,"
                        + CHOICE2_FILES + " TEXT,"
                        + CHOICE3_FILES + " TEXT,"
                        + CHOICE4_FILES + " TEXT,"
                        + CLASS_ID + " TEXT,"
                        + SUBJECT_ID + " TEXT,"
                        + TOPIC_ID + " TEXT,"
                        + QUESTION + " TEXT,"
                        + RESULT + " TEXT,"
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";

        String CREATE_CONTEST_TABLE =
                "CREATE TABLE " + TABLE_CONTEST + "("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + USER_TEST_ID + " TEXT,"
                        + TEST_DATE + " TEXT,"
                        + TEST_DURATION + " TEXT,"
                        + START_TEST_TIME + " TEXT,"
                        + END_TEST_TIME + " TEXT,"
                        + TITLE + " TEXT,"
                        + STATUS + " TEXT,"
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";
        //create table
        db.execSQL(CREATE_CONTEST_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTEST);
        // Create tables again
        onCreate(db);
    }

    //dao logic
    public long insertQuestion(String usertestid, String testquestionid, String questionid,
                               String question, String duration, String choice1, String choice2,
                               String choice3, String choice4, String answer) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_TEST_ID, usertestid);
        // values.put(START_TIME, starttime);
        values.put(TEST_QUESTION_ID, testquestionid);
        values.put(QUESTION_ID, questionid);
        values.put(QUESTION, question);
        values.put(DURATION, duration);
        // values.put(USER_ANSWER, userAnswer);
        values.put(CHOICE1, choice1);
        values.put(CHOICE2, choice2);
        values.put(CHOICE3, choice3);
        values.put(CHOICE4, choice4);
        values.put(ANSWER, answer);
        //insert row
        long id = db.insert(TABLE_QUESTION, null, values);
        db.close();
        return id;
    }

    public long insertContest(String usertestid, String title, String testdate, String starttime, String endtime) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_TEST_ID, usertestid);
        values.put(TITLE, title);
        values.put(TEST_DATE, testdate);
        values.put(START_TEST_TIME, starttime);
        values.put(END_TEST_TIME, endtime);
        values.put(STATUS, "Pending"); //Complete
        //insert row
        long id = db.insert(TABLE_CONTEST, null, values);
        db.close();
        return id;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String questionID) {  //QUESTION_ID
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_QUESTION + " where " + TEST_QUESTION_ID + "='" + questionID + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void DeleteByQuestionId(String questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "DELETE FROM " + TABLE_QUESTION + " WHERE " + TEST_QUESTION_ID + "='" + questionId + "'";
        db.execSQL(Query);
        db.close();
    }

    public void DeleteAllContest() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "DELETE FROM " + TABLE_QUESTION;
        db.execSQL(Query);
        db.close();
    }

    public void DeleteAllQuestion() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "DELETE FROM " + TABLE_CONTEST;
        db.execSQL(Query);
        db.close();
    }

    public Question getQuestionByID(String testquestionID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION + " WHERE "
                + TEST_QUESTION_ID + " = " + testquestionID;
        //+ COLUMN_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

        Question question = new Question();
        question.setTestquestionid(cursor.getLong(cursor.getColumnIndexOrThrow(TEST_QUESTION_ID)));
        question.setChoice1(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE1)));
        question.setChoice2(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE2)));
        question.setChoice3(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE3)));
        question.setChoice4(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE4)));
        question.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(DURATION)));
        question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(QUESTION)));
        question.setUserAnswer(cursor.getString(cursor.getColumnIndexOrThrow(USER_ANSWER)));
        question.setAnswer(cursor.getString(cursor.getColumnIndexOrThrow(ANSWER)));
        //post.setImage_s(cursor.getString(cursor.getColumnIndex(IMAGE)));
        //question.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
        return question;
    }

    public int updateQuestion(String testquestionid, String userAnswer, String duration) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ANSWER, userAnswer);
        values.put(DURATION, duration);

        // updating row
        return db.update(TABLE_QUESTION, values, TEST_QUESTION_ID + " = ?",
                new String[]{String.valueOf(testquestionid)});
    }

    public int updateContestStatus(String usertestid, String status, String testduration) { //complete
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS, status);
        values.put(TEST_DURATION, testduration);
        //values.put(DURATION, duration);
        // updating row
        return db.update(TABLE_CONTEST, values, USER_TEST_ID + " = ?",
                new String[]{String.valueOf(usertestid)});
    }

    public List<Question> getAllData(String questionID) {
        List<Question> arrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION +
                " where " + TEST_QUESTION_ID + "='" + questionID + "'" +
                " ORDER BY " + QUESTION_ID + " DESC";

        ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                //  question.setT(cursor.getLong(cursor.getColumnIndexOrThrow(USER_TEST_ID)));
                question.setTestquestionid(cursor.getLong(cursor.getColumnIndexOrThrow(TEST_QUESTION_ID)));
                question.setChoice1(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE1)));
                question.setChoice2(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE2)));
                question.setChoice3(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE3)));
                question.setChoice4(cursor.getString(cursor.getColumnIndexOrThrow(CHOICE4)));
                question.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(DURATION)));
                question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(QUESTION)));
                question.setUserAnswer(cursor.getString(cursor.getColumnIndexOrThrow(USER_ANSWER)));
                question.setAnswer(cursor.getString(cursor.getColumnIndexOrThrow(ANSWER)));
                //post.setImage_s(cursor.getString(cursor.getColumnIndex(IMAGE)));
                //question.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                arrayList.add(question);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();

        // return notes list
        return arrayList;
    }

    public List<QuestionModel> getAllQuestionList(String userTestID) {
        List<QuestionModel> arrayList = new ArrayList<>();
        // Select All Query
        /*String selectQuery = "SELECT  * FROM " + TABLE_QUESTION + " where " + TEST_QUESTION_ID +
                "='" + testquestionid + "'" +
                " ORDER BY " + QUESTION_ID + " DESC";*/
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION + " WHERE "
                + USER_TEST_ID + " = " + userTestID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                QuestionModel contestList = new QuestionModel();
                contestList.setTestquestionid(cursor.getString(cursor.getColumnIndexOrThrow(TEST_QUESTION_ID)));
                contestList.setResult(cursor.getInt(cursor.getColumnIndexOrThrow(RESULT)));
                arrayList.add(contestList);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return arrayList;
    }

    public List<ContestListModel> getAllContestList() {
        List<ContestListModel> arrayList = new ArrayList<>();
        // Select All Query
        String status = "Complete";
        String selectQuery = "SELECT  * FROM " + TABLE_CONTEST + " WHERE "
                + STATUS + " ='" + status + "' ORDER BY " +
                COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContestListModel contestList = new ContestListModel();
                contestList.setUsertestid(cursor.getString(cursor.getColumnIndexOrThrow(USER_TEST_ID)));
                contestList.setTestdate(cursor.getString(cursor.getColumnIndexOrThrow(TEST_DATE)));
                contestList.setTestduration(cursor.getString(cursor.getColumnIndexOrThrow(TEST_DURATION)));
                contestList.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                contestList.setStarttesttime(cursor.getString(cursor.getColumnIndexOrThrow(START_TEST_TIME)));
                contestList.setEndtesttime(cursor.getString(cursor.getColumnIndexOrThrow(END_TEST_TIME)));
                contestList.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                arrayList.add(contestList);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();

        // return notes list
        return arrayList;
    }
}