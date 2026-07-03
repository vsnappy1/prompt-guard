# Sensitive Data Scanner

The scanner is the currently implemented PromptGuard MVP feature. It detects common sensitive values in text before that text is sent to AI providers, logs, analytics, crash reports, or other sinks.

## Status

Implemented:

* default sensitive data scanner,
* regex-based detectors for common sensitive values,
* source position metadata,
* severity metadata,
* custom detector support,
* overlap deduplication,
* unit tests for the scanner and each detector.

## Usage

```kotlin
import dev.randos.promptguard.scanner.SensitiveDataScanner

val findings = SensitiveDataScanner.scan(
    "Contact user@example.com with token abc123xyz..."
)
```

Each result is a `SensitiveFinding`:

```kotlin
SensitiveFinding(
    type = SensitiveDataType.EMAIL,
    value = "user@example.com",
    startIndex = 8,
    endIndex = 24,
    severity = Severity.MEDIUM
)
```

Blank input returns an empty list.

## Default Detectors

| Type | Severity | Example |
| --- | --- | --- |
| `AUTHORIZATION_HEADER` | `HIGH` | `Authorization: Bearer sk_live_123456789` |
| `API_KEY` | `HIGH` | `api_key = "secret-key-12345"` |
| `PASSWORD` | `HIGH` | `password: correct-horse` |
| `EMAIL` | `MEDIUM` | `user@example.com` |
| `URL_WITH_SENSITIVE_QUERY` | `HIGH` | `https://example.com/callback?token=abc123` |
| `CREDIT_CARD` | `HIGH` | `4111 1111 1111 1111` |
| `PHONE_NUMBER` | `LOW` | `(312) 555-0198` |
| `TOKEN` | `HIGH` | `token abc123xyz...` |

Credit card matches must pass Luhn validation before they are returned.

## Custom Detectors

Pass a custom detector list to `SensitiveDataScanner.scan` when a workflow needs project-specific detection rules:

```kotlin
import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.scanner.SensitiveDataScanner
import dev.randos.promptguard.scanner.SensitiveDetector
import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

val detector = SensitiveDetector { text ->
    val startIndex = text.indexOf("private")
    if (startIndex == -1) {
        emptyList()
    } else {
        listOf(
            SensitiveFinding(
                type = SensitiveDataType.TOKEN,
                value = "private",
                startIndex = startIndex,
                endIndex = startIndex + "private".length,
                severity = Severity.LOW
            )
        )
    }
}

val findings = SensitiveDataScanner.scan(
    text = "custom private value",
    detectors = listOf(detector)
)
```

## Ordering And Overlaps

`SensitiveDataScanner.scan` returns findings in source order.

When detector results overlap, the scanner keeps one finding:

* higher severity wins,
* when severity matches, the longer match wins.

This avoids returning broad matches, such as authorization headers or sensitive URLs, alongside smaller overlapping token-like fragments.

## Development

Run scanner and detector tests with the module test task:

```bash
./gradlew :prompt-guard:test
```
