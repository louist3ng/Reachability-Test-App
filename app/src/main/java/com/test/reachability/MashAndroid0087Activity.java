package com.test.reachability;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MashAndroid0087Activity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("TLS bypass demo results will appear here");

        Button btnTrustManager = new Button(this);
        btnTrustManager.setText("Empty checkServerTrusted (TrustManager)");
        btnTrustManager.setOnClickListener(v -> installEmptyTrustManager());

        Button btnHostnameVerifierClass = new Button(this);
        btnHostnameVerifierClass.setText("Trust-All HostnameVerifier (Class)");
        btnHostnameVerifierClass.setOnClickListener(v -> installTrustAllHostnameVerifierClass());

        Button btnHostnameVerifierLambda = new Button(this);
        btnHostnameVerifierLambda.setText("Trust-All HostnameVerifier (Lambda)");
        btnHostnameVerifierLambda.setOnClickListener(v -> installTrustAllHostnameVerifierLambda());

        layout.addView(btnTrustManager);
        layout.addView(btnHostnameVerifierClass);
        layout.addView(btnHostnameVerifierLambda);
        layout.addView(tvResult);

        setContentView(layout);
    }

    // VULNERABILITY: Custom X509TrustManager with empty checkServerTrusted()
    // Semgrep Rule: empty_check_server_trusted (MASH-ANDROID-0087)
    // Pattern: public void checkServerTrusted(X509Certificate[] $CERTS, String $AUTH) { }
    // CWE: CWE-295 | OWASP Mobile: M5 | MASVS: network-1
    private void installEmptyTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }

                // VULNERABILITY: Empty body accepts every server certificate unconditionally —
                // self-signed, expired, and attacker-controlled certs all pass validation
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            tvResult.setText("Empty TrustManager installed — all server certs accepted");
        } catch (Exception e) {
            tvResult.setText("Error: " + e.getMessage());
        }
    }

    // VULNERABILITY: HostnameVerifier anonymous class returning true unconditionally
    // Semgrep Rule: trust_all_hostname_verifier_class (MASH-ANDROID-0087)
    // Pattern: new HostnameVerifier() { public boolean verify(String $H, SSLSession $S) { return true; } }
    // CWE: CWE-295 | OWASP Mobile: M5 | MASVS: network-1
    private void installTrustAllHostnameVerifierClass() {
        // VULNERABILITY: Accepts any hostname for any certificate — enables MITM attacks
        HostnameVerifier trustAllVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(trustAllVerifier);
        tvResult.setText("Trust-all HostnameVerifier (class) installed — hostname check bypassed");
    }

    // VULNERABILITY: HostnameVerifier lambda returning true unconditionally
    // Semgrep Rule: trust_all_hostname_verifier_lambda (MASH-ANDROID-0087)
    // Pattern: HttpsURLConnection.setDefaultHostnameVerifier(($H, $S) -> true)
    //          $CONN.setHostnameVerifier(($H, $S) -> true)
    //          $BUILDER.hostnameVerifier(($H, $S) -> true)
    // CWE: CWE-295 | OWASP Mobile: M5 | MASVS: network-1
    private void installTrustAllHostnameVerifierLambda() {
        // VULNERABILITY: Lambda always returns true — any hostname accepted regardless of cert
        HttpsURLConnection.setDefaultHostnameVerifier((host, session) -> true);

        try {
            java.net.URL url = new java.net.URL("https://api.example.com/data");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            // VULNERABILITY: Per-connection lambda verifier also accepts any hostname
            conn.setHostnameVerifier((host, session) -> true);

            // OkHttp-style builder pattern — stub builder to trigger third lambda pattern
            FakeOkHttpClientBuilder builder = new FakeOkHttpClientBuilder();
            // VULNERABILITY: OkHttp builder hostnameVerifier lambda accepts any hostname
            builder.hostnameVerifier((host, session) -> true);

            tvResult.setText("Trust-all HostnameVerifier (lambda) installed on default, connection, and builder");
        } catch (Exception e) {
            tvResult.setText("Trust-all lambda set on default connection\nBuilder also configured\n(Error opening conn: " + e.getMessage() + ")");
        }
    }

    // ---------------------------------------------------------------------------
    // Stub class that mirrors OkHttpClient.Builder API surface for the
    // hostnameVerifier(...) lambda pattern the Semgrep rule matches against
    // ---------------------------------------------------------------------------

    static class FakeOkHttpClientBuilder {
        FakeOkHttpClientBuilder hostnameVerifier(HostnameVerifier verifier) {
            // no-op; exists to trigger semgrep pattern on lambda above
            return this;
        }
    }

}
