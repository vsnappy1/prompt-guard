package dev.randos.promptguard.model

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

data class SensitiveFinding(
    val type: SensitiveDataType,
    val value: String,
    val startIndex: Int,
    val endIndex: Int,
    val severity: Severity
)