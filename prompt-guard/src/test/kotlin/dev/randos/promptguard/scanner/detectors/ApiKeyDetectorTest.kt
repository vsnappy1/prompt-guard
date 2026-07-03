package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiKeyDetectorTest {
    @Test
    fun `find extracts assigned api key value`() {
        val findings = ApiKeyDetector.find("""api_key = "secret-key-12345"""")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.API_KEY, findings.single().type)
        assertEquals("secret-key-12345", findings.single().value)
        assertEquals(Severity.HIGH, findings.single().severity)
    }

    @Test
    fun `find supports alternate key labels`() {
        val findings = ApiKeyDetector.find("client_secret: abcdefgh12345678")

        assertEquals(1, findings.size)
        assertEquals("abcdefgh12345678", findings.single().value)
    }

    @Test
    fun `find ignores short values`() {
        assertEquals(emptyList(), ApiKeyDetector.find("api_key = short"))
    }
}
