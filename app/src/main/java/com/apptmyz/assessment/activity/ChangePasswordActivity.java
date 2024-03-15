package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.ChangePasswordModel;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputEditText currentPass_et, newPass_et, confirmNewPass_et;

    Button changePassword_Btn;

    UserDetails userDetails;
    DataSource dataSource;
    ImageView backkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
//        userDetails = UserDetails.getInstance();
        dataSource = new DataSource(ChangePasswordActivity.this);
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        backkBtn = findViewById(R.id.backkBtn);
        backkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        initializeUI();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.changePassword_Btn:
                        if (validatePassword()) {
                            updatePassword();
                        }
                        break;
                }
            }
        };


        currentPass_et = findViewById(R.id.currentPass_et);
        newPass_et = findViewById(R.id.newPass_et);
        confirmNewPass_et = findViewById(R.id.confirmNewPass_et);

        changePassword_Btn = findViewById(R.id.changePassword_Btn);
        changePassword_Btn.setOnClickListener(listener);

    }

    private Boolean validatePassword() {
        if (currentPass_et.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Current Password.", Toast.LENGTH_SHORT).show();
        } else if (newPass_et.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter New Password.", Toast.LENGTH_SHORT).show();
        } else if (confirmNewPass_et.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Confirm New Password.", Toast.LENGTH_SHORT).show();
        } else if (!newPass_et.getText().toString().equals(confirmNewPass_et.getText().toString())) {
            Toast.makeText(getApplicationContext(), "New Password and Confirm new Password does not Match.", Toast.LENGTH_SHORT).show();
        } else if (!checkCurrentPassword()) {
            Toast.makeText(getApplicationContext(), "Enter Correct Current Password.", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }


    private void updatePassword() {
        String cp = currentPass_et.getText().toString();
        String np = newPass_et.getText().toString();
        ProgressDialog dialog = ProgressDialog.show(this, "", "Updating Password...", true);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        ChangePasswordModel model = new ChangePasswordModel();
        model.setUserId(userDetails.getUserId());
        model.setOldPassword(Util.getMd5(cp));
        model.setNewPassword(Util.getMd5(np));
        System.out.println();
        //model.setRoleId(3L);
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            System.out.println("url " + App.CHANGE_PASSWORD_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.CHANGE_PASSWORD_URL, new JSONObject(data),

                    response -> {
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                Toast.makeText(getApplicationContext(), " " + message, Toast.LENGTH_SHORT).show();
                                userDetails.setPassword(model.getNewPassword());
                                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent1);
                            } else if (status.equals("failed")) {
                                Toast.makeText(getApplicationContext(), " " + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Change Password Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                dialog.dismiss();
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
                    headers.put("Authorization", dataSource.sharedPreferences.getValue(Constants.TOKEN));

                    return headers;
                }
            };
            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkCurrentPassword() {
        System.out.println(Util.getMd5(currentPass_et.getText().toString()));
        System.out.println(userDetails.getPassword());
        return Util.getMd5(currentPass_et.getText().toString()).equals(userDetails.getPassword());
    }


    private void initializeUI() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
//        // set toolbar title and back button
//        toolbar.setNavigationOnClickListener(v -> finish());
    }
}