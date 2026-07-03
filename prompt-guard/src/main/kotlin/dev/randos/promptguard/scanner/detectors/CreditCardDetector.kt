package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object CreditCardDetector : RegexSensitiveDetector(
    type = SensitiveDataType.CREDIT_CARD,
    severity = Severity.HIGH,
    regex = Regex(
        pattern = """(?<!\d)(?:\d[ -]?){13,19}(?!\d)"""
    )
) {
    override fun normalizeRange(text: String, range: IntRange): IntRange =
        range.trimTrailing(text) { it == ' ' || it == '-' || it == '.' }

    override fun isValid(value: String): Boolean {
        val digits = value.filter(Char::isDigit)
        if (digits.length !in 13..19) return false

        var sum = 0
        var doubleDigit = false

        for (index in digits.length - 1 downTo 0) {
            var digit = digits[index].digitToInt()
            if (doubleDigit) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            sum += digit
            doubleDigit = !doubleDigit
        }

        return sum % 10 == 0
    }

    private fun IntRange.trimTrailing(text: String, predicate: (Char) -> Boolean): IntRange {
        var end = last
        while (end >= first && predicate(text[end])) {
            end--
        }
        return first..end
    }
}
