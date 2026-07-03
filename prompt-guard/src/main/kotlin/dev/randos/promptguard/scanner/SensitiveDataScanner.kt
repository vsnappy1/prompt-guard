package dev.randos.promptguard.scanner

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.scanner.DefaultSensitiveDetectors

/**
 * Scans text for common sensitive values that should be reviewed before an AI request is built.
 */
object SensitiveDataScanner {
    private val defaultDetectors: List<SensitiveDetector> = DefaultSensitiveDetectors.all

    /**
     * Returns all non-overlapping sensitive findings in their original text order.
     */
    fun scan(text: String, detectors: List<SensitiveDetector> = defaultDetectors): List<SensitiveFinding> {
        if (text.isBlank()) return emptyList()

        return detectors
            .flatMap { detector -> detector.find(text) }
            .sortedWith(compareBy<SensitiveFinding> { it.startIndex }.thenBy { it.endIndex })
            .dedupeOverlaps()
    }

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
