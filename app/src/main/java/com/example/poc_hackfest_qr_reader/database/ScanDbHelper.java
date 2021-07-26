package com.example.poc_hackfest_qr_reader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.poc_hackfest_qr_reader.PassportScan;
import com.example.poc_hackfest_qr_reader.PassportVaccinal;

import java.util.ArrayList;

public class ScanDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scans.db";

    private Context mContext;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScanDatabaseContract.ScanEntry.TABLE_NAME + " (" +
                    ScanDatabaseContract.ScanEntry._ID + " INTEGER PRIMARY KEY, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_SCAN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_FIRST_NAME + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LAST_NAME + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_GENDER + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_DATE_OF_BIRTH + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOT_NUM_1 + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOT_NUM_2 + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOCATION_1 + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOCATION_2 + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_VACCINE_DATE_1 + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_VACCINE_DATE_2 + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LATITUDE + " TEXT, " +
                    ScanDatabaseContract.ScanEntry.COLUMN_NAME_LONGITUDE + " TEXT)";

    public ScanDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public long addPassportScan(PassportScan passportScan) {
        ContentValues values = new ContentValues();
        PassportVaccinal passportVaccinal = passportScan.getPassport();
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_FIRST_NAME, passportVaccinal.getFirstName());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LAST_NAME, passportVaccinal.getFamilyName());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_GENDER, passportVaccinal.getGender());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_DATE_OF_BIRTH, passportVaccinal.getBirthDate());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOT_NUM_1, passportVaccinal.getLotNumber1());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOT_NUM_2, passportVaccinal.getLotNumber2());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOCATION_1, passportVaccinal.getLocation1());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOCATION_2, passportVaccinal.getLocation2());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_VACCINE_DATE_1, passportVaccinal.getVaccineDate1());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_VACCINE_DATE_2, passportVaccinal.getVaccineDate2());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LATITUDE, passportScan.getLatitude());
        values.put(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LONGITUDE, passportScan.getLongitude());

        return this.getWritableDatabase().insert(ScanDatabaseContract.ScanEntry.TABLE_NAME, null, values);
    }

    public ArrayList<PassportScan> getAllPassportScans() {
        ArrayList<PassportScan> passportScans = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ScanDatabaseContract.ScanEntry.TABLE_NAME,
                null,   // Get All
                null,
                null,
                null,
                null,
                ScanDatabaseContract.ScanEntry._ID + " DESC"
        );

        // Initialize the values
        while (cursor.moveToNext()) {
            long entryId = cursor.getLong(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry._ID));
            PassportVaccinal passportVaccinal = new PassportVaccinal(entryId, this.mContext);
            PassportScan passportScan = new PassportScan(passportVaccinal,
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LONGITUDE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LATITUDE)));
            passportScans.add(passportScan);
        }
        return passportScans;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                // Upgrade to next version
        }
    }
}
