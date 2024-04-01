package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.model.ClassMasterModel;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivityBefore extends AppCompatActivity {

    DataSource dataSource;
    Button teacher, parent;
    TextView login;
    ProgressDialog loadingClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_before);

        dataSource = new DataSource(getApplicationContext());
        teacher = findViewById(R.id.teacher_button);
        parent = findViewById(R.id.parent_button);
        login = findViewById(R.id.alreadyLoginTxtBtn);

//        getClasses();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ParentRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getClasses(){
        loadingClasses = Util.getProgressDialog(RegistrationActivityBefore.this, "Loading Data");

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            System.out.println("url " + App.GET_CLASS_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    App.GET_CLASS_URL, null,

                    response -> {
                        loadingClasses.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
//                                dataSource.states.clearAll();

                                ArrayList<ClassMasterModel> classMasterModels = new ArrayList<>();
                                JSONArray statesJsonArray = new JSONArray(obj.getString("data"));

                                for (int i = 0; i < statesJsonArray.length(); i++) {
                                    JSONObject classes = statesJsonArray.getJSONObject(i);

                                    ClassMasterModel model = new ClassMasterModel();
                                    model.setId(classes.getInt("id"));
                                    model.setClassName(classes.getString("className"));
                                    model.setClassDescription(classes.getString("classDescription"));
                                    model.setActiveFlag(classes.getInt("activeFlag"));

                                    classMasterModels.add(model);
                                }

//                                dataSource.states.addStates(classMasterModels);

                            } else {
                                Toast.makeText(RegistrationActivityBefore.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                loadingClasses.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert")
                            .setMessage("Token Expired. Logging Out !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
//                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));
                    return headers;
                }
            };
            Log.e("Subjects: ", "response");
            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSchool(){

    }

}