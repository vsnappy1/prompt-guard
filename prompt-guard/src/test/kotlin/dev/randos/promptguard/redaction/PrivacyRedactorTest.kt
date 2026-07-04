package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.scanner.SensitiveDetector
import dev.randos.promptguard.type.RedactionMode
import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyRedactorTest {
    @Test
    fun `redact replaces scanner findings with stable placeholders`() {
        val redacted = PrivacyRedactor.redact(
            "Email user@example.com, cc admin@example.com, token abc123xyz..."
        )

        assertEquals(
            "Email [EMAIL_1], cc [EMAIL_2], token [TOKEN_1]",
            redacted
        )
    }

    @Test
    fun `redact masks supported sensitive values`() {
        val redacted = PrivacyRedactor.redact(
            text = "Send to user@example.com, call (312) 555-0198, card 4111 1111 1111 1111",
            mode = RedactionMode.MASK
        )

        assertEquals(
            "Send to u***@example.com, call (***) ***-**98, card **** **** **** 1111",
            redacted
        )
    }

    @Test
    fun `redact hashes sensitive values deterministically`() {
        val redacted = PrivacyRedactor.redact(
            text = "Email user@example.com",
            mode = RedactionMode.HASH
        )

        assertEquals(
            "Email sha256:b4c9a289323b21a01c3e940f150eb9b8c542587f1abfd8f0e1cc1ffc5e475514",
            redacted
        )
    }

    @Test
    fun `redact drops sensitive values`() {
        val redacted = PrivacyRedactor.redact(
            text = "Email user@example.com now",
            mode = RedactionMode.DROP
        )

        assertEquals("Email  now", redacted)
    }

    @Test
    fun `redact supports custom detectors`() {
        val detector = SensitiveDetector { text ->
            val startIndex = text.indexOf("project-private")
            listOf(
                SensitiveFinding(
                    type = SensitiveDataType.TOKEN,
                    value = "project-private",
                    startIndex = startIndex,
                    endIndex = startIndex + "project-private".length,
                    severity = Severity.HIGH
                )
            )
        }

        val redacted = PrivacyRedactor.redact(
            text = "Value project-private",
            detectors = listOf(detector)
        )

        assertEquals("Value [TOKEN_1]", redacted)
    }

    @Test
    fun `redact accepts caller provided findings`() {
        val text = "internal value"
        val finding = SensitiveFinding(
            type = SensitiveDataType.TOKEN,
            value = "internal",
            startIndex = 0,
            endIndex = "internal".length,
            severity = Severity.HIGH
        )

        val redacted = PrivacyRedactor.redact(
            text = text,
            findings = listOf(finding)
        )

        assertEquals("[TOKEN_1] value", redacted)
    }

    @Test
    fun `redact ignores invalid caller provided findings`() {
        val text = "safe text"
        val finding = SensitiveFinding(
            type = SensitiveDataType.TOKEN,
            value = "missing",
            startIndex = -1,
            endIndex = 7,
            severity = Severity.HIGH
        )

        assertEquals(text, PrivacyRedactor.redact(text = text, findings = listOf(finding)))
    }

    @Test
    fun `redact removes overlapping findings by severity before replacement`() {
        val text = "secret-token"
        val lowSeverityFinding = SensitiveFinding(
            type = SensitiveDataType.TOKEN,
            value = "secret-token",
            startIndex = 0,
            endIndex = 12,
            severity = Severity.LOW
        )
        val highSeverityFinding = SensitiveFinding(
            type = SensitiveDataType.API_KEY,
            value = "secret",
            startIndex = 0,
            endIndex = 6,
            severity = Severity.HIGH
        )

        val redacted = PrivacyRedactor.redact(
            text = text,
            findings = listOf(lowSeverityFinding, highSeverityFinding)
        )

        assertEquals("[API_KEY_1]-token", redacted)
    }

    @Test
    fun `redact returns blank text unchanged`() {
        assertEquals("  ", PrivacyRedactor.redact("  "))
    }

    @Test
    fun `redact returns text unchanged when caller provided findings are empty`() {
        assertEquals("safe text", PrivacyRedactor.redact(text = "safe text", findings = emptyList()))
    }

    @Test
    fun `redact returns empty text unchanged when caller provided findings are present`() {
        val finding = SensitiveFinding(
            type = SensitiveDataType.TOKEN,
            value = "token",
            startIndex = 0,
            endIndex = 5,
            severity = Severity.HIGH
        )

        assertEquals("", PrivacyRedactor.redact(text = "", findings = listOf(finding)))
    }

    @Test
    fun `redacted output does not contain original sensitive values`() {
        val redacted = PrivacyRedactor.redact("Email user@example.com and token abc123xyz...")

        assertFalse(redacted.contains("user@example.com"))
        assertFalse(redacted.contains("abc123xyz..."))
        assertTrue(redacted.contains("[EMAIL_1]"))
        assertTrue(redacted.contains("[TOKEN_1]"))
    }
}
