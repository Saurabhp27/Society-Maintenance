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
        insertInitialFlat(db, "101", "110", "0", "", 1);
        insertInitialFlat(db, "102", "88", "0", "", 1);
        insertInitialFlat(db, "103", "71", "0", "", 1);
        insertInitialFlat(db, "104", "128", "0", "", 1);
        insertInitialFlat(db, "201", "62", "0", "", 1);
        insertInitialFlat(db, "202", "135", "0", "", 1);
        insertInitialFlat(db, "203", "82", "0", "", 1);
        insertInitialFlat(db, "204", "79", "0", "", 1);
        insertInitialFlat(db, "301", "88", "0", "", 1);
        insertInitialFlat(db, "302", "120", "0", "", 1);
        insertInitialFlat(db, "303", "110", "0", "", 1);
        insertInitialFlat(db, "304", "16", "0", "", 1);
        insertInitialFlat(db, "401", "43", "0", "", 1);
        insertInitialFlat(db, "402", "108", "0", "", 1);
        insertInitialFlat(db, "403", "83", "0", "", 1);
        insertInitialFlat(db, "404", "44", "0", "", 1);

        insertInitialFlat(db, "101", "50", "0", "", 2);
        insertInitialFlat(db, "102", "108", "0", "", 2);
        insertInitialFlat(db, "103", "66", "0", "", 2);
        insertInitialFlat(db, "104", "122", "0", "", 2);
        insertInitialFlat(db, "105", "35", "0", "", 2);
        insertInitialFlat(db, "106", "35", "0", "", 2);
        insertInitialFlat(db, "201", "65", "0", "", 2);
        insertInitialFlat(db, "202", "108", "0", "", 2);
        insertInitialFlat(db, "203", "40", "0", "", 2);
        insertInitialFlat(db, "204", "126", "0", "", 2);
        insertInitialFlat(db, "205", "122", "0", "", 2);
        insertInitialFlat(db, "206", "44", "0", "", 2);
        insertInitialFlat(db, "301", "67", "0", "", 2);
        insertInitialFlat(db, "302", "173", "0", "", 2);
        insertInitialFlat(db, "303", "70", "0", "", 2);
        insertInitialFlat(db, "304", "95", "0", "", 2);
        insertInitialFlat(db, "305", "94", "0", "", 2);
        insertInitialFlat(db, "306", "51", "0", "", 2);
        insertInitialFlat(db, "401", "92", "0", "", 2);
        insertInitialFlat(db, "402", "81", "0", "", 2);
        insertInitialFlat(db, "403", "38", "0", "", 2);
        insertInitialFlat(db, "404", "55", "0", "", 2);
        insertInitialFlat(db, "405", "86", "0", "", 2);
        insertInitialFlat(db, "406", "65", "0", "", 2);


        insertInitialFlat(db, "101", "118", "0", "", 3);
        insertInitialFlat(db, "102", "94", "0", "", 3);
        insertInitialFlat(db, "103", "92", "0", "", 3);
        insertInitialFlat(db, "104", "144", "0", "", 3);
        insertInitialFlat(db, "201", "267", "0", "", 3);
        insertInitialFlat(db, "202", "131", "0", "", 3);
        insertInitialFlat(db, "203", "106", "0", "", 3);
        insertInitialFlat(db, "204", "196", "0", "", 3);
        insertInitialFlat(db, "301", "109", "0", "", 3);
        insertInitialFlat(db, "302", "82", "0", "", 3);
        insertInitialFlat(db, "303", "56", "0", "", 3);
        insertInitialFlat(db, "304", "123", "0", "", 3);
        insertInitialFlat(db, "401", "99", "0", "", 3);
        insertInitialFlat(db, "402", "72", "0", "", 3);
        insertInitialFlat(db, "403", "35", "0", "", 3);
        insertInitialFlat(db, "404", "86", "0", "", 3);

        insertInitialFlat(db, "101", "65", "0", "", 4);
        insertInitialFlat(db, "102", "113", "0", "", 4);
        insertInitialFlat(db, "103", "107", "0", "", 4);
        insertInitialFlat(db, "104", "99", "0", "", 4);
        insertInitialFlat(db, "105", "83", "0", "", 4);
        insertInitialFlat(db, "106", "228", "0", "", 4);
        insertInitialFlat(db, "201", "85", "0", "", 4);
        insertInitialFlat(db, "202", "200", "0", "", 4);
        insertInitialFlat(db, "203", "134", "0", "", 4);
        insertInitialFlat(db, "204", "66", "0", "", 4);
        insertInitialFlat(db, "205", "115", "0", "", 4);
        insertInitialFlat(db, "206", "140", "0", "", 4);
        insertInitialFlat(db, "301", "179", "0", "", 4);
        insertInitialFlat(db, "302", "148", "0", "", 4);
        insertInitialFlat(db, "303", "198", "0", "", 4);
        insertInitialFlat(db, "304", "6", "0", "", 4);
        insertInitialFlat(db, "305", "169", "0", "", 4);
        insertInitialFlat(db, "306", "79", "0", "", 4);
        insertInitialFlat(db, "401", "209", "0", "", 4);
        insertInitialFlat(db, "402", "104", "0", "", 4);
        insertInitialFlat(db, "403", "95", "0", "", 4);
        insertInitialFlat(db, "404", "137", "0", "", 4);
        insertInitialFlat(db, "405", "66", "0", "", 4);
        insertInitialFlat(db, "406", "136", "0", "", 4);

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

