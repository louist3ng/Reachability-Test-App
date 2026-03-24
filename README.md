# ReachabilityTestApp

**WARNING: This app is intentionally vulnerable and is for authorized security research and testing only. Do NOT publish or deploy this application.**

## Purpose

This Android application serves as a deliberately vulnerable target for security testing, including:
- **SAST (Static Application Security Testing)** — Contains real, reachable vulnerabilities mapped to MobSF rules with verified regex patterns, CWEs, and OWASP/MASVS references
- **Reachability analysis** — Dead code patterns are separated into dedicated orphaned classes, allowing tools to be validated on whether they can distinguish reachable vulnerabilities from unreachable dead code
- **Android component analysis** — Exported activities and services with no permission guards

## Intentional Vulnerabilities

Each vulnerability is mapped to a specific **MobSF `android_rules.yaml`** rule, ensuring detection by MobSF's regex-based source code scanner. The rule ID, regex pattern, CWE, OWASP Mobile category, and MASVS control are listed for each.

### 1. Hardcoded Credentials (`MainActivity.java`)
- `API_KEY` and `DB_PASSWORD` are hardcoded as public constants
- Referenced in `logCredentials()` and displayed in `ExposedActivity`
- **Reachability test**: Trace data flow from constants → log output → UI display
- **MobSF Rule**: `android_hardcoded` — `(password\s*=\s*['|"].{1,100}['|"])|(key\s*=\s*['|"].{1,100}['|"])`
- **CWE**: CWE-312 | **OWASP Mobile**: M9 | **MASVS**: storage-14

### 1b. Credential Logging (`MainActivity.java`)
- `logCredentials()` writes API key and DB password to Android system log via `Log.d()`
- **MobSF Rule**: `android_logging` — `Log\.(v|d|i|w|e|f|s)`
- **CWE**: CWE-532 | **MASVS**: storage-3

### 2. Cleartext HTTP with IP Disclosure (`NetworkActivity.java`)
- Plain HTTP request to hardcoded IP `http://10.0.2.2/api/data` via `fetchHttp()`
- Network security config allows cleartext for all domains (`network_security_config.xml`)
- **Reachability test**: Identify all HTTP (non-HTTPS) network calls with embedded IP addresses
- **MobSF Rule**: `android_ip_disclosure` — IP address regex pattern
- **CWE**: CWE-200 | **MASVS**: code-2

### 3. Disabled SSL Validation (`NetworkActivity.java`)
- `fetchHttpsNoPinning()` uses a custom TrustManager that accepts all certificates
- Hostname verification globally disabled via `HttpsURLConnection.setDefaultHostnameVerifier()`
- **Reachability test**: Trace TrustManager implementations that skip validation
- **MobSF Rule**: `android_insecure_ssl` — `javax\.net\.ssl` AND `\.setDefaultHostnameVerifier\(`
- **CWE**: CWE-295 | **OWASP Mobile**: M3 | **MASVS**: network-3

### 4. Insecure Storage (`StorageActivity.java`)
- SharedPreferences opened with `MODE_WORLD_READABLE` — any app can read
- Secret token written to external storage as plaintext (`secrets.txt`)
- `logSensitiveData()` logs the secret via `Log.d()` with tag "SECRETS"
- **Reachability test**: Trace sensitive data flow to insecure storage and log sinks
- **MobSF Rules**:
  - `android_world_readable` — `MODE_WORLD_READABLE` | **CWE**: CWE-276 | **OWASP Mobile**: M2 | **MASVS**: storage-2
  - `android_read_write_external` — `\.getExternalStorage` | **CWE**: CWE-276 | **OWASP Mobile**: M2 | **MASVS**: storage-2
  - `android_logging` — `Log\.(d)` | **CWE**: CWE-532 | **MASVS**: storage-3

### 5. SQL Injection (`SqlActivity.java`)
- `performLogin()` builds SQL query via direct string concatenation with user input
- No parameterized queries or input sanitization
- **Reachability test**: Trace user input from EditText → raw SQL query execution
- **MobSF Rule**: `android_sql_raw_query` — `android\.database\.sqlite` AND (`rawQuery\(` | `execSQL\(`)
- **CWE**: CWE-89 | **OWASP Mobile**: M7

### 6. Exported Activity with Hidden Data (`ExposedActivity.java`)
- Declared `exported="true"` with a custom intent filter, no permission checks
- Displays hardcoded credentials to any caller
- Contains hidden `View.GONE` element with backup secret in view hierarchy
- **Reachability test**: Identify exported components accessible by external apps
- **MobSF Rules**:
  - `android_hiddenui` — `setVisibility\(View\.GONE\)` | **CWE**: CWE-919 | **OWASP Mobile**: M1 | **MASVS**: storage-7
  - `android_hardcoded` — `secret\s*=\s*['"].{1,100}['"]` (hidden backup secret) | **CWE**: CWE-312 | **OWASP Mobile**: M9 | **MASVS**: storage-14

### 7. Exported Service with Logging (`PhantomService.java`)
- Service declared `exported="true"` with no permissions
- Logs all incoming intent data to system log, leaking caller information
- **Reachability test**: Identify exported components with no permission protection
- **MobSF Rule**: `android_logging` — `Log\.(d)`
- **CWE**: CWE-532 | **MASVS**: storage-3

### 8. Database Helper with Raw SQL (`UserDatabaseHelper.java`)
- Creates user table via `execSQL()` with hardcoded seed credentials (`admin` / `password123`)
- Used by `SqlActivity` — reachable through the app's normal flow
- **MobSF Rule**: `android_sql_raw_query` — `android\.database\.sqlite` AND `execSQL\(`
- **CWE**: CWE-89 | **OWASP Mobile**: M7

## ADB Commands

Trigger ExposedActivity from another app or ADB:

```bash
# By component name
adb shell am start -n com.test.reachability/.ExposedActivity

# By intent action
adb shell am start -a com.test.reachability.ADMIN

# Start the phantom service
adb shell am startservice -n com.test.reachability/.PhantomService
```

## Build

```bash
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Dead Code Test Cases

All dead code patterns have been **separated into dedicated files** for cleaner validation.
Vulnerability files (e.g., `NetworkActivity.java`) now contain only reachable vulnerabilities.
Dead code lives in orphaned classes that are never instantiated or referenced.

A correctly functioning reachability analysis tool should **NOT** flag any of these as reachable issues.

Each dead code pattern is mapped to the **MobSF `android_rules.yaml`** rule it *simulates* (if it were reachable).

### Dedicated Dead Code Files

| File | Origin | Description |
|---|---|---|
| `DeadMainActivity.java` | Extracted from `MainActivity.java` | `leakCredentialsToUrl()`, `nukeDatabase()`, `writeCredsToExternalStorage()` |
| `DeadNetworkActivity.java` | Extracted from `NetworkActivity.java` | `if(false)` HTTP branch, `sendToAnalytics()` dead code after throw |
| `DeadStorageActivity.java` | Extracted from `StorageActivity.java` | `conditionallyDeleteSecrets()` dead branch (shouldDelete=false) |
| `DeadSqlActivity.java` | Extracted from `SqlActivity.java` | `if(debugMode==1)` dead branch, `executeAdminQuery()` dead code after return |
| `DeadAdminClient.java` | Original orphaned class | Hardcoded credentials, IP disclosure, contact exfiltration |
| `LegacyDataUploader.java` | Original orphaned class | External storage read, IP disclosure, hardcoded secret |

### Dead Code Pattern Details

| Pattern | File | Simulated Vuln | MobSF Rule IDs | Regex Patterns | Expected Result |
|---|---|---|---|---|---|
| Dead method: `leakCredentialsToUrl()` | DeadMainActivity.java | Credential exfil via HTTP | `android_hardcoded` + `android_ip_disclosure` + `android_logging` | `key\s*=\s*['"].{1,100}['"]` + IP regex (10.0.2.2) + `Log\.(e)` | NOT reachable |
| Dead method: `nukeDatabase()` | DeadMainActivity.java | Destructive DB operation | `android_sql_raw_query` | `android\.database\.sqlite` AND `execSQL\(` | NOT reachable |
| Dead method: `writeCredsToExternalStorage()` | DeadMainActivity.java | Insecure file write | `android_read_write_external` + `android_logging` | `\.getExternalStorage` + `Log\.(e)` | NOT reachable |
| Dead method: `deadHttpBranch()` | DeadNetworkActivity.java | Credential in URL param | `android_ip_disclosure` + `android_logging` | IP regex (192.168.1.1) + `Log\.(e)` | NOT reachable |
| Dead method: `deadAnalyticsEndpoint()` | DeadNetworkActivity.java | Cred exfil in analytics | `android_ip_disclosure` | IP regex (172.16.0.5) | NOT reachable |
| Dead method: `deadDeleteSecrets()` | DeadStorageActivity.java | Secret log + file wipe | `android_logging` + `android_read_write_external` | `Log\.(d)` + `\.getExternalStorage` | NOT reachable |
| Dead method: `deadDebugDump()` | DeadSqlActivity.java | Schema dump via raw SQL | `android_sql_raw_query` + `android_logging` | `rawQuery\(` + `Log\.(d)` | NOT reachable |
| Dead method: `deadAdminQuery()` | DeadSqlActivity.java | Destructive SQL | `android_sql_raw_query` | `android\.database\.sqlite` AND `execSQL\(` | NOT reachable |
| Orphaned class: `DeadAdminClient` | DeadAdminClient.java | HTTP auth + contact exfil | `android_hardcoded` + `android_ip_disclosure` + `android_logging` | `username\s*=\s*['"]...['"]` + `password\s*=\s*['"]...['"]` + IP regex (10.0.0.1) + `Log\.(d\|e)` | NOT reachable |
| Orphaned class: `LegacyDataUploader` | LegacyDataUploader.java | Secrets upload via HTTP | `android_hardcoded` + `android_read_write_external` + `android_write_app_dir` + `android_ip_disclosure` + `android_logging` | `secret\s*=\s*['"]...['"]` + `\.getExternalStorage` + `Context\.MODE_PRIVATE` + IP regex (192.168.50.10) + `Log\.(e)` | NOT reachable |
