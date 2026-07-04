# PromptGuard

[![Maven Central](https://img.shields.io/maven-central/v/dev.randos/prompt-guard.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/dev.randos/prompt-guard)
<a href="https://github.com/vsnappy1/secure-flow/actions"><img alt="Build Status" src="https://github.com/vsnappy1/secure-flow/workflows/Android%20CI/badge.svg"/></a>
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.21-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![JVM](https://img.shields.io/badge/JVM-8%2B-orange.svg)](https://www.oracle.com/java/)

PromptGuard is a Kotlin-first toolkit for building privacy-aware AI workflows in Android and JVM applications.

The goal is simple: help developers detect sensitive values, redact them, build safer AI requests, avoid raw prompt logging, and validate AI output before it is displayed or stored.

## Current Status

PromptGuard is currently in early MVP development.

The first implemented capability is runtime sensitive data detection. This scanner checks text for common sensitive values before that text is sent to AI providers, logs, analytics, crash reports, or other sinks.

Implemented:

* Sensitive Data Detection
* Default regex-based detectors
* Custom detector support
* Source position metadata
* Severity metadata
* Overlap deduplication

Planned next:

* Redaction Engine
* Privacy-Aware Prompt Request Builder
* Safe Metadata Logging
* AI Output Validation
* Configurable privacy policies
* Optional provider adapters

## Why This Exists

AI features often handle data that should not be copied into prompts, logs, crash reports, analytics events, or generated responses.

Common examples include:

* emails and phone numbers,
* access tokens and API keys,
* authorization headers,
* credit-card-like values,
* URLs with sensitive query parameters,
* password-like key/value pairs,
* user-provided financial or health text.

PromptGuard provides reusable runtime building blocks for safer AI data handling. It is provider-neutral, so sanitized requests can be sent to OpenAI, Gemini, Claude, local models, internal AI services, or any other provider.

## MVP Scope

The MVP focuses on five practical features.

### 1. Sensitive Data Detection

Status: ✅ implemented. See the [Sensitive Data Scanner docs](docs/scanner/README.md).

Scan text for common sensitive patterns before it is used in an AI request.

```kotlin
val findings = SensitiveDataScanner.scan(
    "Contact user@example.com with token abc123..."
)
```

Example result:

```kotlin
listOf(
    SensitiveFinding(type = SensitiveDataType.EMAIL, value = "user@example.com"),
    SensitiveFinding(type = SensitiveDataType.TOKEN, value = "abc123...")
)
```

### 2. Redaction Engine

Replace or mask sensitive values before prompt construction.

```kotlin
val redacted = PrivacyRedactor.redact(
    "My email is user@example.com"
)
```

Output:

```text
My email is [EMAIL_1]
```

Planned redaction modes:

```text
REPLACE  -> [EMAIL_1]
MASK     -> u***@example.com
HASH     -> sha256:abc123...
DROP     -> removed completely
```

By default, PromptGuard should not store original sensitive values.

### 3. Privacy-Aware Prompt Request Builder

Build a sanitized, provider-neutral request object instead of passing raw strings directly to an AI client.

```kotlin
val request = PrivacyPromptRequestBuilder("transaction_categorization")
    .system("You are a privacy-aware assistant.")
    .task("Categorize this transaction.")
    .input(transactionInput)
    .redactSensitiveData()
    .outputJson()
    .build()
```

The builder returns a request model that can be checked, logged safely, blocked, or sent to any provider.

```kotlin
data class PrivacyPromptRequest(
    val workflowName: String,
    val sanitizedPrompt: String,
    val redactionApplied: Boolean,
    val findings: List<SensitiveFinding>,
    val metadata: PrivacySafeMetadata
)
```

Example provider handoff:

```kotlin
if (request.findings.any { it.severity == Severity.HIGH }) {
    return AiResult.Blocked("Prompt contains sensitive data that cannot be sent.")
}

val response = aiProvider.generateText(request.sanitizedPrompt)
```

### 4. Safe Metadata Logging

Log workflow metadata without logging raw prompts or user content.

```kotlin
PrivacyPromptLogger.log(
    request = request,
    includePrompt = false
)
```

Example safe log:

```json
{
  "workflow": "transaction_categorization",
  "redactionApplied": true,
  "findingCount": 2,
  "inputLength": 642,
  "sanitizedPromptLength": 418
}
```

### 5. AI Output Validation

Scan AI-generated responses before displaying, storing, logging, or sending them to analytics.

```kotlin
val validation = PrivacyOutputValidator.validate(aiResponse)

if (validation.hasSensitiveData) {
    val safeResponse = PrivacyRedactor.redact(aiResponse)
}
```

## Example: Finance Transaction Categorization

PromptGuard can be used in finance workflows where the model needs transaction context but should not receive unnecessary account or user identifiers.

```kotlin
val request = PrivacyPromptRequestBuilder("transaction_categorization")
    .task("Categorize a card transaction for a personal finance app.")
    .input(
        PromptInput.fromMap(
            mapOf(
                "merchantName" to transaction.merchantName,
                "description" to transaction.description,
                "amount" to transaction.amount,
                "currency" to transaction.currency,
                "postedDate" to transaction.postedDate
            )
        )
    )
    .instructions {
        add("Return the most likely spending category.")
        add("Use only the provided merchant and transaction details.")
        add("Do not include account numbers, emails, or private identifiers.")
    }
    .redactSensitiveData()
    .outputJson()
    .build()

val response = aiProvider.generateText(request.sanitizedPrompt)
```

## Roadmap

Short-term:

* pure Kotlin core module,
* regex-based sensitive data detectors,
* redaction policies,
* prompt request builder,
* safe metadata logger,
* output validation,
* unit tests,
* local Maven publishing for testing in other projects.

Later:

* configurable privacy policies,
* stricter fail-fast modes,
* optional provider adapters,
* SecureFlow integration,
* Maven Central publication.

## PromptGuard and SecureFlow

PromptGuard is intended to complement SecureFlow.

PromptGuard provides runtime APIs for safer AI workflow construction. SecureFlow can scan source code and CI for risky patterns such as raw prompt concatenation, direct AI request logging, or unredacted user input in AI workflows.

## What PromptGuard Is Not

PromptGuard is not:

* an AI chatbot framework,
* an AI provider SDK,
* a backend server,
* a monitoring dashboard,
* a replacement for legal privacy review,
* a guarantee that an AI workflow is compliant.

## License

This project is licensed under the Apache License 2.0.
