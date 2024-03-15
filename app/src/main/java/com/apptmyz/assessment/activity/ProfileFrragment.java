package com.apptmyz.assessment.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.database.DataSharedPref;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.database.UserDetails;
import com.apptmyz.assessment.utils.Constants;
import com.google.gson.Gson;

public class ProfileFrragment extends Fragment {
    TextView fullName_tv, dob_tv, email_tv, number_tv, loginName_tv, tv_class;

    TextView name_tv;

    TextView textView;

    UserDetails userDetails;
    ImageView btn_logout, backButton;
    ImageView editBtn;
//    ImageButton editBtn;

    private DataSharedPref dataSharedPref;
    private DataSource dataSource;



    //    Button btn1,btn2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        // Inflate the layout for this fragment

//        userDetails = UserDetails.getInstance();

        dataSharedPref = new DataSharedPref(getContext());
        dataSource = new DataSource(getContext());
        String userdata = dataSource.sharedPreferences.getValue(Constants.USER_DETAILS);
        Gson json = new Gson();
        userDetails = json.fromJson(userdata, UserDetails.class);
        name_tv = view.findViewById(R.id.tv_fullname);
        // class_tv = view.findViewById(R.id.class_tv);
        backButton = view.findViewById(R.id.iv_back);
        name_tv.setText(userDetails.getFirstName());
        // class_tv.setText("Class " +userDetails.getUserClass()+"");

        btn_logout = view.findViewById(R.id.iv_logout);
        editBtn = view.findViewById(R.id.iv_edit);

        btn_logout.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);


            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSharedPref dataSharedPref = new DataSharedPref(getContext());
                dataSharedPref.logout();
                dataSource.sharedPreferences.set(Constants.LOGOUT_PREF, Constants.TRUE);

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Alert")
                        .setMessage("Do you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });
        /*
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (view.getId()){
//                    case R.id.btn_logout:
//                        dataSharedPref = new DataSharedPref(getContext());
//                        dataSharedPref.logout();
//                        new AlertDialog.Builder(getContext())
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setTitle("Alert")
//                                .setMessage("Do you want to logout?")
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent(getContext(), LoginActivity.class);
//                                        startActivity(intent);
////                                        finish();
//                                    }
//
//                                })
//                                .setNegativeButton("No", null)
//                                .show();
//                    case R.id.edit_profile:
//                        Intent intent = new Intent(getContext(), EditProfileActivity.class);
//                        startActivity(intent);
//                        break;
////
//
//                }
//            }
//        };
//        btn_logout.setOnClickListener(listener);
//        editBtn.setOnClickListener(listener);

         */


//        btn1 = (Button) view.findViewById(R.id.nav_home);
//        btn2 = (Button) view.findViewById(R.id.nav_logout);
        /*
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MainActivity2.class);
//                startActivity(intent);
//
//            }
//        });
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DataSharedPref dataSharedPref = new DataSharedPref(getContext());
//                dataSharedPref.logout();
//
//                Intent intent = new Intent(getContext(), LoginActivity.class);
//                startActivity(intent);
//
//            }
//        });


         */


        fullName_tv = view.findViewById(R.id.tv_fullname);
        loginName_tv = view.findViewById(R.id.loginName_tv);
        dob_tv = view.findViewById(R.id.tv_dob);
        email_tv = view.findViewById(R.id.tv_email);
        number_tv = view.findViewById(R.id.tv_mobile);
        tv_class = view.findViewById(R.id.tv_class);

        fullName_tv.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        dob_tv.setText(userDetails.getDob());
        email_tv.setText(userDetails.getEmailId());
        number_tv.setText(userDetails.getContactNumber());
        loginName_tv.setText(userDetails.getLoginName());
        tv_class.setText(String.valueOf(userDetails.getUserClass()));

//        textView = view.findViewById(R.id.textView);
//        textView.setText("Edit Profile");
//        textView.setOnClickListener(listener);
        return view;
    }
}
