package com.example.proiectlaborator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LiveLocationForContactsActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private Location currentLocation; // Variable to store the current location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location_for_contacts);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        } else {
            // Permission has already been granted, proceed with location-related operations
            // For example, you can start retrieving the location here
            Log.d(TAG, "Location granted");
            requestLocationUpdates();
        }

        Button shareLiveLocationBtn = findViewById(R.id.shareLiveLocationBtn);
        shareLiveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    shareLocation(currentLocation);
                } else {
                    Log.e(TAG, "Current location is null");
                    Toast.makeText(LiveLocationForContactsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e(TAG, "Location result is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update currentLocation with the new location
                    currentLocation = location;
                }
            }
        };

        // Check for location permission before requesting updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                    }
                }
            });
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            Log.e(TAG, "Location permission not granted");
            // You may want to handle this case by requesting permission again or showing a message to the user.
        }
    }

    private void shareLocation(@NonNull Location location) {
        // Create a Uri representing the location
        String uriString = "geo:" + location.getLatitude() + "," + location.getLongitude();
        Uri locationUri = Uri.parse(uriString);

        // Create a share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "My current location");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Location");
        shareIntent.putExtra(Intent.EXTRA_STREAM, locationUri);

        // Set the flag to allow the user to choose an app to share the location with
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start the activity with the share intent
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related operations
                // For example, you can start retrieving the location here after permission is granted
                Log.d(TAG, "Location permission granted");
                requestLocationUpdates();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Location permission denied");
            }
        }
    }
}
