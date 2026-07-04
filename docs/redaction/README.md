# Redaction Engine

The redaction engine replaces sensitive values before text is sent to AI providers, logs, analytics, crash reports, or other sinks.

## Status

Implemented:

* default redaction through `PrivacyRedactor`,
* scanner-backed redaction,
* caller-provided finding redaction,
* custom detector support,
* `REPLACE`, `MASK`, `HASH`, and `DROP` modes,
* overlap handling for caller-provided findings,
* unit tests for redaction behavior.

## Usage

```kotlin
import dev.randos.promptguard.redaction.PrivacyRedactor

val redacted = PrivacyRedactor.redact(
    "My email is user@example.com"
)
```

Output:

```text
My email is [EMAIL_1]
```

## Redaction Modes

```kotlin
import dev.randos.promptguard.redaction.PrivacyRedactor
import dev.randos.promptguard.type.RedactionMode

val masked = PrivacyRedactor.redact(
    text = "My email is user@example.com",
    mode = RedactionMode.MASK
)
```

| Mode | Behavior | Example |
| --- | --- | --- |
| `REPLACE` | Replaces each finding with a stable type placeholder. | `[EMAIL_1]` |
| `MASK` | Keeps limited readable context for supported values. | `u***@example.com` |
| `HASH` | Replaces each finding with a SHA-256 digest. | `sha256:b4c9...` |
| `DROP` | Removes each finding completely. | empty string |

Placeholders are counted per sensitive data type in source order:

```text
user@example.com admin@example.com token abc123xyz...
```

becomes:

```text
[EMAIL_1] [EMAIL_2] token [TOKEN_1]
```

## Custom Detectors

Pass custom detectors when a workflow needs project-specific rules:

```kotlin
val redacted = PrivacyRedactor.redact(
    text = "custom private value",
    detectors = listOf(customDetector)
)
```

## Existing Findings

If text has already been scanned, pass the findings directly:

```kotlin
val findings = SensitiveDataScanner.scan(text)
val redacted = PrivacyRedactor.redact(
    text = text,
    findings = findings
)
```

Invalid caller-provided source ranges are ignored. Overlapping caller-provided findings are resolved the same way as scanner findings: higher severity wins, then the longer match wins.

## Development

Run redaction tests with the module test task:

```bash
./gradlew :prompt-guard:test
```
