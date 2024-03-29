package com.example.proiectlaborator;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAlertActivity extends AppCompatActivity {

    private final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String WEATHER_API_KEY = "5ffe07908e81bf80190020cddce01f86";

    private TextView txtViewStats;
    private EditText edtTxtCoordinates;
    private Button btnShowStats;
    private Button btnGetCurrentLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    // Notification channel ID and name
    private static final String CHANNEL_ID = "weather_warning_channel";
    private static final String CHANNEL_NAME = "Weather Warnings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_alert);

        createNotificationChannel();

        txtViewStats = findViewById(R.id.txtViewStats);
        edtTxtCoordinates = findViewById(R.id.edtTxtCoordinates);
        btnShowStats = findViewById(R.id.btnShowStats);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);

        // Initialize LocationManager and LocationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Update EditText with current coordinates
                edtTxtCoordinates.setText(latitude + ", " + longitude);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // Handle provider disabled
            }
        };


        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(WeatherAlertActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(WeatherAlertActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Handle permissions
                    return;
                }
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            }
        });

        // Fetch weather data when the button is clicked
        btnShowStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = edtTxtCoordinates.getText().toString();
                if (!coordinates.isEmpty()) {
                    // Extract latitude and longitude
                    String[] parts = coordinates.split(",");
                    double latitude = Double.parseDouble(parts[0].trim());
                    double longitude = Double.parseDouble(parts[1].trim());
                    // Fetch weather data
                    new FetchWeatherTask().execute(WEATHER_API_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + WEATHER_API_KEY);
                } else {
                    Toast.makeText(WeatherAlertActivity.this, "Please get current location first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Create notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = "Channel for weather warnings";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to send notification
    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Weather Warning")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            String response = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                if (urlConnection.getResponseCode() == 200) {
                    StringBuilder builder = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    response = builder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");
                    double pressure = main.getDouble("pressure");

                    JSONObject wind = jsonObject.getJSONObject("wind");
                    double windSpeed = wind.getDouble("speed");

                    // Check for severe weather conditions
                    boolean hasWarnings = false;
                    StringBuilder warningMessage = new StringBuilder();

                    // Example: Check for high temperature
                    if (temp > 35) {
                        hasWarnings = true;
                        warningMessage.append("High temperature! ");
                    }

                    // Example: Check for high wind speed
                    if (windSpeed > 20) {
                        hasWarnings = true;
                        warningMessage.append("High wind speed! ");
                    }

                    // Send notification if there are warnings
                    if (hasWarnings) {
                        sendNotification(warningMessage.toString());
                    }

                    // Update TextView with weather stats and warnings
                    String statsMessage = "Temperature: " + temp + "°C\n" +
                            "Humidity: " + humidity + "%\n" +
                            "Pressure: " + pressure + " hPa\n" +
                            "Wind Speed: " + windSpeed + " m/s";

                    if (hasWarnings) {
                        statsMessage += "\n\nWARNING: " + warningMessage.toString();
                    }

                    txtViewStats.setText(statsMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

