package com.example.poc_hackfest_qr_reader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PassportActivity extends AppCompatActivity {

    public static final String EXTRA_ENTRY_ID = "EXTRA_ENTRY_ID";

    private TextView lblScanDate;
    private TextView lblFirstName;
    private TextView lblLastName;
    private TextView lblGender;
    private TextView lblDoB;
    private TextView lblLotNumber1;
    private TextView lblLotNumber2;
    private TextView lblLocation1;
    private TextView lblLocation2;
    private TextView lblVaccineDate1;
    private TextView lblVaccineDate2;
    private Button btnOpenGoogleMaps;
    private TextView lblVaccinTitle;

    private PassportVaccinal passportVaccinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        // Get the entry
        Intent intent = getIntent();
        long entryId = intent.getLongExtra(EXTRA_ENTRY_ID, 0);
        if (entryId == 0) {
            finish();
        }

        // Gui
        lblScanDate = findViewById(R.id.lblScanDate);
        lblFirstName = findViewById(R.id.lblFirstName);
        lblLastName = findViewById(R.id.lblLastName);
        lblGender = findViewById(R.id.lblGender);
        lblDoB = findViewById(R.id.lblDoB);
        lblLotNumber1 = findViewById(R.id.lblLotNumber1);
        lblLotNumber2 = findViewById(R.id.lblLotNumber2);
        lblLocation1 = findViewById(R.id.lblLocation1);
        lblLocation2 = findViewById(R.id.lblLocation2);
        lblVaccineDate1 = findViewById(R.id.lblVaccineDate1);
        lblVaccineDate2 = findViewById(R.id.lblVaccineDate2);
        btnOpenGoogleMaps = findViewById(R.id.btnOpenGoogleMaps);
        lblVaccinTitle = findViewById(R.id.lblVaccinTitle);

        // Get the passport
        passportVaccinal = new PassportVaccinal(entryId, this);

        // Display the passport
        lblScanDate.setText(passportVaccinal.getScanDate());
        lblFirstName.setText(passportVaccinal.getFirstName());
        lblLastName.setText(passportVaccinal.getFamilyName());
        lblGender.setText(passportVaccinal.getGender());
        lblDoB.setText(passportVaccinal.getBirthDate());
        lblLotNumber1.setText(passportVaccinal.getLotNumber1());
        lblLotNumber2.setText(passportVaccinal.getLotNumber2());
        lblLocation1.setText(passportVaccinal.getLocation1());
        lblLocation2.setText(passportVaccinal.getLocation2());
        lblVaccineDate1.setText(passportVaccinal.getVaccineDate1());
        lblVaccineDate2.setText(passportVaccinal.getVaccineDate2());
        lblVaccinTitle.setText("Vaccins (" + String.valueOf(passportVaccinal.getVaccineCount()) + ")");

        // Button Listener
        btnOpenGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://maps.google.com/maps?q=" + passportVaccinal.getLatitude() + "," + passportVaccinal.getLongitude();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }
}