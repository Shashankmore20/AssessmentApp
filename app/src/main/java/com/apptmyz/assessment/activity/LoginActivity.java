package com.apptmyz.assessment.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.model.User;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username, password;
    public static final String StudentPrefs = "StudentPrefs";
    private Button submit;
    private ImageView backBtn;
    private DataSource dataSource;
    private TextView register_tv, forgotPassword;
    String TAG = "LOGIN MAIN";
    private AppCompatCheckBox checkbox;

    ProgressDialog loadingStates;
    ProgressDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dataSource = new DataSource(getApplicationContext());

        getStates();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        backBtn = findViewById(R.id.backkBtn);
//        checkbox = (AppCompatCheckBox) findViewById(R.id);
        register_tv = findViewById(R.id.register_tv);
        forgotPassword = findViewById(R.id.forgotPassword);
//        checkbox = findViewById(R.id.checkbox);


        // THIS IS FOR THE STRING OF TERMS AND CONDITIONS A CHANGE IN COLOR FOR THE SPECIFIC TEXT
        TextView textView = findViewById(R.id.TermsAndCondition);
        String termsText = getString(R.string.TermsAndConditions);
        SpannableString spannableString = new SpannableString(termsText);

        String termOfService = "Terms of Services";
        String privacyPolicy = "Privacy Policy";

        int termsStart = termsText.indexOf(termOfService);
        int termsEnd = termsStart + termOfService.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int policyStart = termsText.indexOf(privacyPolicy);
        int policyEnd = policyStart + privacyPolicy.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        //

        register_tv.setOnClickListener(view -> goToRegistrationScreen());

        backBtn.setOnClickListener(view -> finish());

        forgotPassword.setOnClickListener(view -> gotToForgotPasswordScreen());

        submit.setOnClickListener(view -> checkValidation());


    }

    private void getStates() {
        loadingStates = Util.getProgressDialog(LoginActivity.this, "Loading Data");

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            System.out.println("url " + App.STATES_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    App.STATES_URL, null,

                    response -> {
                        loadingStates.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                dataSource.states.clearAll();

                                ArrayList<StateModel> stateModels = new ArrayList<>();
                                JSONArray statesJsonArray = new JSONArray(obj.getString("data"));

                                for (int i = 0; i < statesJsonArray.length(); i++) {
                                    JSONObject state = statesJsonArray.getJSONObject(i);

                                    StateModel model = new StateModel();
                                    model.setId(state.getInt("id"));
                                    model.setStateCode(state.getString("code"));
                                    model.setStateName(state.getString("state"));

                                    stateModels.add(model);
                                }

                                dataSource.states.addStates(stateModels);

                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // method to handle errors.
                loadingStates.dismiss();
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

    private void goToRegistrationScreen() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotToForgotPasswordScreen() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void checkValidation() {
        String user, upass;
        user = Objects.requireNonNull(username.getText()).toString();
        upass = Objects.requireNonNull(password.getText()).toString();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(getApplicationContext(), "Please enter User Name...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(upass)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
        } else {

            login(user, upass);
        }
    }

    private void login(String uname, String upass) {
        loginDialog = Util.getProgressDialog(LoginActivity.this, "Logging in");

        RequestQueue queue = Volley.newRequestQueue(this);

        HashMap model = new HashMap();
        model.put("loginName", uname);
        model.put("password", upass);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            System.out.println("url " + App.LOGIN_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.LOGIN_URL, new JSONObject(data),

                    response -> {
                        loginDialog.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                UserDetails userDetails = UserDetails.getInstance();

                                JSONObject obj1 = new JSONObject(obj.getString("data"));
                                try {
                                    userDetails.setUserId((int) obj1.getLong("userId"));
                                    userDetails.setFirstName(obj1.getString("firstname"));
                                    userDetails.setLastName(obj1.getString("lastname"));
                                    userDetails.setLoginName(obj1.getString("loginname"));
//                                    userDetails.setPassword(obj1.getString("password"));
                                    userDetails.setEmailId(obj1.getString("emailId"));
                                    dataSource.sharedPreferences.set(Constants.TOKEN, token);
                                    dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.FALSE);

                                    //-----------------------------------
                                    String sDate6 = obj1.getString("dob");

                                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date6 = formatter1.parse(sDate6);
                                    System.out.println(sDate6 + "\t" + date6);

                                    String pattern = "dd/MM/yyyy";
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                    String date = simpleDateFormat.format(date6);
                                    System.out.println(date);

                                    //--------------------------------------

                                    userDetails.setDob(date);
                                    userDetails.setGender(obj1.getString("gender"));
                                    userDetails.setContactNumber(obj1.getString("contactNumber"));
//                                    userDetails.setStateId(obj1.getInt("stateId"));
                                    userDetails.setUserClass(obj1.getInt("userClass"));
                                    userDetails.setRoleId(obj1.getInt("roleId"));
                                    userDetails.setProfilePicture(obj1.getString("profilePicture"));
                                    Gson gson = new Gson();
                                    dataSource.sharedPreferences.set(Constants.USER_DETAILS, gson.toJson(userDetails));

                                } catch (NullPointerException e) {
                                }

                                Log.e("login: ", "response");
                                System.out.println(userDetails);

                                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                                finish();

                            } else if (status.equals("false")) {
                                Toast.makeText(LoginActivity.this, " " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }

                    }, error -> {
                Util.logD(error.toString());
                // method to handle errors.
                loginDialog.dismiss();
                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

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
            Log.e("login: ", "response");
            jsonObjReq.setTag(TAG);  // Adding request to request queue
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

    }


}

