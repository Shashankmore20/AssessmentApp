package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.apptmyz.assessment.R;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView backbtn;
    private Button submit;

    private TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backbtn = findViewById(R.id.backkBtn);
        submit = findViewById(R.id.submit);
        email = findViewById(R.id.email);

        backbtn.setOnClickListener(view -> finish());

        submit.setOnClickListener(view -> goToSetNewPasswordScreen());


    }

    private void goToSetNewPasswordScreen() {
        if (validate(email.getText().toString())) {
            submit.setEnabled(true);
            Intent intent = new Intent(ForgotPasswordActivity.this, SetNewPasswordActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ForgotPasswordActivity.this, "Please Enter a Valid Email Id", Toast.LENGTH_SHORT).show();
        }
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}