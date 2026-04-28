package com.test.reachability;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;

import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Demonstrates dynamic DEX loading at runtime via DexClassLoader.
 *
 * SBOM IMPACT: The loaded dependency (dynamic-payload.dex) is NOT declared
 * in build.gradle and does NOT exist at build time. No static SBOM tool
 * (Syft, Blint, CycloneDX) can detect this dependency — it is loaded
 * entirely at runtime from external storage.
 *
 * This pattern is commonly used by:
 * - Plugin frameworks that download extensions at runtime
 * - Malware that loads payloads after installation
 * - Hot-patching / code-push systems
 */
public class DynamicLoaderActivity extends Activity {

    private static final String TAG = "DynamicLoader";

    // Simulated path to a DEX file that would be downloaded or side-loaded at runtime.
    // This file does NOT exist at build time — it represents a runtime-only dependency.
    private static final String DYNAMIC_DEX_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Download/dynamic-payload.dex";

    // The class we expect to find inside the dynamic DEX
    private static final String DYNAMIC_CLASS_NAME = "com.dynamic.payload.EntryPoint";

    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        statusText = new TextView(this);
        statusText.setText("Dynamic Loader: idle");
        layout.addView(statusText);

        Button btnLoad = new Button(this);
        btnLoad.setText("Load Dynamic DEX");
        btnLoad.setOnClickListener(v -> attemptDynamicLoad());
        layout.addView(btnLoad);

        setContentView(layout);
    }

    /**
     * Attempts to load a class from an external DEX file using DexClassLoader.
     * The DEX file is expected at DYNAMIC_DEX_PATH but will not exist in a
     * normal build — this is intentional for SBOM testing.
     */
    private void attemptDynamicLoad() {
        File dexFile = new File(DYNAMIC_DEX_PATH);

        if (!dexFile.exists()) {
            String msg = "DEX not found at: " + DYNAMIC_DEX_PATH
                    + " (expected — this is a simulated runtime dependency)";
            Log.w(TAG, msg);
            statusText.setText(msg);
            return;
        }

        try {
            // Optimized DEX output directory (app-private)
            File optimizedDir = getDir("dex_opt", 0);

            // DexClassLoader: loads classes from .dex or .jar files at runtime
            // This is the core mechanism that makes the dependency invisible to SBOM tools
            DexClassLoader classLoader = new DexClassLoader(
                    dexFile.getAbsolutePath(),
                    optimizedDir.getAbsolutePath(),
                    null,  // no native library search path
                    getClassLoader()
            );

            // Attempt to load the entry point class from the dynamic DEX
            Class<?> entryPointClass = classLoader.loadClass(DYNAMIC_CLASS_NAME);
            Method executeMethod = entryPointClass.getMethod("execute");
            Object result = executeMethod.invoke(null);

            String msg = "Dynamic class loaded successfully: " + result;
            Log.i(TAG, msg);
            statusText.setText(msg);

        } catch (Exception e) {
            String msg = "Dynamic load failed: " + e.getMessage();
            Log.e(TAG, msg, e);
            statusText.setText(msg);
        }
    }
}
