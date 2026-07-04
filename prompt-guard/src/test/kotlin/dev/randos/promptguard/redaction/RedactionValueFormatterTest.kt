package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.type.RedactionMode
import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Assert.assertEquals
import org.junit.Test

class RedactionValueFormatterTest {
    @Test
    fun `format creates replacement placeholder`() {
        val value = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.EMAIL, value = "user@example.com"),
            mode = RedactionMode.REPLACE,
            index = 3
        )

        assertEquals("[EMAIL_3]", value)
    }

    @Test
    fun `format creates deterministic sha256 hash`() {
        val value = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.EMAIL, value = "user@example.com"),
            mode = RedactionMode.HASH,
            index = 1
        )

        assertEquals("sha256:b4c9a289323b21a01c3e940f150eb9b8c542587f1abfd8f0e1cc1ffc5e475514", value)
    }

    @Test
    fun `format drops value`() {
        val value = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.TOKEN, value = "secret-token"),
            mode = RedactionMode.DROP,
            index = 1
        )

        assertEquals("", value)
    }

    @Test
    fun `format masks email`() {
        val value = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.EMAIL, value = "user@example.com"),
            mode = RedactionMode.MASK,
            index = 1
        )

        assertEquals("u***@example.com", value)
    }

    @Test
    fun `format masks malformed email-like values without context`() {
        val missingLocalPart = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.EMAIL, value = "@example.com"),
            mode = RedactionMode.MASK,
            index = 1
        )
        val missingDomain = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.EMAIL, value = "user@"),
            mode = RedactionMode.MASK,
            index = 1
        )

        assertEquals("********", missingLocalPart)
        assertEquals("********", missingDomain)
    }

    @Test
    fun `format masks credit card digits while preserving separators and last four digits`() {
        val value = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.CREDIT_CARD, value = "4111-1111-1111-1111"),
            mode = RedactionMode.MASK,
            index = 1
        )

        assertEquals("****-****-****-1111", value)
    }

    @Test
    fun `format masks phone digits while preserving separators and last two digits`() {
        val value = RedactionValueFormatter.format(
            finding = finding(type = SensitiveDataType.PHONE_NUMBER, value = "(312) 555-0198"),
            mode = RedactionMode.MASK,
            index = 1
        )

        assertEquals("(***) ***-**98", value)
    }

    @Test
    fun `format masks opaque secret types without retaining original value`() {
        val opaqueTypes = listOf(
            SensitiveDataType.TOKEN,
            SensitiveDataType.API_KEY,
            SensitiveDataType.AUTHORIZATION_HEADER,
            SensitiveDataType.URL_WITH_SENSITIVE_QUERY,
            SensitiveDataType.PASSWORD
        )

        val values = opaqueTypes.map { type ->
            RedactionValueFormatter.format(
                finding = finding(type = type, value = "secret-value"),
                mode = RedactionMode.MASK,
                index = 1
            )
        }

        assertEquals(listOf("********", "********", "********", "********", "********"), values)
    }

    private fun finding(type: SensitiveDataType, value: String): SensitiveFinding = SensitiveFinding(
        type = type,
        value = value,
        startIndex = 0,
        endIndex = value.length,
        severity = Severity.HIGH
    )
}
