package com.test.reachability;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StorageActivity extends AppCompatActivity {

    private static final String SECRET = "super_secret_token_9982xABC";
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Storage demo");

        Button btnWrite = new Button(this);
        btnWrite.setText("Write Secret");
        btnWrite.setOnClickListener(v -> writeSecret());

        Button btnRead = new Button(this);
        btnRead.setText("Read Secret");
        btnRead.setOnClickListener(v -> readSecret());

        layout.addView(btnWrite);
        layout.addView(btnRead);
        layout.addView(tvResult);

        setContentView(layout);
    }

    @SuppressWarnings("deprecation")
    private void writeSecret() {
        // Write to SharedPreferences with MODE_WORLD_READABLE (insecure)
        SharedPreferences prefs = getSharedPreferences("secrets", 1); // 1 = MODE_WORLD_READABLE
        prefs.edit().putString("token", SECRET).apply();

        // Write to external storage (insecure)
        try {
            @SuppressWarnings("deprecation")
            File file = new File(Environment.getExternalStorageDirectory(), "secrets.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(SECRET);
            writer.close();
            tvResult.setText("Secret written to SharedPreferences and external storage");
        } catch (IOException e) {
            tvResult.setText("Error writing to external storage: " + e.getMessage());
        }

        logSensitiveData();
        conditionallyDeleteSecrets();
    }

    private void readSecret() {
        try {
            @SuppressWarnings("deprecation")
            File file = new File(Environment.getExternalStorageDirectory(), "secrets.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String content = reader.readLine();
            reader.close();
            tvResult.setText("Secret from file: " + content);
        } catch (IOException e) {
            tvResult.setText("Error reading file: " + e.getMessage());
        }
    }

    private void logSensitiveData() {
        Log.d("SECRETS", "Stored token: " + SECRET);
    }

    @SuppressWarnings("deprecation")
    private void conditionallyDeleteSecrets() {
        boolean shouldDelete = false; // never changes
        if (shouldDelete) {
            // DEAD BRANCH - reachability test: should NOT be flagged
            File f = new File(Environment.getExternalStorageDirectory(),
                              "secrets.txt");
            f.delete();
            SharedPreferences prefs = getSharedPreferences("app_prefs", 0);
            prefs.edit().clear().apply();
            Log.d("SECRETS", "All secrets wiped: " +
                  "super_secret_token_9982xABC");
        }
    }
}
