package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_MULTIPLIER = "multiplier";
    private static final String KEY_FIXED_MAINTENANCE = "fixed_maintenance";
    private static final int DEFAULT_MULTIPLIER = 25;
    private static final int DEFAULT_FIXED_MAINTENANCE = 500;

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

    public static void updateFixedMaintenance(Context context, int fixedMaintenance) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_FIXED_MAINTENANCE, fixedMaintenance);
        editor.apply();
    }

    // Method to get the fixed maintenance value
    public static int getFixedMaintenance(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_FIXED_MAINTENANCE, DEFAULT_FIXED_MAINTENANCE); // Default to 500 if not set
    }

}
