package com.apptmyz.assessment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptmyz.assessment.R;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class SetNewPasswordActivity extends AppCompatActivity {

    TextInputEditText password, confirmPassword;
    TextView star, checkTwo;
    ImageView check;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        submit = findViewById(R.id.submit);
        check = findViewById(R.id.check);
        star = findViewById(R.id.checkOnestar);
        checkTwo = findViewById(R.id.checkTwo);

        //Text Watcher for Password Field
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 8){
                    submit.setEnabled(false);
                    star.setVisibility(View.VISIBLE);
//                    Toast.makeText(SetNewPasswordActivity.this, "Password should contain at least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    check.setVisibility(View.VISIBLE);
                    star.setVisibility(View.INVISIBLE);
                    submit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        //Text Watcher for Confirm Password field
        TextWatcher confirmPasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check if confirm password matches the password

                if (charSequence.toString().equals(password.getText().toString())){
                    submit.setEnabled(true);
                    checkTwo.setVisibility(View.INVISIBLE);
                }
                else {
                    submit.setEnabled(false);
                    checkTwo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        password.addTextChangedListener(passwordWatcher);
        confirmPassword.addTextChangedListener(confirmPasswordWatcher);

        submit.setOnClickListener(view -> goToConfirmedScreen());

    }

    private void goToConfirmedScreen(){
        if (password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()){
          Toast.makeText(SetNewPasswordActivity.this, "Please enter a Password", Toast.LENGTH_SHORT).show();
          submit.setEnabled(false);
        } else {
            submit.setEnabled(true);
            Intent intent = new Intent(SetNewPasswordActivity.this, SuccessfullScreen.class);
            startActivity(intent);
            finish();
        }
    }
}