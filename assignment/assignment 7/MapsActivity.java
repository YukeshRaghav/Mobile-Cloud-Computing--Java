package com.example.googlelocation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlelocation.databinding.ActivityMapsBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.tasks.OnSuccessListener;

import android.widget.SearchView;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener{

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    TextView mTapTextView, mCameraTextView;
    private SearchView mapSearch;
    LinearLayout floorPickerLayout;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTapTextView = findViewById(R.id.tapTextView);
        mCameraTextView = findViewById(R.id.cameraTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));

        mapSearch = findViewById(R.id.mapSearch);

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchForPlace(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform live suggestions here as the user types
                return true;
            }
        });
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapClick(LatLng point) {
        mTapTextView.setText("tapped, point=" + point);
        mMap.addMarker(new MarkerOptions().position(point));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mTapTextView.setText("long pressed, point=" + point);
        mMap.addMarker(new MarkerOptions().position(point));
    }

    @Override
    public void onCameraIdle() {
        mCameraTextView.setText(mMap.getCameraPosition().toString());
    }

    private void searchForPlace(String query) {
        String location = mapSearch.getQuery().toString();
        List<Address> addressList = null;
        if (location != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
        Toast.makeText(MapsActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            updateLocationUI();
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getDeviceLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(location)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                                if (currentLocationMarker != null) {
                                    currentLocationMarker.remove();
                                }
                                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                                        .title("Current Location"));
                            } else {
                                Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    updateLocationUI();
                    getDeviceLocation();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
