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

    private void fetchHttp() {
        new Thread(() -> {
            try {
                URL url = new URL("http://example.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                runOnUiThread(() -> tvResult.setText("HTTP Response Code: " + responseCode));
                connection.disconnect();
            } catch (IOException e) {
                runOnUiThread(() -> tvResult.setText("HTTP Error: " + e.getMessage()));
                sendToAnalytics("http://example.com");
            }
        }).start();
    }

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
     * Sends data to an internal analytics endpoint.
     * Called from exception handlers - non-obvious reachability path.
     */
    private void sendToAnalytics(String url) {
        new Thread(() -> {
            try {
                URL analyticsUrl = new URL("http://analytics.internal.corp/track?url=" + url);
                HttpURLConnection connection = (HttpURLConnection) analyticsUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
                connection.disconnect();
            } catch (IOException ignored) {
            }
        }).start();
    }
}