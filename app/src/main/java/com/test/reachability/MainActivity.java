package com.test.reachability;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // VULNERABILITY: Hardcoded sensitive information (credentials in source code)
    // MobSF Rule: android_hardcoded
    // Pattern: (password\s*=\s*['|"].{1,100}['|"]) | (key\s*=\s*['|"].{1,100}['|"])
    // input_case: lower | type: Regex
    // CWE: CWE-312 | OWASP Mobile: M9 | MASVS: storage-14
    public static final String API_KEY = "sk-prod-ABC123hardcodedSecret999";
    public static final String DB_PASSWORD = "admin1234!";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        Button btnNetwork = new Button(this);
        btnNetwork.setText("Network Demo");
        btnNetwork.setOnClickListener(v -> startActivity(new Intent(this, NetworkActivity.class)));
        layout.addView(btnNetwork);

        Button btnStorage = new Button(this);
        btnStorage.setText("Storage Demo");
        btnStorage.setOnClickListener(v -> startActivity(new Intent(this, StorageActivity.class)));
        layout.addView(btnStorage);

        Button btnComponent = new Button(this);
        btnComponent.setText("Component Demo");
        btnComponent.setOnClickListener(v -> startActivity(new Intent(this, ExposedActivity.class)));
        layout.addView(btnComponent);

        Button btnSql = new Button(this);
        btnSql.setText("SQL Demo");
        btnSql.setOnClickListener(v -> startActivity(new Intent(this, SqlActivity.class)));
        layout.addView(btnSql);

        setContentView(layout);

        logCredentials();
    }

    // VULNERABILITY: Sensitive data leakage via Android logging
    // MobSF Rule: android_logging
    // Pattern: Log\.(v|d|i|w|e|f|s)|System\.out\.print|System\.err\.print
    // input_case: exact | type: Regex
    // CWE: CWE-532 | OWASP Mobile: (info) | MASVS: storage-3
    private void logCredentials() {
        Log.d(TAG, "App initialized with key: " + API_KEY);
        Log.d(TAG, "DB access with: " + DB_PASSWORD);
    }

}
