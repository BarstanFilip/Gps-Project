package com.example.proiectlaborator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class EmergencySuppliesActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "emergency_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_supplies);

        createNotificationChannel();

        TextView foodTxt = findViewById(R.id.foodTxt);
        TextView waterTxt = findViewById(R.id.waterTxt);
        TextView gasTxt = findViewById(R.id.gasTxt);
        TextView moneyTxt = findViewById(R.id.moneyTxt);

        TextView foodAmountTxt = findViewById(R.id.foodAmountTxt);
        TextView waterAmountTxt = findViewById(R.id.watterAmountTxt);
        TextView gasAmountTxt = findViewById(R.id.gasAmountTxt);
        TextView moneyAmountTxt = findViewById(R.id.moneyAmountTxt);

        SeekBar foodSeekBar = findViewById(R.id.foodSeekBar);
        SeekBar waterSeekBar = findViewById(R.id.waterSeekBar);
        SeekBar gasSeekBar = findViewById(R.id.gasSeekBar);
        SeekBar moneySeekBar = findViewById(R.id.moneySeekBar);


        foodSeekBar.setMax(20);
        waterSeekBar.setMax(10);
        gasSeekBar.setMax(50);
        moneySeekBar.setMax(1000);


        foodSeekBar.setProgress(20);
        waterSeekBar.setProgress(10);
        gasSeekBar.setProgress(50);
        moneySeekBar.setProgress(1000);


        foodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                foodAmountTxt.setText(String.valueOf(progress));
                if (progress < 5) {
                    sendNotification("Food", "Foodis running low!");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        waterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waterAmountTxt.setText(String.valueOf(progress));
                if (progress < 2) {
                    sendNotification("Water", "Water is running low!");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        gasSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gasAmountTxt.setText(String.valueOf(progress));
                if (progress < 10) {
                    sendNotification("Gas", "Gas is running low!");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        moneySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moneyAmountTxt.setText(String.valueOf(progress));
                if (progress < 300) {
                    sendNotification("Money", "Money is running low!");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Emergency Notification Channel";
            String description = "Channel for emergency supply notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = getNotification(title, message);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private NotificationCompat.Builder getNotification(String title, String message) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
