package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object AuthorizationHeaderDetector : RegexSensitiveDetector(
    type = SensitiveDataType.AUTHORIZATION_HEADER,
    severity = Severity.HIGH,
    regex = Regex(
        pattern = """(?i)\bauthorization\s*:\s*(?:bearer|basic)\s+[A-Za-z0-9._~+/=-]{8,}"""
    )
)
