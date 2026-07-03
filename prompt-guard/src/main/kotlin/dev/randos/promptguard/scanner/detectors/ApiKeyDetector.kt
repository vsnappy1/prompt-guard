package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object ApiKeyDetector : RegexSensitiveDetector(
    type = SensitiveDataType.API_KEY,
    severity = Severity.HIGH,
    regex = Regex(
        pattern = """(?i)\b(?:api[_-]?key|secret[_-]?key|client[_-]?secret|access[_-]?token)""" +
            """\s*[:=]\s*["']?([A-Za-z0-9._~+/=-]{8,})["']?"""
    ),
    group = 1
)
