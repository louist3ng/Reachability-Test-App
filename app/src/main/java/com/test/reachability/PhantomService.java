package com.test.reachability;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// VULNERABILITY: Exported Service Without Permission Guard
// OWASP Mobile Top 10 2024: M8 (Security Misconfiguration)
// MASVS: MASVS-PLATFORM (Exported Component Without Permission Protection)
// MASTG: MASTG-ANDROID-PLAT (Testing for Exported Service Abuse)
//
// This Service is declared exported="true" in AndroidManifest.xml with no
// android:permission attribute. Any third-party app can bind to or start it.
public class PhantomService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
