package com.test.reachability;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MashAndroid0084Activity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Path traversal demo results will appear here");

        Button btnFileRead = new Button(this);
        btnFileRead.setText("Read File (No Canonicalization)");
        btnFileRead.setOnClickListener(v -> readFileWithoutCanon());

        Button btnFileWrite = new Button(this);
        btnFileWrite.setText("Write File (No Canonicalization)");
        btnFileWrite.setOnClickListener(v -> writeFileWithoutCanon());

        Button btnNewFile = new Button(this);
        btnNewFile.setText("New File (No Canonicalization)");
        btnNewFile.setOnClickListener(v -> newFileWithoutCanon());

        layout.addView(btnFileRead);
        layout.addView(btnFileWrite);
        layout.addView(btnNewFile);
        layout.addView(tvResult);

        setContentView(layout);
    }

    // VULNERABILITY: User-controlled path used in FileInputStream without canonicalization
    // Semgrep Rule: relative_path_traversal_file_access (MASH-ANDROID-0084)
    // Pattern: String from getStringExtra -> new FileInputStream(base + user)
    // CWE: CWE-23 | OWASP Mobile: M9 | MASVS: storage-1
    private void readFileWithoutCanon() {
        Intent intent = getIntent();
        String userPath = intent.getStringExtra("file_name");
        if (userPath == null) userPath = "../../etc/passwd";

        String baseDir = getFilesDir().getAbsolutePath() + "/documents/";
        try {
            // Missing: canonical path check — attacker can use ../ to escape base directory
            FileInputStream fis = new FileInputStream(baseDir + userPath);
            byte[] data = new byte[1024];
            int bytesRead = fis.read(data);
            fis.close();
            tvResult.setText("Read " + bytesRead + " bytes from: " + baseDir + userPath);
        } catch (Exception e) {
            tvResult.setText("Read attempt: " + baseDir + userPath + "\n(Error: " + e.getMessage() + ")");
        }
    }

    // VULNERABILITY: User-controlled path used in FileOutputStream without canonicalization
    // Semgrep Rule: relative_path_traversal_file_access (MASH-ANDROID-0084)
    // Pattern: String from getStringExtra -> new FileOutputStream(base + user)
    // CWE: CWE-23 | OWASP Mobile: M9 | MASVS: storage-1
    private void writeFileWithoutCanon() {
        Intent intent = getIntent();
        String userPath = intent.getStringExtra("output_name");
        if (userPath == null) userPath = "../../data/malicious.txt";

        String baseDir = getFilesDir().getAbsolutePath() + "/uploads/";
        try {
            // Missing: canonical path check — attacker can write outside intended directory
            FileOutputStream fos = new FileOutputStream(baseDir + userPath);
            fos.write("attacker-controlled-content".getBytes());
            fos.close();
            tvResult.setText("Wrote file to: " + baseDir + userPath);
        } catch (Exception e) {
            tvResult.setText("Write attempt: " + baseDir + userPath + "\n(Error: " + e.getMessage() + ")");
        }
    }

    // VULNERABILITY: User-controlled path used in new File() without canonicalization
    // Semgrep Rule: relative_path_traversal_file_access (MASH-ANDROID-0084)
    // Pattern: String from getStringExtra -> new File(base, user)
    // CWE: CWE-23 | OWASP Mobile: M9 | MASVS: storage-1
    private void newFileWithoutCanon() {
        Intent intent = getIntent();
        String userPath = intent.getStringExtra("doc_path");
        if (userPath == null) userPath = "../../../sensitive_data.db";

        String baseDir = getFilesDir().getAbsolutePath() + "/docs/";
        // Missing: canonical path check — attacker can traverse outside base directory
        File targetFile = new File(baseDir, userPath);
        tvResult.setText("File resolved to: " + targetFile.getAbsolutePath()
                + "\nExists: " + targetFile.exists());
    }

}
