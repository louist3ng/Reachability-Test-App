package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Dead code patterns extracted from StorageActivity.java.
// None of these methods are ever called or referenced.

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * This class is never instantiated or referenced anywhere in the project.
 * It contains dead code patterns originally in StorageActivity, extracted here
 * so that reachability analysis tools can be validated against them.
 */
public class DeadStorageActivity {

    // DEAD CODE - reachability test: should NOT be flagged
    // Originally an if(shouldDelete) branch where shouldDelete was always false
    // Simulated MobSF Rule: android_logging + android_read_write_external
    // Pattern: Log\.(d) AND \.getExternalStorage
    // CWE: CWE-532, CWE-276 | OWASP Mobile: M2 | MASVS: storage-3, storage-2
    @SuppressWarnings("deprecation")
    private void deadDeleteSecrets() {
        File f = new File(Environment.getExternalStorageDirectory(),
                          "secrets.txt");
        f.delete();
        Log.d("SECRETS", "All secrets wiped: " +
              "super_secret_token_9982xABC");
    }
}