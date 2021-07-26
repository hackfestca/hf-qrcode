package com.example.poc_hackfest_qr_reader.database;

import android.provider.BaseColumns;

public final class ScanDatabaseContract {
    private ScanDatabaseContract() {};

    public static class ScanEntry implements BaseColumns {
        public static final String TABLE_NAME = "scans";
        public static final String COLUMN_NAME_SCAN_DATE = "scan_date";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_DATE_OF_BIRTH = "date_of_birth";
        public static final String COLUMN_NAME_LOT_NUM_1 = "lot_number_1";
        public static final String COLUMN_NAME_LOT_NUM_2 = "lot_number_2";
        public static final String COLUMN_NAME_LOCATION_1 = "location_1";
        public static final String COLUMN_NAME_LOCATION_2 = "location_2";
        public static final String COLUMN_NAME_VACCINE_DATE_1 = "vaccine_date_1";
        public static final String COLUMN_NAME_VACCINE_DATE_2 = "vaccine_date_2";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }



}
