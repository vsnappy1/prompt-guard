package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RedactionFindingSelectorTest {
    @Test
    fun `select returns findings in source order`() {
        val findings = listOf(
            finding(value = "token", startIndex = 18, endIndex = 23, type = SensitiveDataType.TOKEN),
            finding(value = "user@example.com", startIndex = 0, endIndex = 16, type = SensitiveDataType.EMAIL)
        )

        val selected = RedactionFindingSelector.select(
            findings = findings,
            text = "user@example.com, token"
        )

        assertEquals(
            listOf(SensitiveDataType.EMAIL, SensitiveDataType.TOKEN),
            selected.map { it.type }
        )
    }

    @Test
    fun `select ignores invalid source ranges`() {
        val findings = listOf(
            finding(value = "negative", startIndex = -1, endIndex = 8),
            finding(value = "empty", startIndex = 2, endIndex = 2),
            finding(value = "outside", startIndex = 0, endIndex = 20),
            finding(value = "safe", startIndex = 0, endIndex = 4)
        )

        val selected = RedactionFindingSelector.select(findings = findings, text = "safe text")

        assertEquals(listOf("safe"), selected.map { it.value })
    }

    @Test
    fun `select keeps adjacent non-overlapping findings`() {
        val findings = listOf(
            finding(value = "one", startIndex = 0, endIndex = 3),
            finding(value = "two", startIndex = 3, endIndex = 6)
        )

        val selected = RedactionFindingSelector.select(findings = findings, text = "onetwo")

        assertEquals(listOf("one", "two"), selected.map { it.value })
    }

    @Test
    fun `select chooses higher severity overlapping finding`() {
        val findings = listOf(
            finding(value = "secret-token", startIndex = 0, endIndex = 12, severity = Severity.LOW),
            finding(value = "secret", startIndex = 0, endIndex = 6, type = SensitiveDataType.API_KEY, severity = Severity.HIGH)
        )

        val selected = RedactionFindingSelector.select(findings = findings, text = "secret-token")

        assertEquals(listOf("secret"), selected.map { it.value })
    }

    @Test
    fun `select chooses longer overlapping finding when severity matches`() {
        val findings = listOf(
            finding(value = "secret", startIndex = 0, endIndex = 6, severity = Severity.HIGH),
            finding(value = "secret-token", startIndex = 0, endIndex = 12, severity = Severity.HIGH)
        )

        val selected = RedactionFindingSelector.select(findings = findings, text = "secret-token")

        assertEquals(listOf("secret-token"), selected.map { it.value })
    }

    @Test
    fun `select keeps existing overlapping finding when candidate has lower priority`() {
        val findings = listOf(
            finding(value = "secret-token", startIndex = 0, endIndex = 12, severity = Severity.HIGH),
            finding(value = "secret", startIndex = 0, endIndex = 6, type = SensitiveDataType.API_KEY, severity = Severity.HIGH)
        )

        val selected = RedactionFindingSelector.select(findings = findings, text = "secret-token")

        assertEquals(listOf("secret-token"), selected.map { it.value })
    }

    @Test
    fun `select keeps existing overlapping finding when candidate has lower severity`() {
        val findings = listOf(
            finding(value = "secret-token", startIndex = 0, endIndex = 12, severity = Severity.HIGH),
            finding(value = "secret", startIndex = 0, endIndex = 6, type = SensitiveDataType.API_KEY, severity = Severity.LOW)
        )

        val selected = RedactionFindingSelector.select(findings = findings, text = "secret-token")

        assertEquals(listOf("secret-token"), selected.map { it.value })
    }

    @Test
    fun `select returns empty list when no findings are valid`() {
        val selected = RedactionFindingSelector.select(
            findings = listOf(finding(value = "missing", startIndex = -1, endIndex = 7)),
            text = "safe"
        )

        assertTrue(selected.isEmpty())
    }

    private fun finding(
        value: String,
        startIndex: Int,
        endIndex: Int,
        type: SensitiveDataType = SensitiveDataType.TOKEN,
        severity: Severity = Severity.MEDIUM
    ): SensitiveFinding = SensitiveFinding(
        type = type,
        value = value,
        startIndex = startIndex,
        endIndex = endIndex,
        severity = severity
    )
}
