package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SocietyApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FLATS = "flats";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FLAT_NUMBER = "flat_number";
    private static final String COLUMN_PREVIOUS_READING = "previous_reading";
    private static final String COLUMN_TOTAL_MAINTENANCE = "total_maintenance";
    private static final String COLUMN_CURRENT_READING = "current_reading"; // Add this column
    private static final String COLUMN_LIST_TYPE = "list_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FLATS_TABLE = "CREATE TABLE " + TABLE_FLATS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FLAT_NUMBER + " TEXT, "
                + COLUMN_PREVIOUS_READING + " TEXT, "
                + COLUMN_TOTAL_MAINTENANCE + " TEXT, "
                + COLUMN_CURRENT_READING + " TEXT, " // Add this column
                + COLUMN_LIST_TYPE + " INTEGER)";
        db.execSQL(CREATE_FLATS_TABLE);

        // Insert initial data into the database
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLATS);
        onCreate(db);
    }

    // Utility method to insert a single flat's data
    private void insertInitialFlat(SQLiteDatabase db, String flatNumber, String previousReading, String totalMaintenance, String currentReading, int listType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FLAT_NUMBER, flatNumber);
        values.put(COLUMN_PREVIOUS_READING, previousReading);
        values.put(COLUMN_TOTAL_MAINTENANCE, totalMaintenance);
        values.put(COLUMN_CURRENT_READING, currentReading);
        values.put(COLUMN_LIST_TYPE, listType);

        db.insert(TABLE_FLATS, null, values);
    }

    // Method to insert initial data into the database
    private void insertInitialData(SQLiteDatabase db) {
        insertInitialFlat(db, "101", "100", "0", "", 1);
        insertInitialFlat(db, "102", "100", "0", "", 1);
        insertInitialFlat(db, "103", "100", "0", "", 1);
        insertInitialFlat(db, "104", "100", "0", "", 1);
        insertInitialFlat(db, "105", "100", "0", "", 1);
        insertInitialFlat(db, "106", "100", "0", "", 1);
        insertInitialFlat(db, "201", "100", "0", "", 1);
        insertInitialFlat(db, "202", "100", "0", "", 1);
        insertInitialFlat(db, "203", "100", "0", "", 1);
        insertInitialFlat(db, "204", "100", "0", "", 1);
        insertInitialFlat(db, "205", "100", "0", "", 1);
        insertInitialFlat(db, "206", "100", "0", "", 1);
        insertInitialFlat(db, "301", "100", "0", "", 1);
        insertInitialFlat(db, "302", "100", "0", "", 1);
        insertInitialFlat(db, "303", "100", "0", "", 1);
        insertInitialFlat(db, "304", "100", "0", "", 1);
        insertInitialFlat(db, "101", "100", "0", "", 2);
        insertInitialFlat(db, "102", "100", "0", "", 2);
        insertInitialFlat(db, "101", "100", "0", "", 3);
        insertInitialFlat(db, "102", "100", "0", "", 3);
        insertInitialFlat(db, "101", "100", "0", "", 4);
        insertInitialFlat(db, "102", "100", "0", "", 4);

        // Add more initial data as needed
        Log.d("DatabaseHelper", "Initial data inserted into database.");
    }

    public void insertFlat(String flatNumber, String previousReading, String totalMaintenance, String currentReading, int listType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FLAT_NUMBER, flatNumber);
        values.put(COLUMN_PREVIOUS_READING, previousReading);
        values.put(COLUMN_TOTAL_MAINTENANCE, totalMaintenance);
        values.put(COLUMN_CURRENT_READING, currentReading); // Insert this value
        values.put(COLUMN_LIST_TYPE, listType);

        db.insert(TABLE_FLATS, null, values);
        db.close();
    }

    public List<Flat> getFlatsByListType(int listType) {
        List<Flat> flatList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_FLATS + " WHERE " + COLUMN_LIST_TYPE + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(listType)});

        if (cursor.moveToFirst()) {
            do {
                Flat flat = new Flat(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLAT_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PREVIOUS_READING)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_MAINTENANCE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_READING))
                );
                flatList.add(flat);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return flatList;
    }

    public void updateFlat(int id, String currentReading, String totalMaintenance) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_READING, currentReading);
        values.put(COLUMN_TOTAL_MAINTENANCE, totalMaintenance);

        db.update(TABLE_FLATS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void logDatabaseValues() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select all rows from the flats table
        String selectQuery = "SELECT * FROM " + TABLE_FLATS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Check if there are results
        if (cursor.moveToFirst()) {
            do {
                // Retrieve values from the cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String flatNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLAT_NUMBER));
                String previousReading = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PREVIOUS_READING));
                String totalMaintenance = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_MAINTENANCE));
                String currentReading = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_READING));
                int listType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIST_TYPE));

                // Log the values
                Log.d("DatabaseHelper", "ID: " + id +
                        ", Flat Number: " + flatNumber +
                        ", Previous Reading: " + previousReading +
                        ", Total Maintenance: " + totalMaintenance +
                        ", Current Reading: " + currentReading +
                        ", List Type: " + listType);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "No data found in the database.");
        }

        cursor.close();
        db.close();
    }

    public void updateFlatPreviousReading(int id, String previousReading) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PREVIOUS_READING, previousReading);

        db.update(TABLE_FLATS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}

