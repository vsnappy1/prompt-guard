package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class PhoneNumberDetectorTest {
    @Test
    fun `find detects formatted phone number`() {
        val findings = PhoneNumberDetector.find("Call (312) 555-0198 today")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.PHONE_NUMBER, findings.single().type)
        assertEquals("(312) 555-0198", findings.single().value)
        assertEquals(Severity.LOW, findings.single().severity)
    }

    @Test
    fun `find detects phone number with country code`() {
        val findings = PhoneNumberDetector.find("Call +1 312.555.0198 today")

        assertEquals(1, findings.size)
        assertEquals("+1 312.555.0198", findings.single().value)
    }

    @Test
    fun `find ignores phone-like text inside words`() {
        assertTrue(PhoneNumberDetector.find("ref3125550198code").isEmpty())
    }
}
