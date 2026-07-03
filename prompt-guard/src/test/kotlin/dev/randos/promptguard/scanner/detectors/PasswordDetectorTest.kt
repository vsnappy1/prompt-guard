package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordDetectorTest {
    @Test
    fun `find extracts password value`() {
        val findings = PasswordDetector.find("password: correct-horse")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.PASSWORD, findings.single().type)
        assertEquals("correct-horse", findings.single().value)
        assertEquals(Severity.HIGH, findings.single().severity)
    }

    @Test
    fun `find supports alternate password labels`() {
        val findings = PasswordDetector.find("""pwd = "open-sesame"""")

        assertEquals(1, findings.size)
        assertEquals("open-sesame", findings.single().value)
    }

    @Test
    fun `find ignores short password values`() {
        assertEquals(emptyList(), PasswordDetector.find("password: abc"))
    }
}
