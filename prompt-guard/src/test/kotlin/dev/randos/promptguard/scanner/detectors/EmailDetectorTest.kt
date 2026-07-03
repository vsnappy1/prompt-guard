package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class EmailDetectorTest {
    @Test
    fun `find detects email address`() {
        val text = "Email me at user@example.com."

        val findings = EmailDetector.find(text)

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.EMAIL, findings.single().type)
        assertEquals("user@example.com", findings.single().value)
        assertEquals(12, findings.single().startIndex)
        assertEquals(28, findings.single().endIndex)
        assertEquals(Severity.MEDIUM, findings.single().severity)
    }

    @Test
    fun `find detects email address case insensitively`() {
        val findings = EmailDetector.find("Contact USER@EXAMPLE.COM")

        assertEquals(1, findings.size)
        assertEquals("USER@EXAMPLE.COM", findings.single().value)
    }

    @Test
    fun `find ignores malformed email address`() {
        assertTrue(EmailDetector.find("Contact user@example").isEmpty())
    }
}
