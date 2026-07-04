package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.type.RedactionMode
import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RedactionReplacementFactoryTest {
    @Test
    fun `create returns empty list for no findings`() {
        assertTrue(RedactionReplacementFactory.create(findings = emptyList(), mode = RedactionMode.REPLACE).isEmpty())
    }

    @Test
    fun `create builds replacements with per type indexes`() {
        val replacements = RedactionReplacementFactory.create(
            findings = listOf(
                finding(type = SensitiveDataType.EMAIL, value = "first@example.com", startIndex = 0, endIndex = 17),
                finding(type = SensitiveDataType.TOKEN, value = "token-value", startIndex = 18, endIndex = 29),
                finding(type = SensitiveDataType.EMAIL, value = "second@example.com", startIndex = 30, endIndex = 48)
            ),
            mode = RedactionMode.REPLACE
        )

        assertEquals(listOf("[EMAIL_1]", "[TOKEN_1]", "[EMAIL_2]"), replacements.map { it.value })
        assertEquals(listOf(0, 18, 30), replacements.map { it.startIndex })
        assertEquals(listOf(17, 29, 48), replacements.map { it.endIndex })
    }

    @Test
    fun `create delegates requested redaction mode`() {
        val replacements = RedactionReplacementFactory.create(
            findings = listOf(finding(type = SensitiveDataType.EMAIL, value = "user@example.com", startIndex = 0, endIndex = 16)),
            mode = RedactionMode.MASK
        )

        assertEquals(listOf("u***@example.com"), replacements.map { it.value })
    }

    private fun finding(type: SensitiveDataType, value: String, startIndex: Int, endIndex: Int): SensitiveFinding = SensitiveFinding(
        type = type,
        value = value,
        startIndex = startIndex,
        endIndex = endIndex,
        severity = Severity.HIGH
    )
}
