package com.apptmyz.assessment.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.UserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class APIRequests {
    private boolean login(String uname, String upass, Context context) {

        AtomicBoolean temp = new AtomicBoolean(false);

        RequestQueue queue = Volley.newRequestQueue(context);

        HashMap model=new HashMap();
        model.put("loginname",uname);
        model.put("password", Util.getMd5(upass));

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper);

        try {
            String data = mapper.writeValueAsString(model);
            System.out.println(data);
            System.out.println("url " + App.LOGIN_URL);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    App.LOGIN_URL, new JSONObject(data),

                    response -> {
                        try {
                            // to json object to extract data from it.
                            Log.d("Response=>>>", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            String token = obj.getString("token");

                            if (status.equals("true")) {
                                temp.set(true);
                                UserDetails userDetails = UserDetails.getInstance();

                                JSONObject obj1 = new JSONObject(obj.getString("data"));
                                try{
                                    userDetails.setUserId((int)obj1.getLong("userid"));
                                    userDetails.setFirstName(obj1.getString("firstname"));
                                    userDetails.setLastName(obj1.getString("lastname"));
                                    userDetails.setLoginName(obj1.getString("loginname"));
                                    userDetails.setPassword(obj1.getString("password"));
                                    userDetails.setEmailId(obj1.getString("emailid"));

                                    //-----------------------------------
                                    String sDate6 = obj1.getString("dob");

                                    SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date6=formatter1.parse(sDate6);
                                    System.out.println(sDate6+"\t"+date6);

                                    String pattern = "dd/MM/yyyy";
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                    String date = simpleDateFormat.format(date6);
                                    System.out.println(date);

                                    //--------------------------------------

                                    userDetails.setDob(date);
                                    userDetails.setGender(obj1.getString("gender"));
                                    userDetails.setContactNumber(obj1.getString("contactNumber"));
                                    userDetails.setStateId(obj1.getInt("stateId"));
                                    userDetails.setUserClass(obj1.getInt("userClass"));
                                    userDetails.setRoleId(obj1.getInt("roleId"));
                                    userDetails.setProfilePicture(obj1.getString("profilePicture"));

                                }catch (NullPointerException e) {
                                }

                                Log.e( "login: ", "response");
                                System.out.println(userDetails);

                            } else if (status.equals("false")) {
                                Toast.makeText(context, " " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }

                    }, error -> {
                // method to handle errors.
                Toast.makeText(context, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");

                    return headers;
                }
            };
            Log.e( "login: ", "response");
            //jsonObjReq.setTag(TAG);  // Adding request to request queue
            queue.add(jsonObjReq);

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
        return temp.get();
    }
}
