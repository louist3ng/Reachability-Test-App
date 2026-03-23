package com.test.reachability;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExposedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Hidden Admin Panel");
        tvTitle.setTextSize(24);
        layout.addView(tvTitle);

        TextView tvApiKey = new TextView(this);
        tvApiKey.setText("API Key: " + MainActivity.API_KEY);
        tvApiKey.setPadding(0, 16, 0, 0);
        layout.addView(tvApiKey);

        TextView tvDbPassword = new TextView(this);
        tvDbPassword.setText("DB Password: " + MainActivity.DB_PASSWORD);
        tvDbPassword.setPadding(0, 16, 0, 0);
        layout.addView(tvDbPassword);

        setContentView(layout);
    }
}
