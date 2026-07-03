package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenDetectorTest {
    @Test
    fun `find extracts token value`() {
        val findings = TokenDetector.find("token abc123xyz...")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.TOKEN, findings.single().type)
        assertEquals("abc123xyz...", findings.single().value)
        assertEquals(Severity.HIGH, findings.single().severity)
    }

    @Test
    fun `find supports bearer token label`() {
        val findings = TokenDetector.find("""bearer = "sk_live_123456789"""")

        assertEquals(1, findings.size)
        assertEquals("sk_live_123456789", findings.single().value)
    }

    @Test
    fun `find ignores short token values`() {
        assertTrue(TokenDetector.find("token abc").isEmpty())
    }
}
