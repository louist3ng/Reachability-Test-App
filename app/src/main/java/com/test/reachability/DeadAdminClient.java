package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Simulated MobSF Rules:
//   authenticate() →
//     MobSF Rule: android_hardcoded
//     Pattern: (username\s*=\s*['|"].{1,100}['|"]) | (password\s*=\s*['|"].{1,100}['|"])
//     input_case: lower | type: Regex
//     CWE: CWE-312 | OWASP Mobile: M9 | MASVS: storage-14
//
//     MobSF Rule: android_ip_disclosure (IP address in URL)
//     CWE: CWE-200 | MASVS: code-2
//
//   exfiltrateContacts() →
//     MobSF Rule: android_logging
//     Pattern: Log\.(v|d|i|w|e|f|s)
//     CWE: CWE-532 | MASVS: storage-3

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is never instantiated or referenced anywhere in the project.
 * It exists solely to test whether reachability analysis tools correctly
 * identify it as unreachable dead code.
 */
public class DeadAdminClient {

    private final Context context;

    // Hardcoded credentials — matches MobSF android_hardcoded pattern
    private static final String username = "superadmin";
    private static final String password = "letmein999";

    public DeadAdminClient(Context context) {
        this.context = context;
    }

    public void authenticate() {
        try {
            // IP address disclosure — matches MobSF android_ip_disclosure
            URL url = new URL("http://10.0.0.1/api/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(("user=" + username + "&pass=" + password).getBytes());
            os.close();
            connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            Log.e("DeadAdmin", "Auth failed", e);
        }
    }

    public void exfiltrateContacts() {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d("EXFIL", name);
            }
            cursor.close();
        }
    }
}
