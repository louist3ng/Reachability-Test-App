package com.test.reachability;

// ORPHANED CLASS - reachability test: should NOT be flagged
//
// Dead code patterns extracted from SqlActivity.java.
// None of these methods are ever called or referenced.

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class is never instantiated or referenced anywhere in the project.
 * It contains dead code patterns originally in SqlActivity, extracted here
 * so that reachability analysis tools can be validated against them.
 */
public class DeadSqlActivity {

    // DEAD CODE - reachability test: should NOT be flagged
    // Originally an if(debugMode==1) branch where debugMode was always 0
    // Simulated MobSF Rule: android_sql_raw_query + android_logging
    // Pattern: rawQuery\( AND Log\.(d)
    // CWE: CWE-89, CWE-532 | OWASP Mobile: M7 | MASVS: storage-3
    private void deadDebugDump(SQLiteDatabase db) {
        String dumpQuery = "SELECT * FROM sqlite_master WHERE type='table'";
        Cursor c = db.rawQuery(dumpQuery, null);
        String result = "";
        while (c.moveToNext()) {
            result += c.getString(0) + "\n";
        }
        c.close();
        Log.d("SQLDUMP", result);
    }

    // DEAD CODE - reachability test: should NOT be flagged
    // Originally code after an unconditional return in SqlActivity.executeAdminQuery()
    // Simulated MobSF Rule: android_sql_raw_query
    // Pattern: android\.database\.sqlite AND execSQL\(
    // CWE: CWE-89 | OWASP Mobile: M7 | MASVS: (none)
    private void deadAdminQuery(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("GRANT ALL PRIVILEGES ON *.* TO 'hacker'@'%'");
        db.close();
    }
}