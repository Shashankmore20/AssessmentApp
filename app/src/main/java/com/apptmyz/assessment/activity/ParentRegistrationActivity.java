package com.apptmyz.assessment.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.adapter.ChildAdapter;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.model.ClassMasterModel;
import com.apptmyz.assessment.model.CurriculumModel;
import com.apptmyz.assessment.model.RegisterModel;
import com.apptmyz.assessment.model.SchoolMasterModel;
import com.apptmyz.assessment.model.StateModel;
import com.apptmyz.assessment.model.TopicModel;
import com.apptmyz.assessment.model.User;
import com.apptmyz.assessment.model.UserModelNew;
import com.apptmyz.assessment.utils.Constants;
import com.apptmyz.assessment.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParentRegistrationActivity extends AppCompatActivity implements ChildAdapter.OnChildRemovedListener {

    DataSource dataSource;
    AutoCompleteTextView state_sp;
    ArrayList<StateModel> stateModelArrayList;
    ArrayList<String> stateNamesArrayList;
    ArrayList<ClassMasterModel> classMasterModelArrayList;
    ArrayList<String> classNameArrayList;
    ArrayList<SchoolMasterModel> schoolMasterModelArrayList;
    ArrayList<String> schoolNameList;
    ArrayList<CurriculumModel> curriculumModelArrayList;
    ArrayList<String> curriculumNameList;
    TextView login_tv, tvNodataAvailable;
    TextInputEditText firstName_et, lastName_et, email_et, contact_et;
    RadioGroup gender_rg;
    ImageView backkBtn;
    RadioButton male_rb, female_rb;
    String childDate = "null";
    Integer childClass = null;
    Integer childSchool = null;
    Integer childCurriculum = null;
    String TAG = "CHECK VERIFICATION";

    RecyclerView rvChildDetails;
    Button btnRegister, btnAddChild, UploadProfilePic;
    UserModelNew parentUserModel = new UserModelNew();

    private ArrayAdapter<String> arrayAdapter_class;
    private ArrayAdapter<String> arrayAdapter_school;
    private ArrayAdapter<String> arrayAdapter_curriculum;

    ProgressDialog loadingClasses;
    private AlertDialog dialog;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), RegistrationActivityBefore.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_registration);

        //Initializes the DataSource
        dataSource = new DataSource(getApplicationContext());

        //Sets the Functionality of the Back Button
        backkBtn = findViewById(R.id.backkBtn);
        backkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivityBefore.class);
                startActivity(intent);
                finish();
            }
        });

        //Initializer
        stateModelArrayList = dataSource.states.getStates();
        stateNamesArrayList = new ArrayList<>();
        for (StateModel state : stateModelArrayList) {
            stateNamesArrayList.add(state.getStateName());
        }

        //Initializer
        classNameArrayList = new ArrayList<>();
        schoolNameList = new ArrayList<>();
        curriculumNameList = new ArrayList<>();

        //Callers of the Methods
        getClasses();
        getSchool();

        // Sets the functionalities to the Buttons
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
//                    case R.id.dob_lv:
//                        openDatePickerDialog();
//                        break;
                    case R.id.login_tv:
                        Intent intent = new Intent(ParentRegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.btnRegister:
                        checkValidation();
                        break;
                    case R.id.btnAddChild:
                        addChild();
                        break;
                    case R.id.btnUploadProfilePic:
                        UploadParentProfile();
                        break;
                }
            }
        };
        firstName_et = findViewById(R.id.firstName_et);
        lastName_et = findViewById(R.id.lastName_et);
        email_et = findViewById(R.id.email_et);
        email_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                Log.d("email", email);
                if (validEmail(email)) {
                    Log.d("email", email + "inside");
                    checkEmail(email);
                }
            }
        });
        contact_et = findViewById(R.id.contact_et);
        state_sp = findViewById(R.id.state_sp);
        gender_rg = findViewById(R.id.gender_rg);
        male_rb = findViewById(R.id.male_rb);
        female_rb = findViewById(R.id.female_rb);
        login_tv = findViewById(R.id.login_tv);
        login_tv.setOnClickListener(listener);
        tvNodataAvailable = findViewById(R.id.tvNodataAvailable);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(listener);
        btnAddChild = findViewById(R.id.btnAddChild);
        btnAddChild.setOnClickListener(listener);
        UploadProfilePic = findViewById(R.id.btnUploadProfilePic);
        UploadProfilePic.setOnClickListener(listener);
        rvChildDetails = findViewById(R.id.rvChildDetails);

        // Adapter to set the AutoCompleteTextView of the States list
        ArrayAdapter<String> arrayAdapter_subjects = new ArrayAdapter<>(this.getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stateNamesArrayList);
        state_sp.setAdapter(arrayAdapter_subjects);
        state_sp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    state_sp.showDropDown();
                }
            }
        });
        //OnClick Function for the state list
        state_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dropdown
                state_sp.showDropDown();
            }
        });
    }

    //Method to upload Parent Profile Pic
    private void UploadParentProfile() {
    }

    //Opens Date Picker for the Child
    public void openChildDatePicketDialog() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate2 = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        TextInputEditText tvDateOfBirth1 = dialog.findViewById(R.id.dob_tvv);
                        tvDateOfBirth1.setText(selectedDate2);
                        childDate = selectedDate2;
                        Log.d("DateOfBirth", childDate);
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

    //Dialog Opens up on the click of add child button. This is the functionality for it
    public void addChild() {
        if (getApplicationContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedCornersDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.add_child_dialog, null);
            builder.setView(dialogView);

            LinearLayout others_linear = dialogView.findViewById(R.id.other_linear);

            ImageView close = dialogView.findViewById(R.id.close);
            ImageView dob_lvv = dialogView.findViewById(R.id.dob_lvvv);

            TextInputEditText firstName = dialogView.findViewById(R.id.firstName_et);
            TextInputEditText lastName = dialogView.findViewById(R.id.lastName_et);
            TextInputEditText loginName = dialogView.findViewById(R.id.loginName_et);
            RadioButton male_rb = dialogView.findViewById(R.id.male_rb);
            RadioButton female_rb = dialogView.findViewById(R.id.female_rb);
            TextInputEditText schoolName = dialogView.findViewById(R.id.school_Name);
            TextInputEditText schoolCode = dialogView.findViewById(R.id.school_Code);
            AutoCompleteTextView class_spinner = dialogView.findViewById(R.id.class_spinner);
            AutoCompleteTextView school_spinner = dialogView.findViewById(R.id.school_spinner);
            AutoCompleteTextView curriculum_spinner = dialogView.findViewById(R.id.curriculum_spinner);
            Button submit = dialogView.findViewById(R.id.AddChildDialogBtn);
            Button uploadProfilePic = dialogView.findViewById(R.id.add_profile_pic_btn);

            dialog = builder.create();
            dialog.show();

            dob_lvv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openChildDatePicketDialog();
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            //For Classes
            if (classMasterModelArrayList != null) {
                for (ClassMasterModel classMasterModel : classMasterModelArrayList) {
                    Log.d("ClassName", classMasterModel.getClassName());
                }
            } else {
                Log.d("ClassName", "Class list is null");
            }
            arrayAdapter_class = new ArrayAdapter<>(this.getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classNameArrayList);
            class_spinner.setAdapter(arrayAdapter_class);
            class_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedClass = (String) adapterView.getItemAtPosition(position);

                    int selectedClassId = -1;
                    for (ClassMasterModel classMasterModel : classMasterModelArrayList) {
                        if (classMasterModel.getClassName().equals(selectedClass)) {
                            selectedClassId = classMasterModel.getId();
                            childClass = selectedClassId;
                            break;
                        }
                    }
                    // Log the selected class name and ID
                    Log.d("Selected class", "Name: " + selectedClass + ", ID: " + selectedClassId);
                }
            });
            class_spinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    class_spinner.showDropDown();
                }
            });
            class_spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        class_spinner.showDropDown();
                    }
                }
            });

            //For Schools
            if (schoolMasterModelArrayList != null) {
                for (SchoolMasterModel schoolMasterModel : schoolMasterModelArrayList) {
                    Log.d("SchoolName", schoolMasterModel.getSchoolName());
                }
            } else {
                Log.d("SchoolName", "School list is null");
            }
            arrayAdapter_school = new ArrayAdapter<>(this.getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, schoolNameList);
            school_spinner.setAdapter(arrayAdapter_school);
            school_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedSchool = (String) adapterView.getItemAtPosition(position);

                    int selectedSchoolId = -1;
                    if (selectedSchool.equals("Others")) {
                        selectedSchoolId = 0;
                        others_linear.setVisibility(View.VISIBLE);
                    } else {
                        others_linear.setVisibility(View.GONE);
                        for (SchoolMasterModel schoolMasterModel : schoolMasterModelArrayList) {
                            if (schoolMasterModel.getSchoolName().equals(selectedSchool)) {
                                selectedSchoolId = schoolMasterModel.getId();
                                break;
                            }
                        }
                    }
                    childSchool = selectedSchoolId;
                    // Log the selected class name and ID
                    Log.d("Selected class", "Name: " + selectedSchool + ", ID: " + selectedSchoolId);

                    getCurriculum(selectedSchoolId);
                }
            });
            school_spinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    school_spinner.showDropDown();
                }
            });
            school_spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        school_spinner.showDropDown();
                    }
                }
            });

            // For Curriculums
            if (curriculumModelArrayList != null) {
                for (CurriculumModel curriculumModel : curriculumModelArrayList) {
                    Log.d("CurriculumName", curriculumModel.getName());
                }
            } else {
                Log.d("curriculumName", "Curriculum list is null");
            }
            arrayAdapter_curriculum = new ArrayAdapter<>(this.getApplicationContext().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, curriculumNameList);
            curriculum_spinner.setAdapter(arrayAdapter_curriculum);
            curriculum_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedCurriculum = (String) adapterView.getItemAtPosition(position);

                    int selectedCurriculumId = -1;
                    for (CurriculumModel curriculumModel : curriculumModelArrayList) {
                        if (curriculumModel.getName().equals(selectedCurriculum)) {
                            selectedCurriculumId = curriculumModel.getId();
                            break;
                        }
                    }
                    // Log the selected class name and ID
                    childCurriculum = selectedCurriculumId;
                    Log.d("Selected Curriculum", "Name: " + selectedCurriculum + ", ID: " + selectedCurriculumId);

                }
            });
            curriculum_spinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curriculum_spinner.showDropDown();
                }
            });
            curriculum_spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        curriculum_spinner.showDropDown();
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ArrayList<UserModelNew> childrenList = new ArrayList<>();

                    // Check if all fields are filled
                    if (TextUtils.isEmpty(firstName.getText())) {
                        Toast.makeText(getApplicationContext(), "Please enter first name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(lastName.getText())) {
                        Toast.makeText(getApplicationContext(), "Please enter last name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(loginName.getText())) {
                        Toast.makeText(getApplicationContext(), "Please enter login name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!male_rb.isChecked() && !female_rb.isChecked()) {
                        Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    if (TextUtils.isEmpty(schoolName.getText())) {
//                        Toast.makeText(getApplicationContext(), "Please enter school name", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    if (TextUtils.isEmpty(schoolCode.getText())) {
//                        Toast.makeText(getApplicationContext(), "Please enter school code", Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    if (childClass == null) {
                        Toast.makeText(getApplicationContext(), "Please select class", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (childSchool == null) {
                        Toast.makeText(getApplicationContext(), "Please select school", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (childCurriculum == null) {
                        Toast.makeText(getApplicationContext(), "Please select curriculum", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check if parentUserModel has a list of children already
                    ArrayList<UserModelNew> childrenList = parentUserModel.getChildrenList();
                    if (childrenList == null) {
                        childrenList = new ArrayList<>();
                    }

                    UserModelNew child = new UserModelNew();

                    //Set Child Details
                    child.setFirstName(firstName.getText().toString());
                    child.setLastName(lastName.getText().toString());
                    child.setLoginName(loginName.getText().toString());
                    child.setDob(childDate);
                    if (male_rb.isChecked()) {
                        child.setGender("M");
                    } else if (female_rb.isChecked()) {
                        child.setGender("F");
                    } else {
                        Toast.makeText(getApplicationContext(), "Select the gender", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    child.setUserClass(childClass);
                    child.setRoleId(2L);
                    if (childSchool == 0) {
                        child.setSchoolId(null);
                    } else {
                        child.setSchoolId(childSchool);
                    }
                    child.setCurriculumId(childCurriculum);
                    String schoolNameText = schoolName.getText().toString();
                    child.setSchoolName(schoolNameText.isEmpty() ? null : schoolNameText);
                    String schoolCodeText = schoolCode.getText().toString();
                    child.setSchoolCode(schoolCodeText.isEmpty() ? null : schoolCodeText);

                    childrenList.add(child);

                    parentUserModel.setChildrenList(childrenList);

                    Log.d("UserModel", parentUserModel.toString());
                    dialog.dismiss();

                    if (!parentUserModel.getChildrenList().isEmpty()) {
                        ChildAdapter adapter = new ChildAdapter(parentUserModel.getChildrenList());
                        rvChildDetails.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        rvChildDetails.setAdapter(adapter);
                        adapter.setOnChildRemovedListener(ParentRegistrationActivity.this);
                        tvNodataAvailable.setVisibility(View.GONE);
                    } else {
                        tvNodataAvailable.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    //Function to validate EmailId
    private boolean validEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Function to call the API to check if the E-mail is already registered
    private void checkEmail(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);

        HashMap model = new HashMap();
        model.put("emailId", email);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            System.out.println("url" + App.CHECK_EMAIL_VERIFICATION);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, App.CHECK_EMAIL_VERIFICATION, new JSONObject(data),

                    response -> {
                        try {
                            Log.d("Response=>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");

                            if (status.equals("true")) {
//                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            } else if (status.equals("false")) {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                email_et.getText().clear();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                Util.logD(error.toString());
                // method to handle errors.

                if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    Toast.makeText(this, "Email Verification Failed", Toast.LENGTH_SHORT).show();
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
            Log.e("Check Email: ", "response");
            jsonObjReq.setTag(TAG);  // Adding request to request queue
            queue.add(jsonObjReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Function to call the API to get the Classes
    private void getClasses() {
        loadingClasses = Util.getProgressDialog(ParentRegistrationActivity.this, "Loading Data");

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
                                classMasterModelArrayList = classMasterModels;

                                for (int i = 0; i < statesJsonArray.length(); i++) {
                                    JSONObject classes = statesJsonArray.getJSONObject(i);

                                    ClassMasterModel model = new ClassMasterModel();
                                    model.setId(classes.getInt("id"));
                                    model.setClassName(classes.getString("className"));
                                    model.setClassDescription(classes.getString("classDescription"));
                                    model.setActiveFlag(classes.getInt("activeFlag"));

                                    classMasterModels.add(model);
                                    Log.d("ClassName", model.getClassName());
                                }

                                for (ClassMasterModel topic : classMasterModelArrayList) {
                                    classNameArrayList.add(topic.getClassName());
//                                    classNameArrayList.add(topic.getId().toString());
                                }
//                                dataSource.states.addStates(classMasterModels);

                            } else {
                                Toast.makeText(ParentRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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

    //Function to call the API to get the Schools
    private void getSchool() {
        loadingClasses = Util.getProgressDialog(ParentRegistrationActivity.this, "Loading Data");

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            System.out.println("url " + App.GET_SCHOOL_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    App.GET_SCHOOL_URL, null,

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

                                ArrayList<SchoolMasterModel> schoolMasterModels = new ArrayList<>();
                                JSONArray statesJsonArray = new JSONArray(obj.getString("data"));
                                schoolMasterModelArrayList = schoolMasterModels;

                                for (int i = 0; i < statesJsonArray.length(); i++) {
                                    JSONObject schools = statesJsonArray.getJSONObject(i);

                                    SchoolMasterModel model = new SchoolMasterModel();
                                    model.setId(schools.getInt("id"));
                                    model.setSchoolName(schools.getString("schoolName"));
                                    model.setSchoolCode(schools.getString("schoolCode"));
                                    model.setSchoolAddress(schools.getString("schoolAddress"));
                                    model.setCity(schools.getString("city"));
                                    model.setCountry(schools.getString("country"));
                                    model.setState(schools.getString("state"));
                                    model.setActiveFlag(schools.getInt("activeFlag"));

                                    //For Curriculums

                                    JSONArray curriculumsArray = schools.getJSONArray("curriculums");
                                    ArrayList<CurriculumModel> curriculums = new ArrayList<>();
                                    for (int j = 0; j < curriculumsArray.length(); j++) {
                                        JSONObject curriculumObject = curriculumsArray.getJSONObject(j);
                                        CurriculumModel curriculumModel = new CurriculumModel();
                                        // Deserialize curriculum object (assuming similar fields and methods in CurriculumModel)
                                        curriculumModel.setId(curriculumObject.getInt("id"));
                                        curriculumModel.setName(curriculumObject.getString("name"));
                                        curriculumModel.setActiveFlag(curriculumObject.getInt("activeFlag"));
                                        curriculumModel.setCode(curriculumObject.getString("code"));
                                        curriculumModel.setStartMonth(curriculumObject.getInt("startMonth"));
                                        curriculumModel.setEndMonth(curriculumObject.getInt("endMonth"));
                                        curriculumModel.setCurriculumId(curriculumObject.getInt("curriculumId"));
                                        // Add curriculum model to the list
                                        curriculums.add(curriculumModel);
                                    }
                                    model.setCurriculums(curriculums);

                                    schoolMasterModels.add(model);
                                    Log.d("ClassName", model.getSchoolName());
//                                    Log.d("ClassName", String.valueOf(model.getCurriculums()));
                                }

                                SchoolMasterModel otherSchool = new SchoolMasterModel();
                                otherSchool.setSchoolName("Others");
                                schoolMasterModels.add(otherSchool);

                                for (SchoolMasterModel topic : schoolMasterModelArrayList) {
                                    schoolNameList.add(topic.getSchoolName());
                                }
//                                dataSource.states.addStates(classMasterModels);

                            } else {
                                Toast.makeText(ParentRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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

    //Function to call the API to get the Curriculums based on the School Id
    private void getCurriculum(Integer schoolId) {
        loadingClasses = Util.getProgressDialog(ParentRegistrationActivity.this, "Loading Data");

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            String url;
            if (schoolId == 0) {
                url = App.GET_CURRICULUM_WITHOUT_URL;
            } else {
                url = App.GET_CURRICULUM_URL.replace("{schoolId}", String.valueOf(schoolId));
            }
            System.out.println("url " + url);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        loadingClasses.dismiss();
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");

                            if (status.equals("true")) {
//                                dataSource.states.clearAll();

                                ArrayList<CurriculumModel> curriculumModels = new ArrayList<>();
                                JSONArray curriculumJsonArray = new JSONArray(obj.getString("data"));
                                curriculumModelArrayList = curriculumModels;
                                curriculumModelArrayList.clear();
                                curriculumNameList.clear();
                                for (int i = 0; i < curriculumJsonArray.length(); i++) {
                                    JSONObject curriculums = curriculumJsonArray.getJSONObject(i);

                                    CurriculumModel model = new CurriculumModel();
                                    model.setId(curriculums.getInt("id"));
                                    model.setName(curriculums.getString("name"));
                                    model.setCode(curriculums.getString("code"));
                                    model.setStartMonth(curriculums.isNull("startMonth") ? null : curriculums.getInt("startMonth"));
                                    model.setEndMonth(curriculums.isNull("endMonth") ? null : curriculums.getInt("endMonth"));
                                    model.setCurriculumId(curriculums.isNull("curriculumId") ? null : curriculums.getInt("curriculumId"));
                                    model.setActiveFlag(curriculums.getInt("activeFlag"));


                                    curriculumModels.add(model);
                                    Log.d("CurriculumName", model.getName());
//                                    Log.d("ClassName", String.valueOf(model.getCurriculums()));
                                }


                                for (CurriculumModel topic : curriculumModelArrayList) {
                                    curriculumNameList.add(topic.getName());
                                }
//                                dataSource.states.addStates(classMasterModels);

                            } else {
                                Toast.makeText(ParentRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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

    //Function that checks if all the fields are filled before calling RegisterUser
    public void checkValidation() {
        if (firstName_et.getText().toString().equals("")) {
            Toast.makeText(this, "Enter First Name.", Toast.LENGTH_SHORT).show();
        } else if (lastName_et.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Last Name.", Toast.LENGTH_SHORT).show();
        } else if (email_et.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Email Id.", Toast.LENGTH_SHORT).show();
        } else if (!validEmail(email_et.getText().toString())) {
            Toast.makeText(this, "Enter Valid Email Id.", Toast.LENGTH_SHORT).show();
        } else if (contact_et.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Contact Number.", Toast.LENGTH_SHORT).show();
        } else if (contact_et.getText().toString().length() != 10) {
            Toast.makeText(this, "Enter Valid Contact Number.", Toast.LENGTH_SHORT).show();
        } else if (tvNodataAvailable.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, "Add your child", Toast.LENGTH_SHORT).show();
        } else if (!male_rb.isChecked() && !female_rb.isChecked()) {
            Toast.makeText(this, "Select your gender", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
            registerUser();
        }
    }

    //Function to Register Parent With Child
    private void registerUser() {
//        ProgressDialog dialog = ProgressDialog.show(this, "", "Wait...", true);
//        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        List<UserModelNew> parentList = new ArrayList<>();

        UserModelNew model = new UserModelNew();
        model.setFirstName(firstName_et.getText().toString());
        model.setLastName(lastName_et.getText().toString());
        model.setEmailId(email_et.getText().toString());
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
//        String date = format.format(dob_tv.getText().toString());
//        model.setDob(dob_tv.getText().toString());

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
        model.setRoleId(3L);
        model.setProfilePicture("");
        model.setChildrenList(parentUserModel.getChildrenList());
//        parentList.add(model);

        Log.d("UserModelLl", model.toString());

        System.out.println();
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
                                Toast.makeText(ParentRegistrationActivity.this, " " + message, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ParentRegistrationActivity.this, "Please check your Email id for Password and Verification", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ParentRegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (!status) {
                                Toast.makeText(ParentRegistrationActivity.this, " " + message, Toast.LENGTH_SHORT).show();
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

    // Method from the interface to check the adapter for the child if added or removed
    @Override
    public void onChildRemoved(int position) {
        tvNodataAvailable.setVisibility(View.VISIBLE);
    }
}