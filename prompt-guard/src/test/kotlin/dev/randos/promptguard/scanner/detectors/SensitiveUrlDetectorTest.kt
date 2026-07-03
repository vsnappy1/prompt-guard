package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SensitiveUrlDetectorTest {
    @Test
    fun `find detects url with sensitive query parameter`() {
        val url = "https://example.com/callback?token=abc123xyz&state=ready"

        val findings = SensitiveUrlDetector.find("Fetch $url after login")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.URL_WITH_SENSITIVE_QUERY, findings.single().type)
        assertEquals(url, findings.single().value)
        assertEquals(Severity.HIGH, findings.single().severity)
    }

    @Test
    fun `find detects sensitive query parameter after non-sensitive parameter`() {
        val url = "https://example.com/callback?state=ready&signature=abc123"

        val findings = SensitiveUrlDetector.find(url)

        assertEquals(1, findings.size)
        assertEquals(url, findings.single().value)
    }

    @Test
    fun `find ignores url without sensitive query parameter`() {
        assertTrue(SensitiveUrlDetector.find("https://example.com/callback?state=ready").isEmpty())
    }
}
