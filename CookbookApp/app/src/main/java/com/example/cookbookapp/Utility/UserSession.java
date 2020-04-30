package com.example.cookbookapp.Utility;

import android.content.SharedPreferences;

public class UserSession {

    public static final String SHARED_PREFS = "com.example.cookbookapp.SHARED_PREFS";
    public static final String CURRENT_USER = "com.example.cookbookapp.CURRENT_USER";

    public static void saveUser(SharedPreferences.Editor editor, String val) {
        if(editor != null) {
            editor.putString(CURRENT_USER, val);
            editor.commit();
        }
    }

    public static void clearUser(SharedPreferences.Editor editor) {
        if(editor != null) {
            editor.remove(CURRENT_USER);
            editor.commit();
        }
    }

    public static String getUser(SharedPreferences sp) {
        return sp.getString(CURRENT_USER, null);
    }

}
