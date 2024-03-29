// NearbyPlacesTask.java
package com.example.proiectlaborator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearbyPlacesTask extends AsyncTask<String[], Void, List<Place>> {

    private static final String TAG = NearbyPlacesTask.class.getSimpleName();

    private Context context;
    private PlacesClient placesClient;
    private GoogleMap mMap;
    private LatLng currentLocation;

    public NearbyPlacesTask(Context context, PlacesClient placesClient, GoogleMap mMap, LatLng currentLocation) {
        this.context = context;
        this.placesClient = placesClient;
        this.mMap = mMap;
        this.currentLocation = currentLocation;
    }

    @Override
    protected List<Place> doInBackground(String[]... types) {
        try {
            String[] placeTypes = types[0];
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Check for location permissions before making the request
            if (hasLocationPermission()) {
                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Location permission not granted");
                    return null; // Return null as the permission is not granted
                }
                FindCurrentPlaceResponse response = placesClient.findCurrentPlace(request).getResult();

                if (response != null && response.getPlaceLikelihoods() != null) {
                    List<Place> nearbyPlaces = new ArrayList<>();
                    for (int i = 0; i < response.getPlaceLikelihoods().size(); i++) {
                        Place place = response.getPlaceLikelihoods().get(i).getPlace();
                        for (String type : placeTypes) {
                            if (place.getTypes() != null && Arrays.asList(place.getTypes()).contains(type)) {
                                nearbyPlaces.add(place);
                                break;
                            }
                        }
                    }
                    return nearbyPlaces;
                }
            } else {
                Log.e(TAG, "Location permission not granted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Place> places) {
        if (places != null) {
            for (Place place : places) {
                LatLng placeLatLng = place.getLatLng();
                Marker marker = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(place.getName()));
                marker.setTag(place); // Attach the Place object to the marker
            }

            // Set up marker click listener
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Place clickedPlace = (Place) marker.getTag();
                    if (clickedPlace != null) {
                        // Here you can handle the click event and display information about the clicked place
                        Log.d(TAG, "Clicked Place: " + clickedPlace.getName());
                        // Example: Show a dialog with place details
                        // showDialogWithPlaceDetails(clickedPlace);
                        return true; // Consume the event, prevents default behavior (opening marker info window)
                    }
                    return false;
                }
            });
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
