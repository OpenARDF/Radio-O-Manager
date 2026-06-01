# Hidden Debug Logging

Radio-Oracle writes a small diagnostic log under app-private storage. The log
is intended for developer troubleshooting and is not exposed in normal user UI.

## Location

The Android app stores logs in:

```shell
/data/data/org.openardf.radiooracle/files/debug-logs/
```

The active file is `debug.log`. Older files are retained as `debug.log.1`,
`debug.log.2`, and so on according to the rolling-log retention policy.

## Current Scope

The first logging slice records low-volume breadcrumbs for:

- app startup;
- USB-device scanning and attach intents;
- SI reader service start/stop;
- SI station probe/connect results;
- card insert and card-read outcomes.

The log should not contain raw live-result payloads, API keys, full imported
files, competitor names, or other broad personal event data.

## Developer Extraction

For debug builds, use `adb run-as`:

```shell
adb shell run-as org.openardf.radiooracle ls -l files/debug-logs
adb exec-out run-as org.openardf.radiooracle cat files/debug-logs/debug.log
```

To reset the hidden debug logs during a test:

```shell
adb shell run-as org.openardf.radiooracle rm -rf files/debug-logs
```
