package com.apptmyz.assessment.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.apptmyz.assessment.model.ChoicesModel;
import com.apptmyz.assessment.model.ComplexityModel;
import com.apptmyz.assessment.model.SharedPreferenceItem;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.model.SubjectModel;
import com.apptmyz.assessment.model.TestPlayQuestionModel;
import com.apptmyz.assessment.model.TestSummaryModel;
import com.apptmyz.assessment.model.TopicModel;
import com.apptmyz.assessment.model.TopicSummaryModel;
import com.apptmyz.assessment.model.User;
import com.apptmyz.assessment.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSource {

    private SQLiteDatabase database;
    private DataHelper dbHelper;
    private Context mContext;

    public Subjects subjects;
    public Days days;
    public Weeks weeks;
    public Months months;
    public Complexity complexity;
    public Test test;
    public UserDetails userDetails;
    public Topics topics;
    public States states;
    public SubjectSummary subjectSummary;
    public ShardPreferences sharedPreferences;

    public TopicSummary topicSummary;
    public TopicTestsSummary topicTestsSummary;

    public DataSource(Context context) {
        try {
            mContext = context;
            dbHelper = DataHelper.getHelper(context);
            subjects = new Subjects();
            test = new Test();
            userDetails = new UserDetails();
            topics = new Topics();
            states = new States();
            topicSummary = new TopicSummary();
            days = new Days();
            weeks = new Weeks();
            months = new Months();
            topicTestsSummary = new TopicTestsSummary();
            subjectSummary = new SubjectSummary();
            sharedPreferences = new ShardPreferences();
            complexity = new Complexity();


        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.toString());
        } catch (VerifyError e) {
            Log.e("VerifyError", e.toString());
        }
    }

    @SuppressLint("NewApi")
    public void open() throws SQLException {
        if ((database != null && !database.isOpen()) || database == null) {
            try {
                database = dbHelper.getWritableDatabase();
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
        }
    }

    synchronized public void openRead() throws SQLException {
        if ((database != null && !database.isOpen()) || database == null) {
            database = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        if ((database != null && database.isOpen())) {
//            dbHelper.close();
        }
    }

    public class ShardPreferences {

        public void set(String key, String value) {
            open();
            try {
                if (!Util.isValidString(value))
                    value = "";

                if (Util.isValidString(key)) {
                    int id = exists(key);
                    if (id == -1) {
                        create(key, value);
                    } else {
                        update(key, value);
                    }
                }
            } catch (Exception e) {
                Util.logE(e.toString());
            } finally {
                close();
            }
        }

        public void update(String key, String value) {
            open();
            try {
                ContentValues values = new ContentValues();
                values.put(DataHelper.SHARED_PREF_COLUMN_VALUE, value);

                if (database.update(DataHelper.SHARED_PREF_TABLE_NAME, values,
                        DataHelper.SHARED_PREF_COLUMN_KEY + " = '" + key + "'",
                        null) > 0) {
                } else {
                }
            } catch (Exception e) {
                Util.logE(e.toString());
            } finally {
                close();
            }

        }

        public void create(String key, String value) {
            open();
            try {
                ContentValues values = new ContentValues();
                values.put(DataHelper.SHARED_PREF_COLUMN_KEY, key);
                values.put(DataHelper.SHARED_PREF_COLUMN_VALUE, value);

                if (database.insert(DataHelper.SHARED_PREF_TABLE_NAME, null,
                        values) > 0) {
                } else {
                }
            } catch (Exception e) {
                Util.logE(e.toString());
            } finally {
                close();
            }
        }

        public void delete(String key) {
            open();
            try {
                if (database.delete(DataHelper.SHARED_PREF_TABLE_NAME,
                        DataHelper.SHARED_PREF_COLUMN_KEY + " = '" + key + "'",
                        null) > 0) {
                } else {
                }
            } catch (Exception e) {
                Util.logE(e.toString());
            } finally {
                close();
            }
        }

        public List<SharedPreferenceItem> getAll() {
            openRead();
            List<SharedPreferenceItem> items = new ArrayList<SharedPreferenceItem>();
            Cursor cursor = null;
            try {

                cursor = database.query(DataHelper.SHARED_PREF_TABLE_NAME,
                        DataHelper.SHARED_PREF_COLUMNS, null, null, null, null,
                        null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SharedPreferenceItem item = cursorToItem(cursor);
                    items.add(item);
                    cursor.moveToNext();
                }

            } catch (Exception e) {
                Util.logE(e.toString());
                cursor = null;
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }
            return items;
        }

        public String getValue(String key) {
            openRead();

            Cursor cursor = null;
            String value = "";

            try {
                String selectQuery = "SELECT  "
                        + DataHelper.SHARED_PREF_COLUMN_VALUE + " FROM "
                        + DataHelper.SHARED_PREF_TABLE_NAME + " WHERE "
                        + DataHelper.SHARED_PREF_COLUMN_KEY + " = '" + key
                        + "'";

                cursor = database.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    value = cursor.getString(0);
                }

            } catch (Exception e) {
                Util.logE(e.toString());
                cursor = null;
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }
            return value;

        }

        public int exists(String key) {
            openRead();
            int id = -1;
            Cursor cursor = null;
            try {
                String selectQuery = "SELECT  " + DataHelper.SHARED_PREF_KEY_ID
                        + " FROM " + DataHelper.SHARED_PREF_TABLE_NAME
                        + " WHERE " + DataHelper.SHARED_PREF_COLUMN_KEY
                        + " = '" + key + "'";

                cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            } catch (Exception e) {
                Util.logE(e.toString());
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }

            return id;
        }

        private SharedPreferenceItem cursorToItem(Cursor cursor) {
            return new SharedPreferenceItem(cursor.getString(0),
                    cursor.getString(1));
        }
    }

    // public class UserDetails{
//        public void addUserDetails(User details){
//            open();
//            ContentValues values = new ContentValues();
//            values.put(DataHelper.USER_ID, details.getUserid());
//            values.put(DataHelper.USER_FIRST_NAME, details.getFirstname());
//            values.put(DataHelper.USER_LAST_NAME, details.getLastname());
//            values.put(DataHelper.USER_LOGIN_NAME, details.getLoginname());
//            values.put(DataHelper.USER_EMAILID, details.getEmailid());
//            values.put(DataHelper.USER_DOB, details.getDob().toString());
//            values.put(DataHelper.USER_PROFILE_PICTURE, details.getProfilePicture());
//            values.put(DataHelper.USER_GENDER, details.getGender());
//            values.put(DataHelper.USER_CONTACT_NUMBER, details.getContactNumber());
//            values.put(DataHelper.USER_CLASS, details.getUserClass());
//
//            database.insert(DataHelper.USER_TABLE_NAME, null, values);
//        }
//
//        public void clearAll() {
//            open();
//            String query = "DELETE FROM " + DataHelper.USER_TABLE_NAME;
//            database.execSQL(query);
//
//            close();
//        }
//    }

    public class States {
        public void addStates(ArrayList<StateModel> stateModelArrayList) {
            open();

            for (int i = 0; i < stateModelArrayList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(DataHelper.STATE_ID, stateModelArrayList.get(i).getId());
                values.put(DataHelper.STATE_CODE, stateModelArrayList.get(i).getStateCode());
                values.put(DataHelper.STATE_NAME, stateModelArrayList.get(i).getStateName());

                database.insert(DataHelper.STATES_TABLE_NAME, null, values);
            }
            close();
        }

        public ArrayList<StateModel> getStates() {
            openRead();
            ArrayList<StateModel> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.STATES_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex(DataHelper.STATE_ID);
                int stateCodeIdx = cursor.getColumnIndex(DataHelper.STATE_CODE);
                int stateNameIdx = cursor.getColumnIndex(DataHelper.STATE_NAME);
                do {
                    StateModel state = new StateModel();
                    state.setId(cursor.getInt(idIdx));
                    state.setStateCode(cursor.getString(stateCodeIdx));
                    state.setStateName(cursor.getString(stateNameIdx));

                    list.add(state);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.STATES_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

    public class Complexity {

        public void addComplexity(Long id, String name) {
            open();
            ContentValues values = new ContentValues();
            values.put(DataHelper.COMPLEXITY_ID, id);
            values.put(DataHelper.COMPLEXITY_NAME, name);
            database.insert(DataHelper.COMPLEXITY_TABLE_NAME, null, values);

            close();
        }

        public ArrayList<ComplexityModel> getComplexityList() {
            openRead();
            ArrayList<ComplexityModel> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.COMPLEXITY_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex(DataHelper.COMPLEXITY_ID);
                int compNameIdx = cursor.getColumnIndex(DataHelper.COMPLEXITY_NAME);

                do {
                    ComplexityModel complexityModel = new ComplexityModel();
                    complexityModel.setId(cursor.getInt(idIdx));
                    complexityModel.setLevel(cursor.getString(compNameIdx));
                    list.add(complexityModel);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public String getCompName(Long subject_id) {
            openRead();
            String subjectName = "";
            String sql = "SELECT * FROM " + DataHelper.COMPLEXITY_TABLE_NAME + " where " + DataHelper.COMPLEXITY_ID + " = " + subject_id;
            Cursor cursor = database.rawQuery(sql, null);

            cursor.moveToFirst();
            int subjectNameIdx = cursor.getColumnIndex(DataHelper.COMPLEXITY_NAME);

            subjectName = cursor.getString(subjectNameIdx);

            if (cursor != null)
                cursor.close();
            close();

            return subjectName;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.COMPLEXITY_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

    public class Subjects {

        public void addSubjects(Long id, String subjectName, String subjectDescp, String subjectImage) {
            open();
            ContentValues values = new ContentValues();
            values.put(DataHelper.SUBJECT_ID, id);
            values.put(DataHelper.SUBJECT_NAME, subjectName);
            values.put(DataHelper.SUBJECT_DESCRIPTION, subjectDescp);
            values.put(DataHelper.SUBJECT_IMAGE, subjectImage);


            database.insert(DataHelper.SUBJECTS_TABLE_NAME, null, values);

            close();
        }

        public ArrayList<SubjectModel> getSubjects() {
            openRead();
            ArrayList<SubjectModel> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.SUBJECTS_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex(DataHelper.SUBJECT_ID);
                int subjectNameIdx = cursor.getColumnIndex(DataHelper.SUBJECT_NAME);
                int subjectDescpIdx = cursor.getColumnIndex(DataHelper.SUBJECT_DESCRIPTION);
                int subjectFilename = cursor.getColumnIndex(DataHelper.SUBJECT_IMAGE);
                do {
                    SubjectModel subject = new SubjectModel();
                    subject.setId(cursor.getLong(idIdx));
                    subject.setSubjectName(cursor.getString(subjectNameIdx));
                    subject.setSubjectDescription(cursor.getString(subjectDescpIdx));
                    subject.setFileName(cursor.getString(subjectFilename));

                    ArrayList<TopicModel> topicModels = topics.getTopics(subject.getId());
                    subject.setTopics(topicModels);

                    list.add(subject);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public String getSubjectName(Long subject_id) {
            openRead();
            String subjectName = "";
            Cursor cursor = null;
            try {
                String sql = "SELECT * FROM " + DataHelper.SUBJECTS_TABLE_NAME + " WHERE " + DataHelper.SUBJECT_ID + " = " + subject_id;
                cursor = database.rawQuery(sql, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int subjectNameIdx = cursor.getColumnIndex(DataHelper.SUBJECT_NAME);
                    subjectName = cursor.getString(subjectNameIdx);
                }
            } catch (Exception e) {
                // Handle exceptions appropriately (log, throw, etc.)
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }

            return subjectName;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.SUBJECTS_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

    public class Days {

        public void addDaysManually(){
            open();

            try{
                // Execute SQL INSERT statements for each day
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (0, 'Sunday');");
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (1, 'Monday');");
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (2, 'Tuesday');");
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (3, 'Wednesday');");
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (4, 'Thursday');");
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (5, 'Friday');");
                executeInsert("INSERT INTO " + DataHelper.DAYS_TABLE_NAME + " (" + DataHelper.DAY_ID + ", " + DataHelper.DAY_NAME + ") VALUES (6, 'Saturday');");
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void executeInsert(String sql){
            try {
                database.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public ArrayList<String> getDays() {
            openRead();
            ArrayList<String> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.DAYS_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int dayNameIdx = cursor.getColumnIndex(DataHelper.DAY_NAME);
                do {
                    String dayName = cursor.getString(dayNameIdx);
                    list.add(dayName);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            close();

            return list;
        }

        public String getDayName(Long day_id) {
            openRead();
            String dayName = "";
            Cursor cursor = null;
            try {
                String sql = "SELECT * FROM " + DataHelper.DAYS_TABLE_NAME + " WHERE " + DataHelper.DAY_ID + " = " + day_id;
                cursor = database.rawQuery(sql, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int dayNameIdx = cursor.getColumnIndex(DataHelper.DAY_NAME);
                    dayName = cursor.getString(dayNameIdx);
                }
            } catch (Exception e) {
                // Handle exceptions appropriately (log, throw, etc.)
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }

            return dayName;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.DAYS_TABLE_NAME;
            database.execSQL(query);

            close();
        }
    }

    public class Weeks {
        public void addWeeksManually(){
            open();

            try{
                // Execute SQL INSERT statements for each day
                executeInsert("INSERT INTO " + DataHelper.WEEKS_TABLE_NAME + " (" + DataHelper.WEEK_ID + ", " + DataHelper.WEEK_NAME + ") VALUES (1, 'Week 1');");
                executeInsert("INSERT INTO " + DataHelper.WEEKS_TABLE_NAME + " (" + DataHelper.WEEK_ID + ", " + DataHelper.WEEK_NAME + ") VALUES (2, 'Week 2');");
                executeInsert("INSERT INTO " + DataHelper.WEEKS_TABLE_NAME + " (" + DataHelper.WEEK_ID + ", " + DataHelper.WEEK_NAME + ") VALUES (3, 'Week 3');");
                executeInsert("INSERT INTO " + DataHelper.WEEKS_TABLE_NAME + " (" + DataHelper.WEEK_ID + ", " + DataHelper.WEEK_NAME + ") VALUES (4, 'Week 4');");
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void executeInsert(String sql){
            try {
                database.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public ArrayList<String> getWeeks() {
            openRead();
            ArrayList<String> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.WEEKS_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int weekNameIdx = cursor.getColumnIndex(DataHelper.WEEK_NAME);
                do {
                    String weekName = cursor.getString(weekNameIdx);
                    list.add(weekName);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            close();

            return list;
        }
        public String getWeekName(Long week_id) {
            openRead();
            String weekName = "";
            Cursor cursor = null;
            try {
                String sql = "SELECT * FROM " + DataHelper.WEEKS_TABLE_NAME + " WHERE " + DataHelper.WEEK_ID + " = " + week_id;
                cursor = database.rawQuery(sql, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int weekNameIdx = cursor.getColumnIndex(DataHelper.WEEK_NAME);
                    weekName = cursor.getString(weekNameIdx);
                }
            } catch (Exception e) {
                // Handle exceptions appropriately (log, throw, etc.)
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }

            return weekName;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.WEEKS_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

    public class Months {

        public void addMonthsManually(){
            open();

            try{
                // Execute SQL INSERT statements for each day
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (1, 'January');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (2, 'February');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (3, 'March');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (4, 'April');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (5, 'May');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (6, 'June');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (7, 'July');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (8, 'August');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (9, 'September');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (10, 'October');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (11, 'November');");
                executeInsert("INSERT INTO " + DataHelper.MONTHS_TABLE_NAME + " (" + DataHelper.MONTH_ID + ", " + DataHelper.MONTH_NAME + ") VALUES (12, 'December');");

            }catch (Exception e){
                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void executeInsert(String sql){
            try {
                database.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public ArrayList<String> getMonths() {
            openRead();
            ArrayList<String> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.MONTHS_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int monthNameIdx = cursor.getColumnIndex(DataHelper.MONTH_NAME);
                do {
                    String monthName = cursor.getString(monthNameIdx);
                    list.add(monthName);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            close();

            return list;
        }

        public String getMonthName(Long month_id) {
            openRead();
            String monthName = "";
            Cursor cursor = null;
            try {
                String sql = "SELECT * FROM " + DataHelper.MONTHS_TABLE_NAME + " WHERE " + DataHelper.MONTH_ID + " = " + month_id;
                cursor = database.rawQuery(sql, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int monthNameIdx = cursor.getColumnIndex(DataHelper.MONTH_NAME);
                    monthName = cursor.getString(monthNameIdx);
                }
            } catch (Exception e) {
                // Handle exceptions appropriately (log, throw, etc.)
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }

            return monthName;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.MONTHS_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

    public class Topics {

        public void addTopics(Long topicId, Long subjectId, String topicName, String topicDescp) {
            open();
            ContentValues values = new ContentValues();
            values.put(DataHelper.TOPICS_ID, topicId);
            values.put(DataHelper.TOPIC_SUBJECT_ID, subjectId);
            values.put(DataHelper.TOPIC_NAME, topicName);
            values.put(DataHelper.TOPIC_DESCRIPTION, topicDescp);

            database.insert(DataHelper.TOPICS_TABLE_NAME, null, values);

            close();
        }

        public ArrayList<TopicModel> getTopics(Long subjectId) {
            openRead();
            ArrayList<TopicModel> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.TOPICS_TABLE_NAME + " where " + DataHelper.TOPIC_SUBJECT_ID + " = " + subjectId;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int topicIdIdx = cursor.getColumnIndex(DataHelper.TOPICS_ID);
                int subjectIdIdx = cursor.getColumnIndex(DataHelper.TOPIC_SUBJECT_ID);
                int topicNameIdx = cursor.getColumnIndex(DataHelper.TOPIC_NAME);
                int topicDescpIdx = cursor.getColumnIndex(DataHelper.TOPIC_DESCRIPTION);
                do {
                    TopicModel topicModel = new TopicModel();
                    topicModel.setTopicId(cursor.getLong(topicIdIdx));
                    topicModel.setSubjectId(cursor.getLong(subjectIdIdx));
                    topicModel.setTopicName(cursor.getString(topicNameIdx));
                    topicModel.setTopicDescp(cursor.getString(topicDescpIdx));
                    topicModel.setDifficultyLevel(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));
                    list.add(topicModel);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public String getTopicName(Long topic_id) {
            openRead();
            String topicName = "";
            String sql = "SELECT * FROM " + DataHelper.TOPICS_TABLE_NAME + " where " + DataHelper.TOPICS_ID + " = " + topic_id;
            Cursor cursor = database.rawQuery(sql, null);

            cursor.moveToFirst();
            int subjectNameIdx = cursor.getColumnIndex(DataHelper.TOPIC_NAME);

            topicName = cursor.getString(subjectNameIdx);

            if (cursor != null)
                cursor.close();
            close();

            return topicName;
        }

        public void clearAll() {
            open();
            String query = "DELETE FROM " + DataHelper.TOPICS_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

    public class Test {
        public void addQuestions(Long testId, ArrayList<TestPlayQuestionModel> questionModelArrayList) {
            open();
            ContentValues values = new ContentValues();
            for (int i = 0; i < questionModelArrayList.size(); i++) {
                TestPlayQuestionModel questionModel = questionModelArrayList.get(i);

                values.put(DataHelper.QUESTIONS_TEST_ID, testId);
                values.put(DataHelper.QUESTIONS_QUESTION_NO, questionModel.getQuestionNo());
                values.put(DataHelper.QUESTIONS_QUESTION_ID, questionModel.getQuestionId());
                values.put(DataHelper.QUESTIONS_CLASS_NAME, questionModel.getClassName());
                values.put(DataHelper.QUESTIONS_CLASS_DESCRIPTION, questionModel.getClassDescription());
                values.put(DataHelper.QUESTIONS_SUBJECT_NAME, questionModel.getSubjectName());
                values.put(DataHelper.QUESTIONS_SUBJECT_DESCRIPTION, questionModel.getSubjectDescription());
                values.put(DataHelper.QUESTIONS_TOPIC_NAME, questionModel.getTopicName());
                values.put(DataHelper.QUESTIONS_TOPIC_DESCRIPTION, questionModel.getTopicDescription());
                values.put(DataHelper.QUESTIONS_QUESTION, questionModel.getQuestion());
                values.put(DataHelper.QUESTIONS_QUESTION_COMPLEXITY, questionModel.getQuestionComplexity());
                values.put(DataHelper.QUESTIONS_QUESTION_TYPE, questionModel.getQuestionType());


                values.put(DataHelper.QUESTIONS_ANSWER, arrayToString(questionModel.getAnswer()));

                addChoices(questionModel.getQuestionId(), questionModel.getChoices());

                database.insert(DataHelper.TEST_QUESTIONS_TABLE_NAME, null, values);
            }

            close();
        }

        public String arrayToString(ArrayList<Integer> arrayList) {
            String str = "";
            if (arrayList.size() == 1) {
                return arrayList.get(0) + "";
            }
            for (int i = 0; i < arrayList.size(); i++) {
                str += arrayList.get(i) + ",";
            }
            return str;
        }

        public void addChoices(Long question_id, ArrayList<ChoicesModel> choicesModelArrayList) {
            open();
            ContentValues values = new ContentValues();
            for (int i = 0; i < choicesModelArrayList.size(); i++) {
                ChoicesModel choicesModel = choicesModelArrayList.get(i);

                values.put(DataHelper.CHOICES_QUESTION_ID, question_id);
                values.put(DataHelper.CHOICES_CHOICE_ID, choicesModel.getChoiceId());
                values.put(DataHelper.CHOICES_CHOICE, choicesModel.getChoice());
                values.put(DataHelper.CHOICES_CHOICE_TEXT, choicesModel.getChoiceText());

                database.insert(DataHelper.CHOICES_TABLE, null, values);
            }

            close();
        }

        public ArrayList<TestPlayQuestionModel> getQuestions() {
            openRead();
            ArrayList<TestPlayQuestionModel> list = new ArrayList<>();

            String sql = "SELECT * FROM " + DataHelper.TEST_QUESTIONS_TABLE_NAME;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int questionIdIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_QUESTION_ID);
                int questionNoIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_QUESTION_NO);
                int classNameIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_CLASS_NAME);
                int classDescpIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_CLASS_DESCRIPTION);
                int subjectNameIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_SUBJECT_NAME);
                int subjectDescpIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_SUBJECT_DESCRIPTION);
                int topicNameIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_TOPIC_NAME);
                int topicDescpIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_TOPIC_DESCRIPTION);
                int quesionIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_QUESTION);
                int quesionCompIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_QUESTION_COMPLEXITY);
                int answerIdx = cursor.getColumnIndex(DataHelper.QUESTIONS_ANSWER);
                int questionType = cursor.getColumnIndex(DataHelper.QUESTIONS_QUESTION_TYPE);
                do {
                    TestPlayQuestionModel questionModel = new TestPlayQuestionModel();
                    questionModel.setQuestionId(cursor.getLong(questionIdIdx));
                    questionModel.setQuestionNo(cursor.getInt(questionNoIdx));
                    questionModel.setClassName(cursor.getString(classNameIdx));
                    questionModel.setClassDescription(cursor.getString(classDescpIdx));
                    questionModel.setSubjectName(cursor.getString(subjectNameIdx));
                    questionModel.setSubjectDescription(cursor.getString(subjectDescpIdx));
                    questionModel.setTopicName(cursor.getString(topicNameIdx));
                    questionModel.setTopicDescription(cursor.getString(topicDescpIdx));
                    questionModel.setQuestion(cursor.getString(quesionIdx));
                    questionModel.setQuestionComplexity(cursor.getInt(quesionCompIdx));
                    questionModel.setQuestionType(cursor.getInt(questionType));

                    questionModel.setUserAnswer(new ArrayList<>());

                    String ans = cursor.getString(answerIdx);
                    ArrayList<String> answerListTemp = new ArrayList<>(Arrays.asList(ans.split(",")));
                    ArrayList<Integer> answerList = new ArrayList<>();
                    for (int i = 0; i < answerListTemp.size(); i++) {
                        answerList.add(Integer.parseInt(answerListTemp.get(i)));
                    }
                    questionModel.setAnswer(answerList);

                    ArrayList<ChoicesModel> choices = getChoices(questionModel.getQuestionId());
                    questionModel.setChoices(choices);

                    list.add(questionModel);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public ArrayList<ChoicesModel> getChoices(Long questionId) {
            openRead();
            ArrayList<ChoicesModel> list = new ArrayList<>();

            String sql = "SELECT * FROM " + DataHelper.CHOICES_TABLE + " where " + DataHelper.CHOICES_QUESTION_ID + " = " + questionId;
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int choiceIdIdx = cursor.getColumnIndex(DataHelper.CHOICES_CHOICE_ID);
                int choiceIdx = cursor.getColumnIndex(DataHelper.CHOICES_CHOICE);
                int choiceText = cursor.getColumnIndex(DataHelper.CHOICES_CHOICE_TEXT);
                do {
                    ChoicesModel choicesModel = new ChoicesModel();
                    choicesModel.setChoiceId(cursor.getInt(choiceIdIdx));
                    choicesModel.setChoice(cursor.getString(choiceIdx));
                    choicesModel.setChoiceText(cursor.getString(choiceText));

                    list.add(choicesModel);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public void clearQuestions() {
            open();
            String query = "DELETE FROM " + DataHelper.TEST_QUESTIONS_TABLE_NAME;
            database.execSQL(query);

            close();
        }

        public void clearChoices() {
            open();
            String query = "DELETE FROM " + DataHelper.CHOICES_TABLE;
            database.execSQL(query);

            close();
        }

    }

    public class SubjectSummary {

    }

    public class TopicSummary {
        public void addTopicSummary(TopicSummaryModel topicSummaryModel) {
            open();
            ContentValues values = new ContentValues();

            values.put(DataHelper.SUMMARY_TOPIC_ID, topicSummaryModel.getTopicId());
            values.put(DataHelper.SUMMARY_SUBJECT_ID, topicSummaryModel.getSubjectId());
            values.put(DataHelper.SUMMARY_TOTAL_QUESTIONS, topicSummaryModel.getTotalQuestions());
            values.put(DataHelper.SUMMARY_TOTAL_ATTEMPTED_QUESTIONS, topicSummaryModel.getTotalAttemptedTests());
            values.put(DataHelper.SUMMARY_TOTAL_CORRECT_QUESTIONS, topicSummaryModel.getTotalCorrectQuestions());
            values.put(DataHelper.SUMMARY_AVERAGE_PERCENTAGE, topicSummaryModel.getAveragePercentage());
            values.put(DataHelper.SUMMARY_AVERAGE_DURATION, topicSummaryModel.getAverageDuration());
            values.put(DataHelper.SUMMARY_TOTAL_ATTEMTED_TESTS, topicSummaryModel.getTotalAttemptedTests());

            topicTestsSummary.addTopicTestsSummary(topicSummaryModel.getTestSummaryModelArrayList());

            database.insert(DataHelper.TOPIC_SUMMARY_TABLE_NAME, null, values);

        }

        public void clearTopicSummary() {
            open();
            String query = "DELETE FROM " + DataHelper.TOPIC_SUMMARY_TABLE_NAME;
            database.execSQL(query);

            close();

        }

    }

    public class TopicTestsSummary {

        public void addTopicTestsSummary(ArrayList<TestSummaryModel> testSummaryModelArrayList) {
            open();
            ContentValues values = new ContentValues();
            for (int i = 0; i < testSummaryModelArrayList.size(); i++) {
                TestSummaryModel summaryModel = testSummaryModelArrayList.get(i);

                values.put(DataHelper.TEST_ID, summaryModel.getTestId());
                values.put(DataHelper.TEST_DATE, summaryModel.getTestDate());
                values.put(DataHelper.TEST_DURATION, summaryModel.getTestDuration());
                values.put(DataHelper.TEST_START_TIME, summaryModel.getStartTime());
                values.put(DataHelper.TEST_END_TIME, summaryModel.getEndTime());
                values.put(DataHelper.TEST_COMPLEXITY, summaryModel.getTestComplexity());
                values.put(DataHelper.TEST_SUBJECT_ID, summaryModel.getSubjectId());
                values.put(DataHelper.TEST_TOPIC_ID, summaryModel.getTopicId());
                values.put(DataHelper.TEST_TOTAL_QUESTIONS, summaryModel.getTotalQuestions());
                values.put(DataHelper.TEST_TOTAL_CORRECT_ANSWERS, summaryModel.getTotalCorrectAnswers());
                values.put(DataHelper.TEST_TOTAL_WRONG_ANSWERS, summaryModel.getTotalWrongAnswers());
                values.put(DataHelper.TEST_AVG_DURATION, summaryModel.getAvgDuration());

                database.insert(DataHelper.TESTS_SUMMARY_TABLE_NAME, null, values);
            }

        }

        public TopicSummaryModel getTestsSummary(int complexity, int subjectId) {
            openRead();
            TopicSummaryModel topicSummaryModel = new TopicSummaryModel();
            String sql = "SELECT  Count(*), SUM(" + DataHelper.TEST_TOTAL_CORRECT_ANSWERS + "),  SUM(" + DataHelper.TEST_TOTAL_QUESTIONS + "),  AVG(" + DataHelper.TEST_AVG_DURATION + ")   FROM " + DataHelper.TESTS_SUMMARY_TABLE_NAME + " where " + DataHelper.TEST_COMPLEXITY + " = " + complexity + " AND "
                    + DataHelper.TEST_SUBJECT_ID + " = " + subjectId;

            if (complexity == -1) {
                sql = "SELECT  Count(*), SUM(" + DataHelper.TEST_TOTAL_CORRECT_ANSWERS + "),  SUM(" + DataHelper.TEST_TOTAL_QUESTIONS + "),  AVG(" + DataHelper.TEST_AVG_DURATION + ")   FROM " + DataHelper.TESTS_SUMMARY_TABLE_NAME;
            }

            Util.logD(sql);
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                topicSummaryModel.setTotalAttemptedTests(cursor.getInt(0));
                topicSummaryModel.setTotalCorrectQuestions(cursor.getInt(1));
                topicSummaryModel.setTotalQuestions(cursor.getInt(2));
                topicSummaryModel.setAverageDuration(cursor.getLong(3));

            }
            return topicSummaryModel;
        }

        public ArrayList<TestSummaryModel> getTests(int complexity) {
            openRead();
            ArrayList<TestSummaryModel> list = new ArrayList<>();
            String sql = "SELECT * FROM " + DataHelper.TESTS_SUMMARY_TABLE_NAME + " where " + DataHelper.TEST_COMPLEXITY + " = " + complexity;

            if (complexity == -1) {
                sql = "SELECT * FROM " + DataHelper.TESTS_SUMMARY_TABLE_NAME;
            }

            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int testIdIdx = cursor.getColumnIndex(DataHelper.TEST_ID);
                int testDateIdIdx = cursor.getColumnIndex(DataHelper.TEST_DATE);
                int testDurationIdx = cursor.getColumnIndex(DataHelper.TEST_DURATION);
                int testStartTimeIdx = cursor.getColumnIndex(DataHelper.TEST_START_TIME);
                int testEndTimeIdx = cursor.getColumnIndex(DataHelper.TEST_END_TIME);
                int testComplexityIdx = cursor.getColumnIndex(DataHelper.TEST_COMPLEXITY);
                int testSubjectIdIdx = cursor.getColumnIndex(DataHelper.TEST_SUBJECT_ID);
                int testTopicIdIdx = cursor.getColumnIndex(DataHelper.TEST_TOPIC_ID);
                int testTotalQuestionsIdx = cursor.getColumnIndex(DataHelper.TEST_TOTAL_QUESTIONS);
                int testTotalCrtAnsIdx = cursor.getColumnIndex(DataHelper.TEST_TOTAL_CORRECT_ANSWERS);
                int testTotalWrongAnsIdx = cursor.getColumnIndex(DataHelper.TEST_TOTAL_WRONG_ANSWERS);
                int testAvgDurationIdx = cursor.getColumnIndex(DataHelper.TEST_AVG_DURATION);

                do {
                    TestSummaryModel testSummaryModel = new TestSummaryModel();
                    testSummaryModel.setTestId(cursor.getLong(testIdIdx));
                    testSummaryModel.setTestDate(cursor.getString(testDateIdIdx));
                    testSummaryModel.setTestDuration(cursor.getLong(testDurationIdx));
                    testSummaryModel.setStartTime(cursor.getString(testStartTimeIdx));
                    testSummaryModel.setEndTime(cursor.getString(testEndTimeIdx));
                    testSummaryModel.setTestComplexity(cursor.getInt(testComplexityIdx));
                    testSummaryModel.setSubjectId(cursor.getLong(testSubjectIdIdx));
                    testSummaryModel.setTopicId(cursor.getLong(testTopicIdIdx));
                    testSummaryModel.setTotalQuestions(cursor.getInt(testTotalQuestionsIdx));
                    testSummaryModel.setTotalCorrectAnswers(cursor.getInt(testTotalCrtAnsIdx));
                    testSummaryModel.setTotalWrongAnswers(cursor.getInt(testTotalWrongAnsIdx));
                    testSummaryModel.setAvgDuration(cursor.getLong(testAvgDurationIdx));
                    list.add(testSummaryModel);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            close();

            return list;
        }

        public void clearTestsSummary() {
            open();
            String query = "DELETE FROM " + DataHelper.TESTS_SUMMARY_TABLE_NAME;
            database.execSQL(query);

            close();
        }

    }

}
