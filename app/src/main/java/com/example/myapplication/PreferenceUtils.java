package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_MULTIPLIER = "multiplier";
    private static final int DEFAULT_MULTIPLIER = 25;

    public static void updateMultiplier(Context context, int multiplier) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_MULTIPLIER, multiplier);
        editor.apply();
    }

    public static int getMultiplier(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MULTIPLIER, DEFAULT_MULTIPLIER); // Default to 25 if not set
    }
}
