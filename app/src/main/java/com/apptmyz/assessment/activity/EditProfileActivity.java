package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.ChangePasswordModel;
import com.apptmyz.assessment.model.RegisterModel;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.model.User;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    UserDetails userDetails;

    TextView dob_tv;
    TextInputEditText firstName_et, lastName_et, loginName_et, email_et, contact_et;
    AutoCompleteTextView state_sp;
    RelativeLayout dob_lv;
    Button saveBtn;

    Button btn_logout, editBtn;

    ArrayList<StateModel> stateModelArrayList;
    ArrayList<String> stateNamesArrayList;

    Button changePwd;

    LinearLayout profile_layout;
    Spinner class_spinner;

    final Calendar myCalendar = Calendar.getInstance();
    DataSource dataSource;
    ImageView backkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        dataSource = new DataSource(getApplicationContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                setDateText();
            }
        };
        btn_logout = findViewById(R.id.btn_logout);
        editBtn = findViewById(R.id.edit_profile);
        backkBtn = findViewById(R.id.backkBtn);
        backkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.dob_lv:
                        new DatePickerDialog(EditProfileActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                    case R.id.login_tv:
                        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.saveBtn:
                        updateChanges();
                        break;

                    case R.id.changePassword_Btn:
                        Intent intent1 = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                        startActivity(intent1);
                        break;
                }
            }
        };

        firstName_et = findViewById(R.id.firstName_et);
        firstName_et.setText(userDetails.getFirstName());
        lastName_et = findViewById(R.id.lastName_et);
        lastName_et.setText(userDetails.getLastName());
        loginName_et = findViewById(R.id.loginName_et);
        loginName_et.setText(userDetails.getLoginName());
        email_et = findViewById(R.id.email_et);
        email_et.setText(userDetails.getEmailId());
        contact_et = findViewById(R.id.contact_et);
        contact_et.setText(userDetails.getContactNumber());
        dob_tv = findViewById(R.id.dob_tv);
        dob_tv.setText(userDetails.getDob());

        state_sp = findViewById(R.id.state_sp);

        stateModelArrayList = dataSource.states.getStates();
        stateNamesArrayList = new ArrayList<>();
        for (StateModel state : stateModelArrayList) {
            stateNamesArrayList.add(state.getStateName());

        }

        Util.logD("position " + getStatePosition());

        ArrayAdapter<String> arrayAdapter_subjects = new ArrayAdapter<>(this.getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stateNamesArrayList);
        state_sp.setAdapter(arrayAdapter_subjects);
        state_sp.setText(state_sp.getAdapter().getItem(getStatePosition()).toString(), false);

        dob_lv = findViewById(R.id.dob_lv);
        dob_lv.setOnClickListener(listener);

        profile_layout = findViewById(R.id.profile_layout);

        changePwd = findViewById(R.id.changePassword_Btn);

        changePwd.setOnClickListener(listener);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(listener);

    }

    public int getStatePosition() {
        int selectedstate = 0;

        Util.logD("customer state " + userDetails.getStateId());
        for (StateModel state : stateModelArrayList) {

            if (state.getId() == userDetails.getStateId()) {
                return selectedstate;
            }
            selectedstate = selectedstate + 1;
        }
        return selectedstate;
    }

    public void setDateText() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        dob_tv.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void updateChanges() {
        if (firstName_et.getText().toString().equals("")) {
            Toast.makeText(EditProfileActivity.this, "Enter First Name.", Toast.LENGTH_SHORT).show();
        } else if (lastName_et.getText().toString().equals("")) {
            Toast.makeText(EditProfileActivity.this, "Enter Last Name.", Toast.LENGTH_SHORT).show();
        } else if (loginName_et.getText().toString().equals("")) {
            Toast.makeText(EditProfileActivity.this, "Enter Login Name.", Toast.LENGTH_SHORT).show();
        } else if (email_et.getText().toString().equals("")) {
            Toast.makeText(EditProfileActivity.this, "Enter Email Id.", Toast.LENGTH_SHORT).show();
        } else if (!validEmail(email_et.getText().toString())) {
            Toast.makeText(EditProfileActivity.this, "Enter Valid Email Id.", Toast.LENGTH_SHORT).show();
        } else if (contact_et.getText().toString().equals("")) {
            Toast.makeText(EditProfileActivity.this, "Enter Contact Number.", Toast.LENGTH_SHORT).show();
        } else if (contact_et.getText().toString().length() != 10) {
            Toast.makeText(EditProfileActivity.this, "Enter Valid Contact Number.", Toast.LENGTH_SHORT).show();
        } else if (dob_tv.getText().toString().equals("")) {
            Toast.makeText(EditProfileActivity.this, "Select Data Of Birth.", Toast.LENGTH_SHORT).show();
        } else {
            update();
        }
    }

    private void update() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Updating Profile...", true);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("old\n" + userDetails.toString());

        User model = new User();
        model.setUserId(userDetails.getUserId());
        model.setProfilePicture(userDetails.getProfilePicture());
        model.setPassword(userDetails.getPassword());
        model.setRoleId(userDetails.getRoleId());
        model.setGender(userDetails.getGender());
        model.setUserClass(userDetails.getUserClass());

        model.setStateId(userDetails.getStateId());

        model.setFirstName(firstName_et.getText().toString());
        model.setLastName(lastName_et.getText().toString());
        model.setLoginName(loginName_et.getText().toString());
        model.setEmailId(email_et.getText().toString());
        model.setDob(dob_tv.getText().toString());
        model.setContactNumber(contact_et.getText().toString());

        System.out.println("new\n" + model);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            System.out.println("url " + App.EDIT_PROFILE_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.EDIT_PROFILE_URL, new JSONObject(data),

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
                                UserDetails userDetails = UserDetails.getInstance();
                                userDetails.setUserId(model.getUserId());
                                userDetails.setFirstName(model.getFirstName());
                                userDetails.setLastName(model.getLastName());
                                userDetails.setLoginName(model.getLoginName());
                                userDetails.setPassword(model.getPassword());
                                userDetails.setEmailId(model.getEmailId());
                                userDetails.setDob(model.getDob());
                                userDetails.setGender(model.getGender());
                                userDetails.setContactNumber(model.getContactNumber());
                                userDetails.setUserClass(model.getUserClass());
                                userDetails.setRoleId(model.getRoleId());
                                userDetails.setProfilePicture(model.getProfilePicture());
                                userDetails.setStateId(model.getStateId());
                                Gson json = new Gson();
                                dataSource.sharedPreferences.set(Constants.USER_DETAILS, json.toJson(userDetails));
                                Toast.makeText(EditProfileActivity.this, " " + message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finish();
                            } else if (status.equals("failed")) {
                                Toast.makeText(EditProfileActivity.this, " " + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Profile Update failed", Toast.LENGTH_SHORT).show();
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
            Log.e("login: ", "response");
            System.out.println(jsonObjReq);
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getMd5(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}