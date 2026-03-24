package com.test.reachability;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// VULNERABILITY: Exported Activity Without Permission Guard
// OWASP Mobile Top 10 2024: M8 (Security Misconfiguration)
// MASVS: MASVS-PLATFORM (Exported Component Without Permission Protection)
// MASTG: MASTG-ANDROID-PLAT (Testing for Sensitive Data Disclosure Through IPC)
//
// This Activity is declared exported="true" in AndroidManifest.xml with a custom
// intent-filter (com.test.reachability.ADMIN) but requires NO permission to launch.
// Any third-party app or ADB command can start it and read the hardcoded credentials.
public class ExposedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No caller-permission check — any external app can reach this screen
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Hidden Admin Panel");
        tvTitle.setTextSize(24);
        layout.addView(tvTitle);

        // Sensitive credentials displayed to any caller without authorization
        TextView tvApiKey = new TextView(this);
        tvApiKey.setText("API Key: " + MainActivity.API_KEY);
        tvApiKey.setPadding(0, 16, 0, 0);
        layout.addView(tvApiKey);

        TextView tvDbPassword = new TextView(this);
        tvDbPassword.setText("DB Password: " + MainActivity.DB_PASSWORD);
        tvDbPassword.setPadding(0, 16, 0, 0);
        layout.addView(tvDbPassword);

        setContentView(layout);
    }
}
