package com.apptmyz.assessment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apptmyz.assessment.model.ScheduleModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Currency;

public class ScheduleHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "my_schedule";
    //table
    private static final String TABLE_NAME = "schedule";
    //set coloumn fields
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DESCP = "descp";
    private static final String TASK_DATE = "taskDate";
    private static final String FROM_TIME = "fromTime";
    private static final String TO_TIME = "toTime";
    private static final String TASK_STATUS = "taskStatus";
    private static final String TASK_ASSIGNED_BY = "taskAssignedBy";

    public ScheduleHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TITLE + " TEXT,"
                        + DESCP + " TEXT,"
                        + TASK_DATE + " TEXT,"
                        + FROM_TIME + " TEXT,"
                        + TO_TIME + " TEXT,"
                        + TASK_STATUS + " TEXT,"
                        + TASK_ASSIGNED_BY + " TEXT" + ")";
        Log.e( "onCreate: ", "create Table query");
        System.out.println(CREATE_TABLE);
        //create table

        db.execSQL(CREATE_TABLE);
//        ScheduleModel scheduleModel = new ScheduleModel("Physics", "Test on Friction", "2022-04-26", LocalTime.parse("04:30"), LocalTime.parse("05:30"), false, "teacher");insertValues(scheduleModel);
//        scheduleModel = new ScheduleModel("Physics", "Test on Friction", "2022-04-27", LocalTime.parse("04:30"), LocalTime.parse("05:30"), false, "teacher");
//        insertValues(scheduleModel);
//        scheduleModel = new ScheduleModel("Physics", "Test on Friction", "2022-04-28", LocalTime.parse("04:30"), LocalTime.parse("05:30"), false, "teacher");
//        insertValues(scheduleModel);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public Boolean insertTeacherTask(ScheduleModel scheduleModel1){
        ScheduleModel scheduleModel = scheduleModel1;
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", scheduleModel.getTitle());
        contentValues.put("descp", scheduleModel.getDescp());
        contentValues.put("taskDate", scheduleModel.getDate());
        contentValues.put("fromtime", scheduleModel.getFromTime().toString());
        contentValues.put("toTime", scheduleModel.getToTime().toString());
        contentValues.put("taskStatus", scheduleModel.getTaskStatus());
        contentValues.put("taskAssignedBy", scheduleModel.getAssignedBy());

        long result = db.insert("schedule", null, contentValues);
        if(result == 1){
            Log.e( "insertValues: ", "false");
            return false;
        } else{
            Log.e( "insertValues: ", "true");
            return true;
        }
    }

    public Boolean insertValues(ScheduleModel scheduleModel1) {
        //ScheduleModel scheduleModel = new ScheduleModel("MathsHW", "Trignometry", "2022:04:23", LocalTime.parse("02:00:00"), LocalTime.parse("03:00:00"), false, "teacher");
        ScheduleModel scheduleModel = scheduleModel1;
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", scheduleModel.getTitle());
        contentValues.put("descp", scheduleModel.getDescp());
        contentValues.put("taskDate", scheduleModel.getDate());
        contentValues.put("fromtime", scheduleModel.getFromTime().toString());
        contentValues.put("toTime", scheduleModel.getToTime().toString());
        contentValues.put("taskStatus", scheduleModel.getTaskStatus());
        contentValues.put("taskAssignedBy", scheduleModel.getAssignedBy());

        long result = db.insert("schedule", null, contentValues);
        if(result == 1){
            Log.e( "insertValues: ", "false");
            return false;
        } else{
            Log.e( "insertValues: ", "true");
            return true;
        }
    }

    public ArrayList<ScheduleModel> getDaySchedule(LocalDate selectedDate) {
        ArrayList<ScheduleModel> models = new ArrayList<>();
        String date = selectedDate.toString();
        db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            int titleIdx = cursor.getColumnIndex(TITLE);
            int descIdx = cursor.getColumnIndex(DESCP);
            int dateIdx = cursor.getColumnIndex(TASK_DATE);
            int fromTimeIdx = cursor.getColumnIndex(FROM_TIME);
            int toTimeIdx = cursor.getColumnIndex(TO_TIME);
            int assignedByIdx = cursor.getColumnIndex(TASK_ASSIGNED_BY);
            int statusIdx = cursor.getColumnIndex(TASK_STATUS);
            do{
                Log.e("getDaySchedule: ", "eee");
                System.out.println(cursor.getString(dateIdx) +"  "+ date);
                if((cursor.getString(dateIdx).equals(date))){
                    ScheduleModel model = new ScheduleModel(
                                                cursor.getString(titleIdx),
                                                cursor.getString(descIdx),
                                                cursor.getString(dateIdx),
                                                LocalTime.parse(cursor.getString(fromTimeIdx)),
                                                LocalTime.parse(cursor.getString(toTimeIdx)),
                                                cursor.getString(statusIdx)=="true"? true:false,
                                                cursor.getString(assignedByIdx));
                    models.add(model);
                }
            }while(cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return models;
    }
}
