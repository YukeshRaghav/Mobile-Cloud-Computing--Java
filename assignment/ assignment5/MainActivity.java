package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.Manifest;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private TextView latitudeTextView, longitudeTextView, timeStampTextView, addressView, zipCodeView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        timeStampTextView = findViewById(R.id.timestampTextView);
        addressView = findViewById(R.id.textView_address);
        zipCodeView = findViewById(R.id.textView_zip);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                try {
                    Location location = locationResult.getLastLocation();
                    Log.d(TAG, "onLocationResult: " + location.toString());

                    // Update latitude and longitude
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());
                    latitudeTextView.setText("Latitude: " + latitude);
                    longitudeTextView.setText("Longitude: " + longitude);

                    // Update timestamp
                    Date date = new Date(location.getTime());
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                    String formattedTimeStamp = format.format(date);
                    timeStampTextView.setText("Timestamp: " + formattedTimeStamp);

                    // Update address and zip code
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String address = addresses.get(0).getAddressLine(0);
                    String zipCode = addresses.get(0).getPostalCode();
                    addressView.setText("Address: " + address);
                    zipCodeView.setText("Zip Code: " + zipCode);
                } catch (Exception e) {
                    e.printStackTrace();
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
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
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
