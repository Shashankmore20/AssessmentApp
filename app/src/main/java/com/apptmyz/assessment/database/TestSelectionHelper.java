package com.apptmyz.assessment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TestSelectionHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "my_contest2";

    private static final String TABLE_TEST = "contest2";


    public TestSelectionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
