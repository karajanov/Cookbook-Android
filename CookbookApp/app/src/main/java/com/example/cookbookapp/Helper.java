package com.example.cookbookapp;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static final String RECIPES_API_BASE = "http://localhost:5000/api/";
    public static final String IMAGE_API_URL = "http://localhost:5000/images/";

    public static void removeFocus(EditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
    }

    public static void displayErrorMessage(EditText editText, String msg) {
        if(editText == null || msg == null)
            return;
        editText.setError(msg);
    }

    public static void clearErrorField(EditText editText) {
        if(editText == null)
            return;
        editText.setError(null);
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Boolean isFieldEmpty(EditText editText) {
        if(editText == null)
            return null;
        return TextUtils.isEmpty(editText.getText().toString());
    }
}
