package com.apptmyz.assessment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apptmyz.assessment.model.UserTestClass;

import java.util.ArrayList;
import java.util.List;

public class TestDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "my_test";
    //set coloumn fields
    private static final String ID = "id";
    private static final String USER_TEST_ID = "usertestid";
    private static final String TITLE = "title";
    private static final String TEST_DATE = "testdate";
    private static final String START_TIME = "starttime";
    private static final String END_TIME = "endtime";
    private static final String SUBJECT_ID = "subjectid";
    private static final String TOPIC_ID = "topicid";
    private static final String TESTDURATION = "testduration";
    private static final String QUESTIONS = "questions";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    //table
    private static final String TABLE_NAME = "favorite";

    public TestDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table SQL query
        String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + USER_TEST_ID + " TEXT,"
                        + TITLE + " TEXT,"
                        + TEST_DATE + " TEXT,"
                        + START_TIME + " TEXT,"
                        + END_TIME + " TEXT,"
                        + SUBJECT_ID + " TEXT,"
                        + TOPIC_ID + " TEXT,"
                        + TESTDURATION + " TEXT,"
                        + QUESTIONS + " TEXT,"
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";
        //create table

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    //dao logic
    public long insertData(String usertestid, String title, String testdate, String starttime, String questions) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_TEST_ID, usertestid);
        values.put(TITLE, title);
        values.put(TEST_DATE, testdate);
        values.put(START_TIME, starttime);
        values.put(QUESTIONS, questions);
        // insert row
        long id = 0;
        if (CheckIsDataAlreadyInDBorNot(usertestid) == false) {
            id = db.insert(TABLE_NAME, null, values);
        }
        // close db connection
        db.close();
        // return newly inserted row id
        return id;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String favid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + USER_TEST_ID + "='" + favid + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void DeleteBookById(String favid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "DELETE FROM " + TABLE_NAME + " WHERE " + USER_TEST_ID + "='" + favid + "'";

        db.execSQL(Query);
        db.close();
    }

    public void DeleteAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(Query);
        db.close();
    }

    public void getJobByID(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{ID, TITLE, COLUMN_TIMESTAMP},
                ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
//        Job job = new Job(
//                cursor.getInt(cursor.getColumnIndex(ID)),
//                cursor.getString(cursor.getColumnIndex(TITLE)),
//                cursor.getString(cursor.getColumnIndex(HREF)));

        // close the db connection
        //  cursor.close();

        //   return job;
    }

    public List<UserTestClass> getAllData(String activity_type) {
        List<UserTestClass> arrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
                COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserTestClass userTest = new UserTestClass();
                userTest.setUsertestid(cursor.getString(cursor.getColumnIndexOrThrow(USER_TEST_ID)));
                userTest.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                userTest.setStarttime(cursor.getString(cursor.getColumnIndexOrThrow(START_TIME)));
                userTest.setTestdate(cursor.getString(cursor.getColumnIndexOrThrow(TEST_DATE)));
                userTest.setStarttime(cursor.getString(cursor.getColumnIndexOrThrow(START_TIME)));
                userTest.setQuestions(cursor.getString(cursor.getColumnIndexOrThrow(QUESTIONS)));
                //post.setImage_s(cursor.getString(cursor.getColumnIndex(IMAGE)));
                userTest.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));

                arrayList.add(userTest);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();

        // return notes list
        return arrayList;
    }
}