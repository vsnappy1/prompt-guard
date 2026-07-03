package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.scanner.SensitiveDetector
import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.type.Severity

internal abstract class RegexSensitiveDetector(
    private val type: SensitiveDataType,
    private val severity: Severity,
    private val regex: Regex,
    private val group: Int = 0
) : SensitiveDetector {
    override fun find(text: String): List<SensitiveFinding> =
        regex.findAll(text).mapNotNull { match ->
            val range = match.groups[group]?.range ?: match.range
            val normalizedRange = normalizeRange(text, range)
            val value = text.substring(normalizedRange)

            if (!isValid(value)) {
                null
            } else {
                SensitiveFinding(
                    type = type,
                    value = value,
                    startIndex = normalizedRange.first,
                    endIndex = normalizedRange.last + 1,
                    severity = severity
                )
            }
        }.toList()

    protected open fun normalizeRange(text: String, range: IntRange): IntRange = range

    protected open fun isValid(value: String): Boolean = true
}
