package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class CreditCardDetectorTest {
    @Test
    fun `find detects luhn-valid card number`() {
        val findings = CreditCardDetector.find("Card 4111 1111 1111 1111")

        assertEquals(1, findings.size)
        assertEquals(SensitiveDataType.CREDIT_CARD, findings.single().type)
        assertEquals("4111 1111 1111 1111", findings.single().value)
        assertEquals(Severity.HIGH, findings.single().severity)
    }

    @Test
    fun `find ignores luhn-invalid card number`() {
        assertTrue(CreditCardDetector.find("Card 4111 1111 1111 1112").isEmpty())
    }

    @Test
    fun `find trims trailing separators from card candidate`() {
        val findings = CreditCardDetector.find("Card 4111-1111-1111-1111- expires soon")

        assertEquals(1, findings.size)
        assertEquals("4111-1111-1111-1111", findings.single().value)
    }
}
