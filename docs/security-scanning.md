# Security Scanning

Run Gitleaks before publishing security-sensitive changes:

```sh
gitleaks detect --no-banner --redact --source .
```

The repository intentionally tracks `app/google-services.json` because the
Android Firebase and Crashlytics build plugins require that client configuration
file during normal app builds. Firebase Android API keys identify the Firebase
project to client SDKs, but they are not authentication secrets by themselves.

The `.gitleaksignore` entry is limited to the existing `gcp-api-key` fingerprint
for that file. New findings, different files, or changed Firebase configuration
values should still be reviewed instead of broadly allowlisted.
