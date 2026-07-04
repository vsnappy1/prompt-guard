package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.scanner.DefaultSensitiveDetectors
import dev.randos.promptguard.scanner.SensitiveDataScanner
import dev.randos.promptguard.scanner.SensitiveDetector
import dev.randos.promptguard.type.RedactionMode

/**
 * Redacts sensitive values from text before that text is used in AI prompts, logs, or analytics.
 */
object PrivacyRedactor {
    /**
     * Scans [text] for sensitive values and redacts each finding.
     */
    fun redact(
        text: String,
        mode: RedactionMode = RedactionMode.REPLACE,
        detectors: List<SensitiveDetector> = DefaultSensitiveDetectors.all
    ): String {
        if (text.isBlank()) return text

        val findings = SensitiveDataScanner.scan(text = text, detectors = detectors)

        return redact(text = text, findings = findings, mode = mode)
    }

    /**
     * Redacts caller-provided findings. Findings with invalid source ranges are ignored.
     */
    fun redact(text: String, findings: List<SensitiveFinding>, mode: RedactionMode = RedactionMode.REPLACE): String {
        if (text.isEmpty() || findings.isEmpty()) return text

        val selectedFindings = RedactionFindingSelector.select(findings = findings, text = text)
        val replacements = RedactionReplacementFactory.create(findings = selectedFindings, mode = mode)

        return replacements
            .asReversed()
            .fold(StringBuilder(text)) { builder, replacement ->
                builder.replace(replacement.startIndex, replacement.endIndex, replacement.value)
            }
            .toString()
    }
}
