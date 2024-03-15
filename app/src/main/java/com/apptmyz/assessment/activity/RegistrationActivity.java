package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
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
import com.apptmyz.assessment.model.RegisterModel;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.model.User;
import com.apptmyz.assessment.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    DataSource dataSource;

    AutoCompleteTextView state_sp;
    ArrayList<StateModel> stateModelArrayList;
    ArrayList<String> stateNamesArrayList;

    TextView login_tv;
    TextInputEditText firstName_et, lastName_et, loginName_et, email_et, contact_et, password_et, confirmPassword_et, dob_tv;
    RadioGroup gender_rg;
    RelativeLayout dob_lv;
    ImageView dob_lvv, backkBtn;
    RadioButton male_rb, female_rb;
    Button btnRegister;

    Spinner class_spinner;
    ArrayList<String> classArrayList;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dataSource = new DataSource(getApplicationContext());

        backkBtn = findViewById(R.id.backkBtn);

        stateModelArrayList = dataSource.states.getStates();
        stateNamesArrayList = new ArrayList<>();

        for (StateModel state : stateModelArrayList) {
            stateNamesArrayList.add(state.getStateName());
        }

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                setDateText();
            }
        };

        backkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.dob_lv:
                        openDatePickerDialog();
                        break;
                    case R.id.dob_lvv:
                        new DatePickerDialog(RegistrationActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                    case R.id.login_tv:
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.btnRegister:
                        checkValidation();
                        break;
                }
            }
        };

        firstName_et = findViewById(R.id.firstName_et);
        lastName_et = findViewById(R.id.lastName_et);
        loginName_et = findViewById(R.id.loginName_et);
        email_et = findViewById(R.id.email_et);
        contact_et = findViewById(R.id.contact_et);

        state_sp = findViewById(R.id.state_sp);

        dob_tv = findViewById(R.id.dob_tv);
        dob_lv = findViewById(R.id.dob_lv);
        dob_lv.setOnClickListener(listener);
        dob_lvv = findViewById(R.id.dob_lvv);
        dob_lvv.setOnClickListener(view -> openDatePickerDialog());
        password_et = findViewById(R.id.password_et);
        confirmPassword_et = findViewById(R.id.confirmPassword_et);
        gender_rg = findViewById(R.id.gender_rg);
        male_rb = findViewById(R.id.male_rb);
        female_rb = findViewById(R.id.female_rb);
        login_tv = findViewById(R.id.login_tv);
        login_tv.setOnClickListener(listener);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(listener);

        class_spinner = findViewById(R.id.class_spinner);

        classArrayList = new ArrayList<>();
        classArrayList.add("11");
        classArrayList.add("12");

        ArrayAdapter<String> arrayAdapter_subjects = new ArrayAdapter<>(this.getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stateNamesArrayList);
        state_sp.setAdapter(arrayAdapter_subjects);

        ArrayAdapter<String> arrayAdapter_classes = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classArrayList);
        class_spinner.setAdapter(arrayAdapter_classes);

        state_sp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    state_sp.showDropDown();
                }
            }
        });

        state_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dropdown
                state_sp.showDropDown();
            }
        });

    }

    public void openDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate1 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        TextInputEditText tvDateOfBirth = findViewById(R.id.dob_tv);
                        tvDateOfBirth.setText(selectedDate1);
                    }
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        Calendar minDate = Calendar.getInstance();
        minDate.set(1947, Calendar.JANUARY, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // Set the DatePicker maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        datePickerDialog.show();
    }

    public void setDateText() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        dob_tv.setText(dateFormat.format(myCalendar.getTime()));
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

    public void checkValidation() {
        if (firstName_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter First Name.", Toast.LENGTH_SHORT).show();
        } else if (lastName_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter Last Name.", Toast.LENGTH_SHORT).show();
        } else if (loginName_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter Login Name.", Toast.LENGTH_SHORT).show();
        } else if (email_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter Email Id.", Toast.LENGTH_SHORT).show();
        } else if (!validEmail(email_et.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Enter Valid Email Id.", Toast.LENGTH_SHORT).show();
        } else if (contact_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter Contact Number.", Toast.LENGTH_SHORT).show();
        } else if (contact_et.getText().toString().length() != 10) {
            Toast.makeText(RegistrationActivity.this, "Enter Valid Contact Number.", Toast.LENGTH_SHORT).show();
        } else if (dob_tv.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Select Data Of Birth.", Toast.LENGTH_SHORT).show();
        } else if (password_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter Password.", Toast.LENGTH_SHORT).show();
        } else if (confirmPassword_et.getText().toString().equals("")) {
            Toast.makeText(RegistrationActivity.this, "Enter Confirm Password.", Toast.LENGTH_SHORT).show();
        } else if (!checkPassword()) {
            Toast.makeText(RegistrationActivity.this, "Password and Confimation password do not match.", Toast.LENGTH_SHORT).show();
//            TextView checkTwo = findViewById(R.id.checkTwo);
//            checkTwo.setVisibility(View.VISIBLE);
        } else {
            registerUser();
        }
    }

    private void registerUser() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Wait...", true);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        RegisterModel model = new RegisterModel();
        model.setFirstName(firstName_et.getText().toString());
        model.setLastName(lastName_et.getText().toString());
        model.setLoginName(loginName_et.getText().toString());
        model.setEmailId(email_et.getText().toString());
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
//        String date = format.format(dob_tv.getText().toString());
        model.setDob(dob_tv.getText().toString());

        if (male_rb.isChecked())
            model.setGender("M");
        else
            model.setGender("F");

        String selectedState = state_sp.getText().toString();
        int statePosition = stateNamesArrayList.indexOf(selectedState);
        if (statePosition != -1) {
            int stateId = (int) stateModelArrayList.get(statePosition).getId(); // Cast to int

            model.setStateId(stateId);
            // ... remaining code to send the request ...
        } else {
            // Handle the case where no valid state is selected
            Toast.makeText(this, "Please select a valid state", Toast.LENGTH_SHORT).show();
        }
        model.setContactNumber(contact_et.getText().toString());
        model.setUserClass(Integer.parseInt(class_spinner.getSelectedItem().toString()));
        model.setPassword(password_et.getText().toString());
        model.setRoleId(1L);
        model.setProfilePicture("");

        System.out.println();
        //model.setRoleId(3L);
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            Log.e("login: ", "data123");
            System.out.println(data);
            System.out.println("url " + App.REGISTER_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.REGISTER_URL, new JSONObject(data),

                    response -> {
                        dialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            Boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status) {
                                Toast.makeText(RegistrationActivity.this, " " + message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (!status) {
                                Toast.makeText(RegistrationActivity.this, " " + message, Toast.LENGTH_SHORT).show();
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
                Log.e("error", String.valueOf(error));
                error.printStackTrace();
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

    private boolean checkPassword() {
        return password_et.getText().toString().equals(confirmPassword_et.getText().toString());
    }

    private boolean validEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}