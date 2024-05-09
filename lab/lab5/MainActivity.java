package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private TextView latTextView, longTextView, timeTextView; // Changed variable names
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationUpdateCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latitudeTextView); // Adjusted variable names
        longTextView = findViewById(R.id.longitudeTextView); // Adjusted variable names
        timeTextView = findViewById(R.id.timestampTextView); // Adjusted variable names

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationUpdateCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "onLocationResult: " + location.toString());
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());

                    Date date = new Date(location.getTime());
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formattedTime = format.format(date);
                    System.out.println(formattedTime);
                    format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                    formattedTime = format.format(date);
                    //System.out.println(formatted);

                    String timestamp = formattedTime;
                    latTextView.setText("Latitude : " + latitude);
                    longTextView.setText("Longitude : " + longitude);
                    timeTextView.setText("TimeStamp : " + timestamp);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationProviderClient.requestLocationUpdates(locationRequest, locationUpdateCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationUpdateCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
