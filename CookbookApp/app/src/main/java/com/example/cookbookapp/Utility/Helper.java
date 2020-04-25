package com.example.cookbookapp.Utility;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static final String RECIPES_API_BASE = "http://localhost:5000/api/";
    public static final String IMAGE_API_URL = "http://localhost:5000/images/";
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 8;

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

    public static boolean isEmailValid(EditText editTextEmail) {
        if(editTextEmail == null) return false;
        String email = editTextEmail.getText().toString();
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isFieldEmpty(EditText editText) {
        if(editText == null) return true;
        return TextUtils.isEmpty(editText.getText().toString());
    }

    public static int getFieldLength(EditText editText) {
        if(editText == null) return -1;
        return editText.getText().toString().length();
    }

    public static boolean hasEmptyFieldTestPassed(ArrayList<EditText> editTextList) {
        if(editTextList == null) {
            return false;
        }
        boolean hasPassed = true;
        for(EditText et: editTextList) {
            if(isFieldEmpty(et)){
                displayErrorMessage(et, "Empty field not allowed");
                hasPassed = false;
            } else {
                clearErrorField(et);
            }
        }
        return hasPassed;
    }

    public static boolean hasEmailTestPassed(EditText editTextEmail) {
        boolean isEmailValid = isEmailValid(editTextEmail);
        if(!isEmailValid) {
            displayErrorMessage(editTextEmail, "Invalid email");
            return false;
        }
        clearErrorField(editTextEmail);
        return true;
    }

    public static boolean hasMinLengthTestPassed(EditText editText, int minLength) {
        if(getFieldLength(editText) < minLength) {
            displayErrorMessage(editText, "Min Length is " + minLength);
            return false;
        }
        clearErrorField(editText);
        return true;
    }

    public static boolean hasPasswordMatchTestPassed(EditText editTextPass, EditText editTextRe) {
        if(editTextPass == null || editTextRe == null) {
            return false;
        }
        String password = editTextPass.getText().toString();
        String repeated = editTextRe.getText().toString();
        if(!password.equals(repeated)) {
            displayErrorMessage(editTextRe, "Passwords must match");
            return false;
        }
        clearErrorField(editTextRe);
        return true;
    }
}
