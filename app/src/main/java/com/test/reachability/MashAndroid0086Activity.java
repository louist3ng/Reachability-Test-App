package com.test.reachability;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;

public class MashAndroid0086Activity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Secure flag demo results will appear here");

        Button btnHttpCookie = new Button(this);
        btnHttpCookie.setText("HttpCookie Without Secure Flag");
        btnHttpCookie.setOnClickListener(v -> addHttpCookieWithoutSecure());

        Button btnOkHttpCookie = new Button(this);
        btnOkHttpCookie.setText("OkHttp Cookie.Builder Without .secure()");
        btnOkHttpCookie.setOnClickListener(v -> buildOkHttpCookieWithoutSecure());

        Button btnWebViewCookie = new Button(this);
        btnWebViewCookie.setText("WebView setCookie Without Secure Directive");
        btnWebViewCookie.setOnClickListener(v -> setWebViewCookieWithoutSecure());

        layout.addView(btnHttpCookie);
        layout.addView(btnOkHttpCookie);
        layout.addView(btnWebViewCookie);
        layout.addView(tvResult);

        setContentView(layout);
    }

    // VULNERABILITY: HttpCookie added to store without setSecure(true)
    // Semgrep Rule: missing_secure_flag_httpcookie (MASH-ANDROID-0086)
    // Pattern: new HttpCookie(...) -> $STORE.add(..., $C) without $C.setSecure(true)
    // CWE: CWE-614 | OWASP Mobile: M5 | MASVS: network-1
    private void addHttpCookieWithoutSecure() {
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        CookieHandler.setDefault(cookieManager);
        CookieStore store = cookieManager.getCookieStore();

        HttpCookie sessionCookie = new HttpCookie("SESSION_ID", "abc123-secret-session");
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(3600);
        sessionCookie.setHttpOnly(true);
        // Missing: sessionCookie.setSecure(true);
        // Cookie may be transmitted over HTTP and intercepted by a network observer
        store.add(URI.create("https://api.example.com"), sessionCookie);

        tvResult.setText("HttpCookie added without Secure flag: " + sessionCookie.getName());
    }

    // VULNERABILITY: OkHttp Cookie.Builder().build() without .secure()
    // Semgrep Rule: missing_secure_flag_okhttp_builder (MASH-ANDROID-0086)
    // Pattern: new Cookie.Builder()...build() without .secure() in chain
    // CWE: CWE-614 | OWASP Mobile: M5 | MASVS: network-1
    private void buildOkHttpCookieWithoutSecure() {
        // Missing: .secure() — cookie will not be restricted to HTTPS transport
        // An HTTP downgrade redirect could expose the auth token in cleartext
        FakeCookie cookie = new FakeCookie.Builder()
                .name("AUTH_TOKEN")
                .value("token-xyz-789-secret")
                .domain("api.example.com")
                .path("/")
                .httpOnly()
                // Missing: .secure()
                .build();

        tvResult.setText("OkHttp Cookie built without .secure(): " + cookie.name());
    }

    // VULNERABILITY: WebView CookieManager.setCookie() without "Secure" directive in cookie string
    // Semgrep Rule: missing_secure_flag_webview_cookiemanager (MASH-ANDROID-0086)
    // Pattern: $CM.setCookie($URL, $COOKIE) where $COOKIE string literal lacks "Secure"
    // CWE: CWE-614 | OWASP Mobile: M5 | MASVS: network-1
    private void setWebViewCookieWithoutSecure() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        String url = "https://api.example.com";

        // Missing: "; Secure" in the cookie string — cookie may be sent over HTTP
        cookieManager.setCookie(url, "AUTH=secret-token-abc; Path=/; HttpOnly");

        // Second variant — instance reference pattern
        CookieManager cm = CookieManager.getInstance();
        cm.setCookie(url, "SESSION=sess-id-12345; Path=/");

        tvResult.setText("WebView setCookie called without Secure directive for:\n" + url);
    }

    // ---------------------------------------------------------------------------
    // Stub classes that mirror the OkHttp Cookie.Builder API surface
    // the Semgrep rule matches against
    // ---------------------------------------------------------------------------

    static class FakeCookie {
        private final String name;
        private final String value;
        private final String domain;
        private final String path;
        private final boolean httpOnly;
        private final boolean secure;

        private FakeCookie(Builder builder) {
            this.name = builder.name;
            this.value = builder.value;
            this.domain = builder.domain;
            this.path = builder.path;
            this.httpOnly = builder.httpOnly;
            this.secure = builder.secure;
        }

        String name() { return name; }
        String value() { return value; }
        String domain() { return domain; }
        boolean secure() { return secure; }

        static class Builder {
            private String name;
            private String value;
            private String domain;
            private String path;
            private boolean httpOnly = false;
            private boolean secure = false;

            Builder name(String name) { this.name = name; return this; }
            Builder value(String value) { this.value = value; return this; }
            Builder domain(String domain) { this.domain = domain; return this; }
            Builder path(String path) { this.path = path; return this; }
            Builder httpOnly() { this.httpOnly = true; return this; }
            Builder secure() { this.secure = true; return this; }

            FakeCookie build() {
                return new FakeCookie(this);
            }
        }
    }

}
