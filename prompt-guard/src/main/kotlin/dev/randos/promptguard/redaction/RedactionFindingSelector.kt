package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding

internal object RedactionFindingSelector {
    fun select(findings: List<SensitiveFinding>, text: String): List<SensitiveFinding> = findings
        .validFor(text)
        .dedupeOverlaps()

    private fun List<SensitiveFinding>.validFor(text: String): List<SensitiveFinding> = filter { finding ->
        finding.startIndex >= 0 &&
            finding.endIndex > finding.startIndex &&
            finding.endIndex <= text.length
    }.sortedWith(compareBy<SensitiveFinding> { it.startIndex }.thenBy { it.endIndex })

    private fun List<SensitiveFinding>.dedupeOverlaps(): List<SensitiveFinding> {
        val findings = mutableListOf<SensitiveFinding>()

        for (candidate in this) {
            val overlappingIndex = findings.indexOfFirst { it.overlaps(candidate) }

            if (overlappingIndex == -1) {
                findings += candidate
                continue
            }

            val existing = findings[overlappingIndex]
            if (candidate.shouldReplace(existing)) {
                findings[overlappingIndex] = candidate
            }
        }

        return findings.sortedWith(compareBy<SensitiveFinding> { it.startIndex }.thenBy { it.endIndex })
    }

    private fun SensitiveFinding.overlaps(other: SensitiveFinding): Boolean =
        startIndex < other.endIndex && other.startIndex < endIndex

    private fun SensitiveFinding.shouldReplace(existing: SensitiveFinding): Boolean {
        val severityDelta = severity.rank - existing.severity.rank
        if (severityDelta != 0) return severityDelta > 0

        val length = endIndex - startIndex
        val existingLength = existing.endIndex - existing.startIndex
        return length > existingLength
    }
}
