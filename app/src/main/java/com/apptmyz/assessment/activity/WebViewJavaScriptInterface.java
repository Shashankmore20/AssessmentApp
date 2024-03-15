package com.apptmyz.assessment.activity;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebViewJavaScriptInterface {

    private EquationTestingActivity context;

    public WebViewJavaScriptInterface(EquationTestingActivity context){
        this.context = context;
    }

    @JavascriptInterface
    public void makeToast(String message, boolean lengthLong){
        Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
    }
}
