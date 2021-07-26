package com.example.poc_hackfest_qr_reader;

public class PassportScan {

    private PassportVaccinal mPassportVaccinal;
    private double mLatitude;
    private double mLongitude;

    public PassportScan(PassportVaccinal passportVaccinal, double latitude, double longitude) {
        this.mPassportVaccinal = passportVaccinal;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public PassportVaccinal getPassport() { return this.mPassportVaccinal; }
    public double getLatitude() { return this.mLatitude; }
    public double getLongitude() { return this.mLongitude; }
}
