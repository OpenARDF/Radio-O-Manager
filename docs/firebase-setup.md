# Firebase Setup

Radio-Oracle keeps Firebase Analytics and Crashlytics support, but the repository
does not track a Firebase client configuration file. Local and release builds
enable Firebase only when `app/google-services.json` exists.

## OpenARDF Project

Use the Google account `openardf@gmail.com` to create or administer the Firebase
project for Radio-Oracle. Do not reuse the legacy `ardf-manager` Firebase
project or client configuration.

Recommended project/app details:

- Firebase project name: `Radio-Oracle`
- Android package name: `org.openardf.radiooracle`
- Android app nickname: `Radio-Oracle Android`

After creating the Android app in Firebase Console, download its
`google-services.json` and place it at:

```sh
app/google-services.json
```

Keep that file local. It is ignored by Git.

## Verification

Without `app/google-services.json`, normal local builds run without Firebase:

```sh
./gradlew shared:test desktopApp:test app:testDebugUnitTest
```

With `app/google-services.json` present, verify that the Firebase plugins and
Crashlytics wiring still process the Android config:

```sh
./gradlew app:processDebugGoogleServices app:testDebugUnitTest
```
