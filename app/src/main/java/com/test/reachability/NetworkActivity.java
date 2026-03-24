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

    // VULNERABILITY: Cleartext HTTP Traffic with IP address disclosure
    // MobSF Rule: android_ip_disclosure
    // Pattern: IP address regex (e.g. 10.0.2.2)
    // input_case: exact | type: Regex
    // CWE: CWE-200 | OWASP Mobile: (warning) | MASVS: code-2
    private void fetchHttp() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/api/data");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                runOnUiThread(() -> tvResult.setText("HTTP Response Code: " + responseCode));
                connection.disconnect();
            } catch (IOException e) {
                runOnUiThread(() -> tvResult.setText("HTTP Error: " + e.getMessage()));
            }
        }).start();
    }

    // VULNERABILITY: Disabled SSL/TLS Certificate Validation
    // MobSF Rule: android_insecure_ssl
    // Pattern: javax\.net\.ssl AND (ALLOW_ALL_HOSTNAME_VERIFIER | \.setDefaultHostnameVerifier\( | ...)
    // input_case: exact | type: RegexAnd
    // CWE: CWE-295 | OWASP Mobile: M3 | MASVS: network-3
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

                // Disable hostname verification globally — matches MobSF android_insecure_ssl
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                URL url = new URL("https://example.com");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                runOnUiThread(() -> tvResult.setText("HTTPS Response Code: " + responseCode));
                connection.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> tvResult.setText("HTTPS Error: " + e.getMessage()));
            }
        }).start();
    }

}
