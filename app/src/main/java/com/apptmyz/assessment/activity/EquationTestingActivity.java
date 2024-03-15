package com.apptmyz.assessment.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.apptmyz.assessment.R;
import com.fasterxml.jackson.core.util.BufferRecyclers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

public class EquationTestingActivity extends AppCompatActivity {

    WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equation_testing);

        webView = findViewById(R.id.questionWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("file:///android_asset/index.html");

    }
}

//public class EquationTestingActivity extends AppCompatActivity {
//
//    private WebViewJavaScriptInterface webViewJavaScriptInterface;
//    WebView questionWebView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_equation_testing);
//
//        questionWebView = findViewById(R.id.questionWebView);
//
//        WebSettings webSettings = questionWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//
//        webViewJavaScriptInterface = new WebViewJavaScriptInterface(this);
//        questionWebView.addJavascriptInterface(webViewJavaScriptInterface, "app");
//
//        questionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//
//        WebView.setWebContentsDebuggingEnabled(true);
//
//        questionWebView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result){
//                return super.onJsAlert(view, url, message, result);
//            }
//        });
//
//        String indexFile = readFileToString("index.html");
//
//        indexFile = indexFile.replace("Insert_title_here", "Hello, World!");
//
//        questionWebView.loadDataWithBaseURL("file:///android_asset/", indexFile, "text/html", "utf-8", null);
//
//        questionWebView.loadUrl("javascript:alert('This alert function was called from the native app.')");
//
//    }
//
//    private String readFileToString(String fileName){
//        BufferedReader reader = null;
//        StringBuilder stringBuilder = new StringBuilder();
//        try{
//            reader = new BufferedReader(
//                    new InputStreamReader(getAssets().open(fileName), "UTF-8"));
//
//            String myLine;
//            while((myLine = reader.readLine()) != null){
//                stringBuilder.append(myLine);
//                stringBuilder.append("\n");
//            }
//        } catch (Exception ex){
//            ex.printStackTrace();
//        } finally {
//            if(reader != null){
//                try {
//                    reader.close();
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        return stringBuilder.toString();
//    }
//
//}