# Tally - Subscription Management App
Tally is an offline-first subscription manager, built with Kotlin Multiplatform and Compose
Multiplatform, targeting Android (primary) and iOS.

Every screen, every mutation, and every renewal reminder works with zero network connectivity.
Currency conversion is the one feature that reaches the internet — and it degrades gracefully
to cached or no conversion when it can't.

| Dashboard | Add / Edit | Alerts | Settings |
|:---:|:---:|:---:|:---:|
| <img width="220" src="https://github.com/user-attachments/assets/5a5c577a-6c76-40ed-b092-bdff4ff6f473" alt="Dashboard"> | <img width="220" src="https://github.com/user-attachments/assets/b468b622-41e2-41fa-899b-af9c2d4e82a7" alt="Add / Edit"> | <img width="220" src="https://github.com/user-attachments/assets/f53b6fe9-69c8-438c-ad1b-2cd7abecf2d5" alt="Alerts"> | <img width="220" src="https://github.com/user-attachments/assets/42ed28b4-7192-4168-b90b-df426623d3fa" alt="Settings"> |

## Features

- **Dashboard** — this month's total (cadence-normalized: yearly subscriptions contribute
  price ÷ 12, not the naive sticker price), annual total, active count, renewing-soon list,
  and a sortable list of every subscription.


- **Upcoming** — a 7/30-day/all-time filtered, bucketed view of what's charging next.
- **Add / Edit** — full CRUD with a category grid, billing-cadence toggle, and a real date

  picker (not free-text parsing). Editing autosaves as you go.
- **Alerts** — every non-paused subscription's reminder, toggleable inline, with a "renews in
  N days · absolute date · amount" line per row.

- **Settings** — dark mode (with a "follow system" default), high contrast, text size, larger
  tap targets, bold text, reduced motion, colorblind-safe labels, currency, and default
  reminder lead time/time-of-day. Every toggle applies live, app-wide.

- **Currency conversion** — subscriptions billed in a currency other than your active one are
  converted into it for the dashboard totals, using a cached exchange rate fetched over HTTPS.
  Offline or on a fetch failure, it falls back to the last cached rate (or excludes that
  subscription from the total) rather than blocking or crashing.

## Stack

- Kotlin Multiplatform 2.4.10 + Compose Multiplatform 1.11.1
- Compose Navigation, Room (KMP) as the local source of truth, DataStore (KMP) for settings
- Ktor Client for the FX-rate lookup
- kotlinx.serialization, kotlinx.coroutines, kotlinx-datetime
- Koin for dependency injection
- AndroidX Lifecycle ViewModel (KMP)

Android `minSdk` 26, `targetSdk`/`compileSdk` 37.

## Project layout

```
composeApp/src/commonMain/kotlin/app/tally
├── ui/{theme,components,screens,nav}         # Compose UI
├── domain/{model,usecase}                    # Pure business logic (no Compose/platform deps)
├── data/{local,remote,settings,repository}   # Room, DataStore, Ktor + repositories
├── platform                                  # expect decls; actuals in androidMain/iosMain
└── money                                     # Currency table, formatting, parsing

composeApp/src/androidMain/...   # Android actuals (Credential Manager, WorkManager, Keystore)
composeApp/src/iosMain/...       # iOS actuals (Keychain, NSDateFormatter, ...)
androidApp/                      # Android entry point, manifest
iosApp/                          # Xcode project, entry point
release/                         # Built APKs
```

## Building and running

### Android

```
./gradlew :androidApp:assembleDebug
```

Installs via `adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk`, or open
the project root in Android Studio and run the `androidApp` configuration.

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run, or from the command line:

```
./gradlew :composeApp:compileKotlinIosArm64
```

### Tests

```
./gradlew :composeApp:testDebugUnitTest
```

All business logic (money math, cadence normalization, date derivation, currency conversion,
the FX-rate cache) is covered by common (platform-independent) unit tests.

## Notes

- Local reminders are 100% on-device — no push server, no notification dependency on
  connectivity.
- Money is always integer minor units + an ISO 4217 currency code, never `Float`/`Double`,
  except where an FX rate is genuinely a ratio (currency conversion) — even there, a value is
  only rounded once, at the point it's finally displayed.
- The bundled Figtree font ships under the SIL Open Font License (`licenses/Figtree-OFL.txt`).
