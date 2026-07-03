package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object PasswordDetector : RegexSensitiveDetector(
    type = SensitiveDataType.PASSWORD,
    severity = Severity.HIGH,
    regex = Regex(
        pattern = """(?i)\b(?:password|passwd|pwd|passcode)\s*[:=]\s*["']?([^"'\s,;]{4,})["']?"""
    ),
    group = 1
)
