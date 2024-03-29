package com.example.proiectlaborator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VehicleDiagnosticActivity extends AppCompatActivity {

    private TextView problemsTxt;
    private static final int CALL_PERMISSION_REQUEST_CODE = 101;
    private String phoneNumber = "123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_diagnostic);

        // Find views
        Button engineBtn = findViewById(R.id.engineBtn);
        Button tiresBtn = findViewById(R.id.tiresBtn);
        Button directionBtn = findViewById(R.id.directionBtn);
        Button exhaustBtn = findViewById(R.id.exhaustBtn);
        problemsTxt = findViewById(R.id.problemsTxt);

        // Set click listeners
        engineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("engine");
            }
        });

        tiresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("tires");
            }
        });

        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("direction");
            }
        });

        exhaustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("exhaust");
            }
        });
    }

    private void showConfirmationDialog(final String topic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Call?");
        builder.setMessage("Do you want to call the service?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check if permission is granted to make the call
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                } else {
                    makeCall();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Display problems and solutions in the TextView
                displayProblemsAndSolutions(topic);
            }
        });
        builder.show();
    }

    private void displayProblemsAndSolutions(String topic) {
        String problems = "";

        switch (topic) {
            case "engine":
                problems = "Engine Issues:\n- Check engine oil level.\n- Inspect for any leaks.";
                break;
            case "tires":
                problems = "Tire Problems:\n- Check tire pressure.\n- Inspect tires for wear and tear.";
                break;
            case "direction":
                problems = "Steering and Suspension:\n- Check for unusual sounds when turning.\n- Inspect for any loose components.";
                break;
            case "exhaust":
                problems = "Exhaust System:\n- Check for exhaust leaks.\n- Inspect muffler and exhaust pipes.";
                break;
            default:
                problems = "Unknown topic.";
        }

        problemsTxt.setText(problems);
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(this, "Permission denied to make a call", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
