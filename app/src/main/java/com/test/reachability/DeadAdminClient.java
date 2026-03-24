package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Simulated Weaknesses:
//   authenticate() →
//     M1 (Improper Credential Usage) + M5 (Insecure Communication)
//     MASVS: MASVS-CRYPTO (Hardcoded Credentials) + MASVS-NETWORK-1 (Cleartext HTTP POST)
//     MASTG: MASTG-ANDROID-CRYPT (Testing Hardcoded Credentials)
//            MASTG-ANDROID-NET (Testing for Cleartext Traffic)
//   exfiltrateContacts() →
//     M6 (Inadequate Privacy Controls)
//     MASVS: MASVS-PRIVACY (Unauthorized Access to User Data)
//     MASTG: MASTG-ANDROID-PLAT (Testing for Sensitive Data Disclosure Through Logging)

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

    public DeadAdminClient(Context context) {
        this.context = context;
    }

    public void authenticate() {
        try {
            URL url = new URL("http://internal.admin.corp/api/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write("user=superadmin&pass=letmein999".getBytes());
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
