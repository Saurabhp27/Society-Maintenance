package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_MULTIPLIER = "multiplier";
    private static final String KEY_FIXED_MAINTENANCE = "fixed_maintenance";
    private static final int DEFAULT_MULTIPLIER = 25;
    private static final int DEFAULT_FIXED_MAINTENANCE = 500;
    private static final String Index_month = "index_month";
    private static final int Index_month_default= 8;

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

    public static void updateIndexmonth(Context context, int index_month) {
        if (index_month < 0 || index_month >= 12) {
            index_month = 0; // Correct it to a valid value
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Index_month, index_month);
        editor.apply();
    }

    public static int getIndex_month(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int index = prefs.getInt(Index_month, Index_month_default);

        if (index < 0 || index >= 12) {
            index = 0; // Reset to a default valid value (e.g., January)
            updateIndexmonth(context, index); // Save the corrected value
        }

        return index;
    }

}
