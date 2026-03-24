package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Simulated MobSF Rules:
//   uploadAll() →
//     MobSF Rule: android_read_write_external
//     Pattern: \.getExternalStorage | \.getExternalFilesDir\(
//     CWE: CWE-276 | OWASP Mobile: M2 | MASVS: storage-2
//
//     MobSF Rule: android_write_app_dir
//     Pattern: MODE_PRIVATE | Context\.MODE_PRIVATE
//     CWE: CWE-276 | MASVS: storage-14
//
//     MobSF Rule: android_logging
//     Pattern: Log\.(v|d|i|w|e|f|s)
//     CWE: CWE-532 | MASVS: storage-3
//
//     MobSF Rule: android_ip_disclosure (IP in upload URL)
//     CWE: CWE-200 | MASVS: code-2
//
//     MobSF Rule: android_hardcoded (hardcoded API key)
//     CWE: CWE-312 | OWASP Mobile: M9 | MASVS: storage-14

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is never instantiated or referenced anywhere in the project.
 * It exists solely to test whether reachability analysis tools correctly
 * identify it as unreachable dead code.
 */
public class LegacyDataUploader {

    // Hardcoded API key — matches MobSF android_hardcoded
    private static final String secret = "legacy_upload_secret_99x";

    @SuppressWarnings("deprecation")
    public void uploadAll(Context ctx) {
        try {
            // Read from SharedPreferences — matches MobSF android_write_app_dir
            SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String prefsData = prefs.getAll().toString();

            // Read from external storage — matches MobSF android_read_write_external
            File file = new File(Environment.getExternalStorageDirectory(), "secrets.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String fileData = reader.readLine();
            reader.close();

            // Send both via HTTP POST to hardcoded IP — matches MobSF android_ip_disclosure
            String payload = "prefs=" + prefsData + "&file=" + fileData + "&key=" + secret;
            URL url = new URL("http://192.168.50.10/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(payload.getBytes());
            os.close();
            connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            Log.e("LegacyUploader", "Upload failed", e);
        }
    }
}
