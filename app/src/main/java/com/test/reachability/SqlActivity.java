package com.test.reachability;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
        btnLogin.setOnLongClickListener(v -> {
            executeAdminQuery();
            return true;
        });

        layout.addView(etUsername);
        layout.addView(btnLogin);
        layout.addView(tvQuery);
        layout.addView(tvResult);

        setContentView(layout);

        dbHelper = new UserDatabaseHelper(this);
    }

    // VULNERABILITY: SQL Injection via unsanitized user input
    // OWASP Mobile Top 10 2024: M4 (Insufficient Input/Output Validation)
    // MASVS: MASVS-CODE (Injection Flaws — SQL Injection)
    // MASTG: MASTG-ANDROID-CODE (Testing for SQL Injection)
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

            int debugMode = 0; // hardcoded, never reassigned
            if (debugMode == 1) {
                // DEAD BRANCH - reachability test: should NOT be flagged
                // Simulated Weakness: M4 (Insufficient Input/Output Validation)
                // MASVS: MASVS-CODE (Information Disclosure via Raw SQL Schema Dump)
                // MASTG: MASTG-ANDROID-CODE (Testing for SQL Injection)
                String dumpQuery = "SELECT * FROM sqlite_master WHERE type='table'";
                Cursor c = db.rawQuery(dumpQuery, null);
                String result = "";
                while (c.moveToNext()) {
                    result += c.getString(0) + "\n";
                }
                Log.d("SQLDUMP", result);
            }
        } catch (Exception e) {
            tvResult.setText("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Destructive admin query - contains early return making all code after it dead.
     */
    private void executeAdminQuery() {
        if (true) {
            return; // always exits here
        }
        // DEAD CODE AFTER RETURN - reachability test: should NOT be flagged
        // Simulated Weakness: M4 (Insufficient Input/Output Validation)
        // MASVS: MASVS-CODE (Destructive SQL Execution)
        // MASTG: MASTG-ANDROID-CODE (Testing for SQL Injection)
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("GRANT ALL PRIVILEGES ON *.* TO 'hacker'@'%'");
        db.close();
    }
}
