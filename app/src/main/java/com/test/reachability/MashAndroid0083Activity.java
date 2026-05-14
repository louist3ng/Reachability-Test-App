package com.test.reachability;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;

public class MashAndroid0083Activity extends AppCompatActivity {

    private TextView tvResult;
    private final FakeHttpServletResponse response = new FakeHttpServletResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Cookie demo results will appear here");

        Button btnCookie = new Button(this);
        btnCookie.setText("Set Cookie (No HttpOnly)");
        btnCookie.setOnClickListener(v -> setCookieWithoutHttpOnly());

        Button btnHttpCookie = new Button(this);
        btnHttpCookie.setText("Set HttpCookie (No HttpOnly)");
        btnHttpCookie.setOnClickListener(v -> setHttpCookieWithoutHttpOnly());

        layout.addView(btnCookie);
        layout.addView(btnHttpCookie);
        layout.addView(tvResult);

        setContentView(layout);
    }

    // VULNERABILITY: Cookie added without HttpOnly flag
    // Semgrep Rule: missing_httponly_cookie_flag (MASH-ANDROID-0083)
    // Pattern: Cookie created and added to response without setHttpOnly(true)
    // CWE: CWE-1004 | OWASP Mobile: M5 | MASVS: network-1
    private void setCookieWithoutHttpOnly() {
        Cookie sessionCookie = new Cookie("SESSION_ID", "abc123secret");
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(3600);
        // Missing: sessionCookie.setHttpOnly(true);
        response.addCookie(sessionCookie);
        tvResult.setText("Cookie set without HttpOnly: " + sessionCookie.getName());
    }

    // VULNERABILITY: HttpCookie added to CookieStore without HttpOnly flag
    // Semgrep Rule: missing_httponly_cookie_flag (MASH-ANDROID-0083)
    // Pattern: HttpCookie created and added to store without setHttpOnly(true)
    // CWE: CWE-1004 | OWASP Mobile: M5 | MASVS: network-1
    private void setHttpCookieWithoutHttpOnly() {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        CookieStore store = cookieManager.getCookieStore();

        HttpCookie authCookie = new HttpCookie("AUTH_TOKEN", "token-xyz-789");
        authCookie.setPath("/");
        authCookie.setMaxAge(7200);
        // Missing: authCookie.setHttpOnly(true);
        store.add(URI.create("https://example.com"), authCookie);
        tvResult.setText("HttpCookie set without HttpOnly: " + authCookie.getName());
    }

    static class Cookie {
        private final String name;
        private final String value;
        private String path;
        private int maxAge = -1;
        private boolean httpOnly = false;

        Cookie(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String getName() { return name; }
        String getValue() { return value; }
        void setPath(String path) { this.path = path; }
        void setMaxAge(int maxAge) { this.maxAge = maxAge; }
        void setHttpOnly(boolean httpOnly) { this.httpOnly = httpOnly; }
    }

    static class FakeHttpServletResponse {
        void addCookie(Cookie cookie) {
            // no-op; exists to trigger semgrep pattern
        }
    }

}
