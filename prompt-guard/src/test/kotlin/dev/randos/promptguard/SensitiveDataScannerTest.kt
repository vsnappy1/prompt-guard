package dev.randos.promptguard

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.scanner.SensitiveDataScanner
import dev.randos.promptguard.scanner.SensitiveDetector
import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import kotlin.test.Test
import kotlin.test.assertEquals

class SensitiveDataScannerTest {
    @Test
    fun `scan returns empty list for blank text`() {
        assertEquals(emptyList(), SensitiveDataScanner.scan("  "))
    }

    @Test
    fun `scan returns default detector findings in source order`() {
        val findings = SensitiveDataScanner.scan("Email user@example.com and token abc123xyz...")

        assertEquals(
            listOf(SensitiveDataType.EMAIL, SensitiveDataType.TOKEN),
            findings.map { it.type }
        )
        assertEquals("user@example.com", findings[0].value)
        assertEquals("abc123xyz...", findings[1].value)
    }

    @Test
    fun `scan accepts custom detector implementations`() {
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

        assertEquals(1, findings.size)
        assertEquals("private", findings.single().value)
    }

    @Test
    fun `scan removes overlapping findings by severity`() {
        val lowSeverityDetector = fixedDetector(
            value = "secret-token",
            startIndex = 0,
            endIndex = 12,
            type = SensitiveDataType.TOKEN,
            severity = Severity.LOW
        )
        val highSeverityDetector = fixedDetector(
            value = "secret",
            startIndex = 0,
            endIndex = 6,
            type = SensitiveDataType.API_KEY,
            severity = Severity.HIGH
        )

        val findings = SensitiveDataScanner.scan(
            text = "secret-token",
            detectors = listOf(lowSeverityDetector, highSeverityDetector)
        )

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.API_KEY, findings.single().type)
        assertEquals("secret", findings.single().value)
    }

    @Test
    fun `scan removes overlapping findings by longest value when severity matches`() {
        val shorterDetector = fixedDetector(
            value = "secret",
            startIndex = 0,
            endIndex = 6,
            type = SensitiveDataType.TOKEN,
            severity = Severity.HIGH
        )
        val longerDetector = fixedDetector(
            value = "secret-token",
            startIndex = 0,
            endIndex = 12,
            type = SensitiveDataType.API_KEY,
            severity = Severity.HIGH
        )

        val findings = SensitiveDataScanner.scan(
            text = "secret-token",
            detectors = listOf(shorterDetector, longerDetector)
        )

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.API_KEY, findings.single().type)
        assertEquals("secret-token", findings.single().value)
    }

    private fun fixedDetector(
        value: String,
        startIndex: Int,
        endIndex: Int,
        type: SensitiveDataType,
        severity: Severity
    ): SensitiveDetector = SensitiveDetector {
        listOf(
            SensitiveFinding(
                type = type,
                value = value,
                startIndex = startIndex,
                endIndex = endIndex,
                severity = severity
            )
        )
    }
}
