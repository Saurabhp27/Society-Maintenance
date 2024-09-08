package com.example.myapplication;

import android.content.Context;

public class Flat {
    private int id; // Add this field
    private String flatNumber;
    private String previousReading;
    private String totalMaintenance;
    private String currentReading; // Add this field

    public Flat(int id, String flatNumber, String previousReading, String totalMaintenance, String currentReading) {
        this.id = id;
        this.flatNumber = flatNumber;
        this.previousReading = previousReading;
        this.totalMaintenance = totalMaintenance;
        this.currentReading = currentReading; // Initialize this field
    }

    public int getId() {
        return id;
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

    public void updateTotalMaintenance(int currentReading,boolean validvalue, Context context) {
        if(!validvalue){
            this.totalMaintenance ="Invalid Value";
        }else {
            int multiplier = PreferenceUtils.getMultiplier(context);
            int fixedMaintenance = PreferenceUtils.getFixedMaintenance(context);
            int previousReadingInt = Integer.parseInt(previousReading);
            int maintenance = ((currentReading - previousReadingInt) * multiplier) + fixedMaintenance;
            this.totalMaintenance = String.valueOf(maintenance);
        }
    }

    public String getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(String currentReading) {
        this.currentReading = currentReading;
    }

}
