package com.test.reachability;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SqlActivity extends AppCompatActivity {

    private EditText etUsername;
    private TextView tvQuery;
    private TextView tvResult;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        etUsername = new EditText(this);
        etUsername.setHint("Enter username");

        tvQuery = new TextView(this);
        tvQuery.setText("Query will appear here");

        tvResult = new TextView(this);
        tvResult.setText("Result will appear here");

        Button btnLogin = new Button(this);
        btnLogin.setText("Login");
        btnLogin.setOnClickListener(v -> performLogin());

        layout.addView(etUsername);
        layout.addView(btnLogin);
        layout.addView(tvQuery);
        layout.addView(tvResult);

        setContentView(layout);

        dbHelper = new UserDatabaseHelper(this);
    }

    // VULNERABILITY: SQL Injection via unsanitized user input
    // MobSF Rule: android_sql_raw_query
    // Pattern: android\.database\.sqlite AND (rawQuery\( | execSQL\()
    // input_case: exact | type: RegexAndOr
    // CWE: CWE-89 | OWASP Mobile: M7 | MASVS: (none)
    private void performLogin() {
        String userInput = etUsername.getText().toString();

        // SQL injection — direct string concatenation of user input into raw query
        String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
        tvQuery.setText("Query: " + query);

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                tvResult.setText("Found user: " + username + " / " + password);
            } else {
                tvResult.setText("No user found");
            }
            cursor.close();
        } catch (Exception e) {
            tvResult.setText("SQL Error: " + e.getMessage());
        }
    }

}
