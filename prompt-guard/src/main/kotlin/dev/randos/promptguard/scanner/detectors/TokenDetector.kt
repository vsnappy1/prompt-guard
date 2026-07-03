package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object TokenDetector : RegexSensitiveDetector(
    type = SensitiveDataType.TOKEN,
    severity = Severity.HIGH,
    regex = Regex(
        pattern = """(?i)\b(?:token|bearer|jwt)\s*[:=]?\s*["']?([A-Za-z0-9._~+/=-]{8,})["']?"""
    ),
    group = 1
)
