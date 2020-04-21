package com.example.cookbookapp;

import android.widget.EditText;

public class Helper {

    public static final String RECIPES_API_BASE = "http://localhost:5000/api/";
    public static final String IMAGE_API_URL = "http://localhost:5000/images/";

    public static void removeFocus(EditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
    }
}
