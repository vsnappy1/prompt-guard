package dev.randos.promptguard.scanner

import dev.randos.promptguard.model.SensitiveFinding

fun interface SensitiveDetector {
    fun find(text: String): List<SensitiveFinding>
}
