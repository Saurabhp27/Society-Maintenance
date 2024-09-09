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
    private static final int DATABASE_VERSION = 2;

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
        insertInitialFlat(db, "101", "129", "0", "", 1);
        insertInitialFlat(db, "102", "111", "0", "", 1);
        insertInitialFlat(db, "103", "86", "0", "", 1);
        insertInitialFlat(db, "104", "159", "0", "", 1);
        insertInitialFlat(db, "201", "77", "0", "", 1);
        insertInitialFlat(db, "202", "154", "0", "", 1);
        insertInitialFlat(db, "203", "96", "0", "", 1);
        insertInitialFlat(db, "204", "98", "0", "", 1);
        insertInitialFlat(db, "301", "102", "0", "", 1);
        insertInitialFlat(db, "302", "139", "0", "", 1);
        insertInitialFlat(db, "303", "131", "0", "", 1);
        insertInitialFlat(db, "304", "19", "0", "", 1);
        insertInitialFlat(db, "401", "53", "0", "", 1);
        insertInitialFlat(db, "402", "130", "0", "", 1);
        insertInitialFlat(db, "403", "96", "0", "", 1);
        insertInitialFlat(db, "404", "52", "0", "", 1);

        insertInitialFlat(db, "101", "59", "0", "", 2);
        insertInitialFlat(db, "102", "117", "0", "", 2);
        insertInitialFlat(db, "103", "77", "0", "", 2);
        insertInitialFlat(db, "104", "146", "0", "", 2);
        insertInitialFlat(db, "105", "41", "0", "", 2);
        insertInitialFlat(db, "106", "45", "0", "", 2);
        insertInitialFlat(db, "201", "77", "0", "", 2);
        insertInitialFlat(db, "202", "128", "0", "", 2);
        insertInitialFlat(db, "203", "47", "0", "", 2);
        insertInitialFlat(db, "204", "161", "0", "", 2);
        insertInitialFlat(db, "205", "144", "0", "", 2);
        insertInitialFlat(db, "206", "51", "0", "", 2);
        insertInitialFlat(db, "301", "82", "0", "", 2);
        insertInitialFlat(db, "302", "196", "0", "", 2);
        insertInitialFlat(db, "303", "77", "0", "", 2);
        insertInitialFlat(db, "304", "114", "0", "", 2);
        insertInitialFlat(db, "305", "99", "0", "", 2);
        insertInitialFlat(db, "306", "62", "0", "", 2);
        insertInitialFlat(db, "401", "109", "0", "", 2);
        insertInitialFlat(db, "402", "93", "0", "", 2);
        insertInitialFlat(db, "403", "46", "0", "", 2);
        insertInitialFlat(db, "404", "64", "0", "", 2);
        insertInitialFlat(db, "405", "101", "0", "", 2);
        insertInitialFlat(db, "406", "79", "0", "", 2);


        insertInitialFlat(db, "101", "139", "0", "", 3);
        insertInitialFlat(db, "102", "111", "0", "", 3);
        insertInitialFlat(db, "103", "105", "0", "", 3);
        insertInitialFlat(db, "104", "170", "0", "", 3);
        insertInitialFlat(db, "201", "299", "0", "", 3);
        insertInitialFlat(db, "202", "152", "0", "", 3);
        insertInitialFlat(db, "203", "119", "0", "", 3);
        insertInitialFlat(db, "204", "214", "0", "", 3);
        insertInitialFlat(db, "301", "120", "0", "", 3);
        insertInitialFlat(db, "302", "96", "0", "", 3);
        insertInitialFlat(db, "303", "65", "0", "", 3);
        insertInitialFlat(db, "304", "137", "0", "", 3);
        insertInitialFlat(db, "401", "108", "0", "", 3);
        insertInitialFlat(db, "402", "88", "0", "", 3);
        insertInitialFlat(db, "403", "43", "0", "", 3);
        insertInitialFlat(db, "404", "97", "0", "", 3);

        insertInitialFlat(db, "101", "74", "0", "", 4);
        insertInitialFlat(db, "102", "128", "0", "", 4);
        insertInitialFlat(db, "103", "127", "0", "", 4);
        insertInitialFlat(db, "104", "116", "0", "", 4);
        insertInitialFlat(db, "105", "97", "0", "", 4);
        insertInitialFlat(db, "106", "263", "0", "", 4);
        insertInitialFlat(db, "201", "101", "0", "", 4);
        insertInitialFlat(db, "202", "231", "0", "", 4);
        insertInitialFlat(db, "203", "153", "0", "", 4);
        insertInitialFlat(db, "204", "72", "0", "", 4);
        insertInitialFlat(db, "205", "136", "0", "", 4);
        insertInitialFlat(db, "206", "166", "0", "", 4);
        insertInitialFlat(db, "301", "205", "0", "", 4);
        insertInitialFlat(db, "302", "168", "0", "", 4);
        insertInitialFlat(db, "303", "230", "0", "", 4);
        insertInitialFlat(db, "304", "8", "0", "", 4);
        insertInitialFlat(db, "305", "190", "0", "", 4);
        insertInitialFlat(db, "306", "89", "0", "", 4);
        insertInitialFlat(db, "401", "245", "0", "", 4);
        insertInitialFlat(db, "402", "118", "0", "", 4);
        insertInitialFlat(db, "403", "107", "0", "", 4);
        insertInitialFlat(db, "404", "160", "0", "", 4);
        insertInitialFlat(db, "405", "90", "0", "", 4);
        insertInitialFlat(db, "406", "154", "0", "", 4);

        // Add more initial data as needed
        Log.d("DatabaseHelper", "Initial data inserted into database.");
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

    public void performUpdate() {
        SQLiteDatabase db = this.getWritableDatabase();
        // SQL statement to update all previous readings to the current reading
        String updateQuery = "UPDATE " + TABLE_FLATS + " SET "
                + COLUMN_PREVIOUS_READING + " = " + COLUMN_CURRENT_READING + ", "
                + COLUMN_CURRENT_READING + " = ''";

        // Execute the SQL statement
        db.execSQL(updateQuery);
    }

    public List<Flat> getAllFlats() {
        List<Flat> flatList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FLATS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

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
        return flatList;
    }

    public void updateFlatPreviousReading(int id, String previousReading) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PREVIOUS_READING, previousReading);

        db.update(TABLE_FLATS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}

