package com.example.poc_hackfest_qr_reader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poc_hackfest_qr_reader.database.ScanDbHelper;
import com.example.poc_hackfest_qr_reader.webapi.WebApiHelper;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Button btnScanQR;
    private RecyclerView rvListPassports;
    private ArrayList<PassportScan> passportScans;

    private Location gpsLocation;

    private ScanDbHelper dbHelper;

    private WebApiHelper webApiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GUI
        btnScanQR = findViewById(R.id.btnScanQR);
        rvListPassports = findViewById(R.id.rvListPassports);

        // Listeners
        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        // GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = new String[2];
            perms[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            perms[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(MainActivity.this, perms, 0);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            setupGps();
        }

        // Database Initialization
        dbHelper = new ScanDbHelper(this);
        this.passportScans = dbHelper.getAllPassportScans();

        rvListPassports.setLayoutManager(new LinearLayoutManager(this));
        rvListPassports.setAdapter(new ScanAdapter(this.passportScans, new ScanAdapter.ListItemClickListener() {
            @Override
            public void onListItemClickListener(int position) {
                PassportScan passportScan = MainActivity.this.passportScans.get(position);
                Intent intent = new Intent(MainActivity.this, PassportActivity.class);
                intent.putExtra(PassportActivity.EXTRA_ENTRY_ID, passportScan.getPassport().getEntryId());
                startActivity(intent);
            }
        }));

        // Web connection
        // TODO: Change Login Info
        this.webApiHelper = new WebApiHelper(this);
        webApiHelper.login("alice", "alice");

    }

    @SuppressLint("MissingPermission")
    private void setupGps() {
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    Log.i("Scan", String.valueOf(location.getLatitude()) + " : " + String.valueOf(location.getLongitude()));
                    MainActivity.this.gpsLocation = location;
                } catch (Exception e) {
                    Log.e("Scan", "Unable to retrieve location");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        setupGps();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            // Get the passport info and display on screen
            if (result.getContents() != null) {
                PassportVaccinal passportVaccinal = null;

                if (result.getContents().startsWith("shc:/")) {
                    // SHC format
                    passportVaccinal = PassportVaccinal.fromShcCode(result.getContents());
                } else if (result.getContents().startsWith("DC") && result.getContents().substring(26, 28).equals("L0")) {
                    // Check if 2D-Doc format
                    passportVaccinal = PassportVaccinal.from2DDoc(result.getContents());
                }

                if (passportVaccinal == null) {
                    Toast.makeText(this, "Erreur de lecture. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                    return;
                }
                PassportScan passportScan = new PassportScan(
                        passportVaccinal,
                        (gpsLocation == null ? 0 : gpsLocation.getLatitude()),
                        (gpsLocation == null ? 0 : gpsLocation.getLongitude()));

                // Add to the database
                long entryId = dbHelper.addPassportScan(passportScan);

                // Add to the web api
                webApiHelper.addScan(passportScan);

                // Add to the recycler view
                this.passportScans.add(0, passportScan);
                this.rvListPassports.getAdapter().notifyDataSetChanged();

                // Display the passport info
                Intent intent = new Intent(MainActivity.this, PassportActivity.class);
                intent.putExtra(PassportActivity.EXTRA_ENTRY_ID, entryId);
                startActivity(intent);

            }
        }

    }

//    private long getPassportInfo(String content) {
//        PassportVaccinal passportVaccinal = PassportVaccinal.fromShcCode(content);
////        this.rvListPassports.getAdapter().notifyDataSetChanged();
//        if (passportVaccinal != null) {
//            long entryId = 0;
//            // Insert in the database
//            if (dbHelper != null) {
//                if (gpsLocation != null) {
//                    entryId = dbHelper.addPassportVaccinal(passportVaccinal, gpsLocation.getLatitude(), gpsLocation.getLongitude());
//                    this.webApiHelper.scan(passportVaccinal, gpsLocation.getLatitude(), gpsLocation.getLongitude());
//                } else {
//                    entryId = dbHelper.addPassportVaccinal(passportVaccinal, 0, 0);
//                    this.webApiHelper.scan(passportVaccinal, 0, 0);
//                }
//            }
//
//            // Insert in web api
//            this.webApiHelper.scan(passportVaccinal);
//
//            return entryId;
//
//        } else {
//            Toast.makeText(this, "Erreur lors de la lecture. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
//        }
//
//        return 0;
//    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i("Scan", String.valueOf(location.getLatitude()) + " : " + String.valueOf(location.getLongitude()));
        MainActivity.this.gpsLocation = location;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}