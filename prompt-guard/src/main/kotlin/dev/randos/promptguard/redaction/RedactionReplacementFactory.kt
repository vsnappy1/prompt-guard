package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.RedactionReplacement
import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.type.RedactionMode
import dev.randos.promptguard.type.SensitiveDataType

internal object RedactionReplacementFactory {
    fun create(findings: List<SensitiveFinding>, mode: RedactionMode): List<RedactionReplacement> {
        val countsByType = mutableMapOf<SensitiveDataType, Int>()

        return findings.map { finding ->
            val index = countsByType.nextIndex(finding.type)
            RedactionReplacement(
                startIndex = finding.startIndex,
                endIndex = finding.endIndex,
                value = RedactionValueFormatter.format(finding = finding, mode = mode, index = index)
            )
        }
    }

    private fun MutableMap<SensitiveDataType, Int>.nextIndex(type: SensitiveDataType): Int {
        val nextIndex = (this[type] ?: 0) + 1
        this[type] = nextIndex
        return nextIndex
    }
}
