package com.example.myapplication;

import android.content.Context;

public class Flat {
    private int id; // Add this field
    private String flatNumber;
    private String previousReading;
    private String totalMaintenance;
    private String currentReading; // Add this field
    private int lastUpdatedMonth;

    public Flat(int id, String flatNumber, String previousReading, String totalMaintenance, String currentReading) {
        this.id = id;
        this.flatNumber = flatNumber;
        this.previousReading = previousReading;
        this.totalMaintenance = totalMaintenance;
        this.currentReading = currentReading; // Initialize this field
        this.lastUpdatedMonth = -1; // Initialize to an invalid month
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public String getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(String previousReading) {
        this.previousReading = previousReading;
    }

    public String getTotalMaintenance() {
        return totalMaintenance;
    }

    public void updateTotalMaintenance(int currentReading, Context context) {
        int multiplier = PreferenceUtils.getMultiplier(context);
        int fixedMaintenance = PreferenceUtils.getFixedMaintenance(context);
        int previousReadingInt = Integer.parseInt(previousReading);
        int maintenance = ((currentReading - previousReadingInt) * multiplier ) + fixedMaintenance;
        this.totalMaintenance = String.valueOf(maintenance);
    }

    public String getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(String currentReading) {
        this.currentReading = currentReading;
    }

    public int getLastUpdatedMonth() {
        return lastUpdatedMonth;
    }

    public void setLastUpdatedMonth(int lastUpdatedMonth) {
        this.lastUpdatedMonth = lastUpdatedMonth;
    }
}
