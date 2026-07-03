package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class AuthorizationHeaderDetectorTest {
    @Test
    fun `find detects bearer authorization header`() {
        val findings = AuthorizationHeaderDetector.find("Authorization: Bearer sk_live_123456789")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.AUTHORIZATION_HEADER, findings.single().type)
        assertEquals("Authorization: Bearer sk_live_123456789", findings.single().value)
        assertEquals(Severity.HIGH, findings.single().severity)
    }

    @Test
    fun `find detects basic authorization header case insensitively`() {
        val findings = AuthorizationHeaderDetector.find("authorization: basic dXNlcjpwYXNz")

        assertEquals(1, findings.size)
        assertEquals("authorization: basic dXNlcjpwYXNz", findings.single().value)
    }

    @Test
    fun `find ignores unsupported auth schemes`() {
        assertTrue(AuthorizationHeaderDetector.find("Authorization: Digest abcdefgh1234").isEmpty())
    }
}
