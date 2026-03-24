package com.test.reachability;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// VULNERABILITY: Exported Activity with hidden sensitive data in view hierarchy
// MobSF Rule: android_hiddenui
// Pattern: setVisibility\(View\.GONE\) | setVisibility\(View\.INVISIBLE\)
// input_case: exact | type: Regex
// CWE: CWE-919 | OWASP Mobile: M1 | MASVS: storage-7
//
// Also triggers:
// MobSF Rule: android_hardcoded (via hardcoded secret displayed in hidden view)
// CWE: CWE-312 | OWASP Mobile: M9 | MASVS: storage-14
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

        // Hidden view containing backup secret — invisible but present in view hierarchy
        // Matches MobSF android_hiddenui: setVisibility(View.GONE)
        TextView tvHiddenSecret = new TextView(this);
        String secret = "backup_admin_secret_key_XJ9";
        tvHiddenSecret.setText("Backup Secret: " + secret);
        tvHiddenSecret.setVisibility(View.GONE);
        layout.addView(tvHiddenSecret);

        setContentView(layout);
    }
}
