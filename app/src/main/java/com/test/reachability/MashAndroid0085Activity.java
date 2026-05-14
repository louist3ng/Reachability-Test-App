package com.test.reachability;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MashAndroid0085Activity extends AppCompatActivity {

    private static final String TAG = "MashAndroid0085";
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Authorization header demo results will appear here");

        Button btnOkHttpHeader = new Button(this);
        btnOkHttpHeader.setText("OkHttp .header() (No Host Check)");
        btnOkHttpHeader.setOnClickListener(v -> sendWithHeaderNoHostCheck());

        Button btnOkHttpAddHeader = new Button(this);
        btnOkHttpAddHeader.setText("OkHttp .addHeader() (No Host Check)");
        btnOkHttpAddHeader.setOnClickListener(v -> sendWithAddHeaderNoHostCheck());

        Button btnWebView = new Button(this);
        btnWebView.setText("WebView Auth Header (No Host Guard)");
        btnWebView.setOnClickListener(v -> loadWebViewWithAuthHeader());

        Button btnLogAuth = new Button(this);
        btnLogAuth.setText("Log Authorization Header");
        btnLogAuth.setOnClickListener(v -> logAuthorizationHeader());

        layout.addView(btnOkHttpHeader);
        layout.addView(btnOkHttpAddHeader);
        layout.addView(btnWebView);
        layout.addView(btnLogAuth);
        layout.addView(tvResult);

        setContentView(layout);
    }

    // VULNERABILITY: Authorization header attached via .header() without host allowlist check
    // Semgrep Rule: authorization_header_added_without_host_check (MASH-ANDROID-0085)
    // Pattern: $REQ.newBuilder().header("Authorization", ...).build() without host equals check
    // CWE: CWE-522 | OWASP Mobile: M5 | MASVS: network-1
    private void sendWithHeaderNoHostCheck() {
        String bearerToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.secret";
        FakeOkHttpClient client = new FakeOkHttpClient();

        FakeRequest req = new FakeRequest("https://api.example.com/data");

        // Missing: if (req.url().host().equals("api.example.com")) { ... }
        // Without this check, a redirect to a third-party host will forward the bearer token
        FakeRequest authedReq = req.newBuilder()
                .header("Authorization", bearerToken)
                .build();

        String result = client.execute(authedReq);
        tvResult.setText(".header() request sent without host check\n" + result);
    }

    // VULNERABILITY: Authorization header attached via .addHeader() without host allowlist check
    // Semgrep Rule: authorization_header_added_without_host_check (MASH-ANDROID-0085)
    // Pattern: $REQ.newBuilder().addHeader("Authorization", ...).build() without host equals check
    // CWE: CWE-522 | OWASP Mobile: M5 | MASVS: network-1
    private void sendWithAddHeaderNoHostCheck() {
        String bearerToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.secret2";
        FakeOkHttpClient client = new FakeOkHttpClient();

        FakeRequest req = new FakeRequest("https://api.example.com/v2/resource");

        // Missing: if (req.url().host().equalsIgnoreCase("api.example.com")) { ... }
        // Credential will be forwarded to any redirect destination
        FakeRequest authedReq = req.newBuilder()
                .addHeader("Authorization", bearerToken)
                .build();

        String result = client.execute(authedReq);
        tvResult.setText(".addHeader() request sent without host check\n" + result);
    }

    // VULNERABILITY: Authorization header passed to WebView.loadUrl via extra headers map
    // Semgrep Rule: authorization_header_in_webview_loadurl (MASH-ANDROID-0085)
    // Pattern: $MAP.put("Authorization", ...) ... $WV.loadUrl($URL, $MAP)
    // CWE: CWE-522 | OWASP Mobile: M5 | MASVS: network-1
    private void loadWebViewWithAuthHeader() {
        WebView webView = new WebView(this);
        String token = "Bearer secret-webview-token-abc123";

        Map<String, String> extraHeaders = new HashMap<>();
        // Missing: no host allowlist — cross-origin subresource requests may inherit this header
        extraHeaders.put("Authorization", token);

        String url = "https://api.example.com/dashboard";
        webView.loadUrl(url, extraHeaders);

        tvResult.setText("WebView loadUrl called with Authorization header for:\n" + url);
    }

    // VULNERABILITY: Authorization header value written to logcat
    // Semgrep Rule: authorization_header_logged (MASH-ANDROID-0085)
    // Pattern: Log.d(..., "Authorization" + ...) — recoverable by apps with READ_LOGS on API < 26
    // CWE: CWE-522 | OWASP Mobile: M5 | MASVS: network-1
    private void logAuthorizationHeader() {
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.logged-secret-token";

        // Missing: redaction — any app with READ_LOGS can recover the bearer token
        Log.d(TAG, "Authorization" + ": " + token);
        Log.e(TAG, "Authorization" + " header sent: " + token);
        Log.i(TAG, "Request header — " + "Authorization" + " = " + token);
        Log.w(TAG, "Authorization" + " debug value: " + token);
        Log.v(TAG, "Authorization" + " verbose: " + token);

        tvResult.setText("Authorization token logged to logcat (vulnerable)");
    }

    // ---------------------------------------------------------------------------
    // Stub classes that mirror the OkHttp API surface the Semgrep rule matches
    // ---------------------------------------------------------------------------

    static class FakeOkHttpClient {
        String execute(FakeRequest request) {
            // no-op; exists to trigger semgrep pattern on request construction above
            return "Response{code=200, url=" + request.url + "}";
        }
    }

    static class FakeRequest {
        final String url;
        private final java.util.Map<String, String> headers = new java.util.HashMap<>();

        FakeRequest(String url) {
            this.url = url;
        }

        FakeRequest(String url, java.util.Map<String, String> headers) {
            this.url = url;
            this.headers.putAll(headers);
        }

        FakeUrl url() {
            return new FakeUrl(this.url);
        }

        Builder newBuilder() {
            return new Builder(this.url, this.headers);
        }

        static class Builder {
            private final String url;
            private final java.util.Map<String, String> headers = new java.util.HashMap<>();

            Builder(String url, java.util.Map<String, String> existingHeaders) {
                this.url = url;
                this.headers.putAll(existingHeaders);
            }

            Builder header(String name, String value) {
                headers.put(name, value);
                return this;
            }

            Builder addHeader(String name, String value) {
                headers.put(name, value);
                return this;
            }

            FakeRequest build() {
                return new FakeRequest(url, headers);
            }
        }
    }

    static class FakeUrl {
        private final String rawUrl;

        FakeUrl(String rawUrl) {
            this.rawUrl = rawUrl;
        }

        String host() {
            try {
                return new java.net.URL(rawUrl).getHost();
            } catch (Exception e) {
                return "";
            }
        }
    }

}
