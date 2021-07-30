package com.example.poc_hackfest_qr_reader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.example.poc_hackfest_qr_reader.database.ScanDatabaseContract;
import com.example.poc_hackfest_qr_reader.database.ScanDbHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PassportVaccinal {

    private String jsonPassport;

    private long entryId;
    private String scanDate;
    private String firstName;
    private String familyName;
    private String birthDate;
    private String gender;
    private String lotNumber1;
    private String lotNumber2 = "";
    private String location1;
    private String location2 = "";
    private String vaccineDate1;
    private String vaccineDate2 = "";
    private double latitude = 0;
    private double longitude = 0;

    private PassportVaccinal() {}
//    public PassportVaccinal(String json) {
//        // TODO: Create a mock database with random names to generate data for the demo
//        Random random = new Random();
//        int num = random.nextInt(5);
//
//        this.lotNumber1 = "AA1234";
//        this.lotNumber2 = "BB5678";
//        this.location1 = "PALAIS DES CONGRES";
//        this.location2 = "PALAIS DES CONGRES";
//        this.vaccineDate1 = "2021-05-02";
//        this.vaccineDate2 = "2021-08-09";
//        this.scanDate = new Date().toString();
//        switch (num) {
//            case 0:
//                this.firstName = "Alice";
//                this.familyName = "Alice";
//                this.gender = "FEMALE";
//                this.birthDate = "1950-01-01";
//                break;
//            case 1:
//                this.firstName = "Bob";
//                this.familyName = "Bob";
//                this.gender = "MALE";
//                this.birthDate = "1951-02-02";
//                break;
//            case 2:
//                this.firstName = "Charlie";
//                this.familyName = "Charlie";
//                this.gender = "MALE";
//                this.birthDate = "1952-03-03";
//                break;
//            case 3:
//                this.firstName = "David";
//                this.familyName = "David";
//                this.gender = "MALE";
//                this.birthDate = "1953-04-04";
//                break;
//            case 4:
//                this.firstName = "Eve";
//                this.familyName = "Eve";
//                this.gender = "FEMALE";
//                this.birthDate = "1954-05-05";
//                break;
//            case 5:
//                this.firstName = "Faythe";
//                this.familyName = "Faythe";
//                this.gender = "FEMALE";
//                this.birthDate = "1955-06-06";
//                break;
//            case 6:
//                this.firstName = "Grace";
//                this.familyName = "Grace";
//                this.gender = "FEMALE";
//                this.birthDate = "1956-07-07";
//                break;
//            case 7:
//                this.firstName = "Heidi";
//                this.familyName = "Heidi";
//                this.gender = "FEMALE";
//                this.birthDate = "1957-08-08";
//                break;
//        }
//    }

    public PassportVaccinal(String json) {
        this.jsonPassport = json;

        this.familyName = getRegexGroup("\\\"family\\\":\\[\\\"(.+?)\\\"");
        this.firstName = getRegexGroup("\\\"given\\\":\\[\\\"(.+?)\\\"");
        this.birthDate = getRegexGroup("\\\"birthDate\\\":\\\"(.+?)\\\"");
        this.gender = getRegexGroup("\\\"gender\\\":\\\"(.+?)\\\"");
        this.lotNumber1 = getRegexGroup("\\\"lotNumber\\\":\\\"(.+?)\\\"", 1);
        this.lotNumber2 = getRegexGroup("\\\"lotNumber\\\":\\\"(.+?)\\\"", 2);
        this.location1 = getRegexGroup("\\\"display\\\":\\\"(.+?)\\\"", 1);
        this.location2 = getRegexGroup("\\\"display\\\":\\\"(.+?)\\\"", 2);
        this.vaccineDate1 = getRegexGroup("\\\"occurrenceDateTime\\\":\\\"(.+?)\\\"", 1)
                .split("T")[0];
        this.vaccineDate2 = getRegexGroup("\\\"occurrenceDateTime\\\":\\\"(.+?)\\\"", 2)
                .split("T")[0];
        this.scanDate = new Date().toString();

    }

    /**
     * Fetch passport from database
     *
     * @param entryId
     * @param context
     */
    public PassportVaccinal(long entryId, Context context) {
        ScanDbHelper dbHelper = new ScanDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = { String.valueOf(entryId) };
        Cursor cursor = db.query(
                ScanDatabaseContract.ScanEntry.TABLE_NAME,
                null,   // Get All
                ScanDatabaseContract.ScanEntry._ID + " = ?",
                args,
                null,
                null,
                null
        );

        // Close if nothing returns
        if (cursor.getCount() <= 0) {
            return;
        }

        // Initialize the values
        while (cursor.moveToNext()) {
            this.entryId = cursor.getLong(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry._ID));
            this.scanDate = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_SCAN_DATE));
            this.firstName = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_FIRST_NAME));
            this.familyName = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LAST_NAME));
            this.birthDate = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_DATE_OF_BIRTH));
            this.gender = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_GENDER));
            this.lotNumber1 = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOT_NUM_1));
            this.lotNumber2 = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOT_NUM_2));
            this.location1 = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOCATION_1));
            this.location2 = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LOCATION_2));
            this.vaccineDate1 = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_VACCINE_DATE_1));
            this.vaccineDate2 = cursor.getString(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_VACCINE_DATE_2));
            this.longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LONGITUDE));
            this.latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ScanDatabaseContract.ScanEntry.COLUMN_NAME_LATITUDE));
        }

        cursor.close();
    }

    public static PassportVaccinal from2DDoc(String docString) {
        PassportVaccinal passportVaccinal = new PassportVaccinal();
        passportVaccinal.familyName = getRegexGroup(docString, "L0([a-zA-Z\\s]+).+?L1", 1);
        passportVaccinal.firstName = getRegexGroup(docString, "L1([a-zA-Z\\s]+).+?L2", 1);
        passportVaccinal.birthDate = getRegexGroup(docString, "L2([a-zA-Z\\s]+).+?L3", 1);
        passportVaccinal.familyName = getRegexGroup(docString, "L0([a-zA-Z\\s]+).+?L1", 1);
        passportVaccinal.scanDate = new Date().toString();

        int nbVaccines = 0;
        try {
            nbVaccines = Integer.parseInt(getRegexGroup(docString, "L7([0-9]).*?L8", 1));
        } catch (Exception e) {}

        if (nbVaccines >= 1) {
            passportVaccinal.lotNumber1 = "Vaccin 1 reçu";
        }
        if (nbVaccines >= 2) {
            passportVaccinal.lotNumber2 = "Vaccin 2 reçu";
        }

        return passportVaccinal;
    }

    public static PassportVaccinal fromShcCode(String shcString) {
        StringBuilder result = new StringBuilder();

        // Make sure it starts with shc:/
        if (!shcString.startsWith("shc:/")) return null;

        // Array to keep the numbers
        ArrayList<Integer> arrNumbers = new ArrayList<Integer>();

        // Start the conversion
        for (int i = 5; i < shcString.length(); i += 2) {
            int num = Integer.parseInt(String.valueOf(shcString.charAt(i)) + String.valueOf(shcString.charAt(i + 1)));
            num += 45;
            result.append((char) num);
        }

        // Split into parts
        String partFull = result.toString();
        String partHeader = partFull.split("\\.")[0];
        String partPayload = partFull.split("\\.")[1];
        String partSig = partFull.split("\\.")[2];

        // Zipped payload
        String strZipped = partPayload;
        Inflater inflater = new Inflater(true);
        byte[] buffer = new byte[strZipped.getBytes().length*2];
        try {
            inflater.setInput(Base64.decode(strZipped, Base64.URL_SAFE));
            inflater.inflate(buffer, 0, buffer.length);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }

        // Unzipped payload
        StringBuilder unzipped = new StringBuilder();
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] == 0) break;
            unzipped.append((char) buffer[i]);
        }

        return new PassportVaccinal(unzipped.toString());
    }

    public static ArrayList<PassportVaccinal> getAllPassports(Context context) {
        ArrayList<PassportVaccinal> passportVaccinals = new ArrayList<>();
        ScanDbHelper dbHelper = new ScanDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
            passportVaccinals.add(new PassportVaccinal(entryId, context));
        }
        return passportVaccinals;
    }

    private String getRegexGroup(String regex) {
        return getRegexGroup(this.jsonPassport, regex, 1);
    }

    private String getRegexGroup(String regex, int numGroup) {
        return getRegexGroup(this.jsonPassport, regex, numGroup);
    }

    private static String getRegexGroup(String text, String regex, int numGroup) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        for (int i = 0; i < numGroup; i++) {
            if (!matcher.find()) {
                return "";
            }
        }
        return matcher.group(1);
    }

    public long getEntryId() {
        return this.entryId;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getBirthDate() {
        return this.birthDate;
    }

    public String getGender() {
        return this.gender;
    }

    public String getLotNumber1() {
        return this.lotNumber1;
    }

    public String getLotNumber2() {
        return this.lotNumber2;
    }

    public String getLocation1() {
        return this.location1;
    }

    public String getLocation2() {
        return this.location2;
    }

    public String getVaccineDate1() {
        return this.vaccineDate1;
    }

    public String getVaccineDate2() {
        return this.vaccineDate2;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getScanDate() {
        return this.scanDate;
    }

    public int getVaccineCount() {
        if (this.getLotNumber2() != "") {
            return 2;
        } else if (this.getLotNumber1() != "") {
            return 1;
        } else {
            return 0;
        }
    }

}
