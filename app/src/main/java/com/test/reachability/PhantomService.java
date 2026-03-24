package com.test.reachability;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

// VULNERABILITY: Exported Service Without Permission Guard — logs sensitive intent data
// MobSF Rule: android_logging
// Pattern: Log\.(v|d|i|w|e|f|s)|System\.out\.print|System\.err\.print
// input_case: exact | type: Regex
// CWE: CWE-532 | OWASP Mobile: (info) | MASVS: storage-3
//
// This Service is declared exported="true" in AndroidManifest.xml with no
// android:permission attribute. Any third-party app can bind to or start it.
// It also logs incoming intent data, leaking caller information to system logs.
public class PhantomService extends Service {

    private static final String TAG = "PhantomService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Logs all intent extras — any external app's data is leaked to logcat
        Log.d(TAG, "Service started with intent: " + intent);
        if (intent != null && intent.getExtras() != null) {
            Log.d(TAG, "Intent extras: " + intent.getExtras().toString());
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound by: " + intent);
        return null;
    }
}
