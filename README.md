# ReachabilityTestApp

**WARNING: This app is intentionally vulnerable and is for authorized security research and testing only. Do NOT publish or deploy this application.**

## Purpose

This Android application serves as a deliberately vulnerable target for reachability analysis testing, including:
- Network reachability analysis
- Code/call graph reachability analysis
- Android component reachability analysis

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

### 4. Hidden Analytics Endpoint (`NetworkActivity.java`)
- `sendToAnalytics()` contains HTTP call to hardcoded IP `http://172.16.0.5/track`
- Method is called from exception handlers but throws immediately (dead code after throw)
- **Reachability test**: Discover network calls hidden in non-obvious code paths (exception handlers)
- **MobSF Rule**: `android_ip_disclosure` — IP address regex pattern
- **CWE**: CWE-200 | **MASVS**: code-2

### 5. Insecure Storage (`StorageActivity.java`)
- SharedPreferences opened with `MODE_WORLD_READABLE` — any app can read
- Secret token written to external storage as plaintext (`secrets.txt`)
- `logSensitiveData()` logs the secret via `Log.d()` with tag "SECRETS"
- **Reachability test**: Trace sensitive data flow to insecure storage and log sinks
- **MobSF Rules**:
  - `android_world_readable` — `MODE_WORLD_READABLE` | **CWE**: CWE-276 | **OWASP Mobile**: M2 | **MASVS**: storage-2
  - `android_read_write_external` — `\.getExternalStorage` | **CWE**: CWE-276 | **OWASP Mobile**: M2 | **MASVS**: storage-2
  - `android_logging` — `Log\.(d)` | **CWE**: CWE-532 | **MASVS**: storage-3

### 6. SQL Injection (`SqlActivity.java`)
- `performLogin()` builds SQL query via direct string concatenation with user input
- No parameterized queries or input sanitization
- **Reachability test**: Trace user input from EditText → raw SQL query execution
- **MobSF Rule**: `android_sql_raw_query` — `android\.database\.sqlite` AND (`rawQuery\(` | `execSQL\(`)
- **CWE**: CWE-89 | **OWASP Mobile**: M7

### 7. Destructive Hidden Query (`SqlActivity.java`)
- `executeAdminQuery()` contains `DROP TABLE` via `execSQL()` (dead code after early return)
- Only reachable via long-press on the Login button (non-obvious reachability path)
- **Reachability test**: Discover destructive DB operations through UI event analysis
- **MobSF Rule**: `android_sql_raw_query` — `android\.database\.sqlite` AND `execSQL\(`
- **CWE**: CWE-89 | **OWASP Mobile**: M7

### 8. Exported Activity with Hidden Data (`ExposedActivity.java`)
- Declared `exported="true"` with a custom intent filter, no permission checks
- Displays hardcoded credentials to any caller
- Contains hidden `View.GONE` element with backup secret in view hierarchy
- **Reachability test**: Identify exported components accessible by external apps
- **MobSF Rule**: `android_hiddenui` — `setVisibility\(View\.GONE\)`
- **CWE**: CWE-919 | **OWASP Mobile**: M1 | **MASVS**: storage-7

### 9. Exported Service with Logging (`PhantomService.java`)
- Service declared `exported="true"` with no permissions
- Logs all incoming intent data to system log, leaking caller information
- **Reachability test**: Identify exported components with no permission protection
- **MobSF Rule**: `android_logging` — `Log\.(d)`
- **CWE**: CWE-532 | **MASVS**: storage-3

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

The following intentionally unreachable/dead vulnerable code patterns have been injected.
A correctly functioning reachability analysis tool should **NOT** flag any of these as reachable issues.

Each dead code pattern is mapped to the **MobSF `android_rules.yaml`** rule it *simulates* (if it were reachable).

| Pattern | File | Simulated Vuln | MobSF Rule ID | Regex Pattern | Expected Result |
|---|---|---|---|---|---|
| Dead method: `leakCredentialsToUrl()` | MainActivity.java | Credential exfil via HTTP | `android_hardcoded` + `android_ip_disclosure` | `key\s*=\s*['"].{1,100}['"]` + IP regex | NOT reachable |
| Dead method: `nukeDatabase()` | MainActivity.java | Destructive DB operation | `android_sql_raw_query` | `android\.database\.sqlite` AND `execSQL\(` | NOT reachable |
| Dead method: `writeCredsToExternalStorage()` | MainActivity.java | Insecure file write | `android_read_write_external` | `\.getExternalStorage` | NOT reachable |
| `if(false)` branch | NetworkActivity.java | Credential in URL param | `android_ip_disclosure` | IP address regex (192.168.1.1) | NOT reachable |
| `if(shouldDelete==false)` branch | StorageActivity.java | Secret log + file wipe | `android_logging` + `android_read_write_external` | `Log\.(d)` + `\.getExternalStorage` | NOT reachable |
| `if(debugMode==1)` branch | SqlActivity.java | Schema dump via raw SQL | `android_sql_raw_query` + `android_logging` | `rawQuery\(` + `Log\.(d)` | NOT reachable |
| Orphaned class: `DeadAdminClient` | DeadAdminClient.java | HTTP auth + contact exfil | `android_hardcoded` + `android_ip_disclosure` + `android_logging` | `password\s*=\s*['"].{1,100}['"]` + IP regex + `Log\.(d\|e)` | NOT reachable |
| Orphaned class: `LegacyDataUploader` | LegacyDataUploader.java | Secrets upload via HTTP | `android_read_write_external` + `android_write_app_dir` + `android_ip_disclosure` | `\.getExternalStorage` + `Context\.MODE_PRIVATE` + IP regex | NOT reachable |
| Code after return | SqlActivity.java | Destructive SQL | `android_sql_raw_query` | `android\.database\.sqlite` AND `execSQL\(` | NOT reachable |
| Code after `if(true){throw}` | NetworkActivity.java | Cred exfil in analytics | `android_ip_disclosure` | IP address regex (172.16.0.5) | NOT reachable |
