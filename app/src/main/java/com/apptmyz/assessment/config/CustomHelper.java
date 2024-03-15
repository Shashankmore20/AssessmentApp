package com.apptmyz.assessment.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.activity.PlayActivity;
import com.apptmyz.assessment.database.ContestDataBaseHelper;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.model.QuestionModel;
import com.apptmyz.assessment.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomHelper {
    private Context context;
    DataSharedPref dataSharedPref;
    ContestDataBaseHelper contestDataBaseHelper;
    List<QuestionModel> arraylist = new ArrayList<>();
    DataSource dataSource;

    public CustomHelper(Context context) {
        this.context = context;
        dataSharedPref = new DataSharedPref(context);
        dataSource = new DataSource(context);
    }

    public void getQuestion() {
        contestDataBaseHelper = new ContestDataBaseHelper(context);
        ProgressDialog dialog = ProgressDialog.show(context, "", "loading...", true);
        dialog.show();
        String loginUrl = App.GENERATE_NEW_TEST_URL + "?className=10&subjectName=Mathematics&count=5";
        System.out.println(loginUrl);
        StringRequest request = new StringRequest(Request.Method.GET, loginUrl, response -> {
            dialog.dismiss();
            if (!response.equals(null)) {
                System.out.println(response);
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    String status = obj.getString("status");
                    String message = obj.getString("message");
                    String token = obj.getString("token");

                    if (status.equals("true")) {
                        //JSONObject obj1 = new JSONObject(obj.getString("data"));

                        JSONObject obj1 = new JSONObject(obj.getString("data"));
                        Long usertestid = obj1.getLong("usertestid");
                        String testdate = obj1.getString("testdate");
                        String starttime = obj1.getString("starttime");
                        String endtime = obj1.getString("endtime");

                        JSONArray jsonArray = obj1.getJSONArray("questions");
                        contestDataBaseHelper.insertContest(String.valueOf(usertestid), "MYUSERID" + usertestid, testdate, starttime, endtime);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String questionid = jsonObject.getString("questionid");
                            contestDataBaseHelper.insertQuestion(String.valueOf(usertestid), jsonObject.getString("testquestionid"),
                                    questionid, jsonObject.getString("question"), jsonObject.getString("duration"),
                                    jsonObject.getString("choice1"), jsonObject.getString("choice2"),
                                    jsonObject.getString("choice3"), jsonObject.getString("choice4"),
                                    jsonObject.getString("answer"));
                            //Log.d("First Names", fname);

                            String testquestionid = jsonObject.getString("testquestionid");

                            //question = contestDataBaseHelper.getQuestionByID(testquestionid);
                           // Log.d("questionid", questionid);
                            QuestionModel question = new QuestionModel(testquestionid);
                            arraylist.add(question);
                        }

                        Intent intent = new Intent(context, PlayActivity.class);
                        intent.putExtra("testquestionid_list", (Serializable) arraylist);
                        intent.putExtra("response", response);
                        intent.putExtra("usertestid", String.valueOf(usertestid));
                        //Log.d("SERVER_RESPONSE=", String.valueOf(response));
                        context.startActivity(intent);
                        //finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("Your Array Response", "Data Null");
            }
        }, error -> {
            // method to handle errors.
            dialog.dismiss();
            Toast.makeText(context, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTcmVlIiwiaWF0IjoxNjQyMDU3MTQwLCJleHAiOjE2NDIxNDM1NDB9.llq6XnKyqFDZVno8QPpRapLbIfk4rZKNjtppYVr_mONO5uFU1YlH5l3PcdZQL2piwdFrNVT5p5jdYxIa4V5xqA";
                String token = dataSharedPref.getSession("token");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
