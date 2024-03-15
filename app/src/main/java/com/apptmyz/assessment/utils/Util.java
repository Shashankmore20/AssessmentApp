package com.apptmyz.assessment.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.apptmyz.assessment.BuildConfig;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    private static final Gson gson = new Gson();
    private static ProgressDialog dialog;

    public static Object parseResponse(InputStream is, Class<?> classOfT) throws Exception {
        try {
            Reader reader = new InputStreamReader(is);
            return gson.fromJson(reader, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception();
    }

    public static ProgressDialog getProgressDialog(Context context, String msg) {
        try {
            if (dialog != null && dialog.isShowing())
                dismissProgressDialog();
            dialog = ProgressDialog.show(context, "", msg, true);
            dialog.show();
            dialog.setCancelable(false);
            dialog.show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static void dismissProgressDialog() {
        try {
            if (dialog != null && dialog.isShowing() == true)
                dialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    public static void logE(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(Constants.LOG_TAG, msg);
        }
    }

    public static void logI(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(Constants.LOG_TAG, msg);
        }
    }

    public static void logD(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(Constants.LOG_TAG, msg);
        }
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
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidString(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0)
                return true;
        }
        return false;
    }


}
