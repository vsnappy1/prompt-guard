package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(emptyList(), CreditCardDetector.find("Card 4111 1111 1111 1112"))
    }

    @Test
    fun `find trims trailing separators from card candidate`() {
        val findings = CreditCardDetector.find("Card 4111-1111-1111-1111- expires soon")

        assertEquals(1, findings.size)
        assertEquals("4111-1111-1111-1111", findings.single().value)
    }
}
