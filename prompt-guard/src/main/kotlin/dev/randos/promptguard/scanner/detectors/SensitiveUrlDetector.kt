package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object SensitiveUrlDetector : RegexSensitiveDetector(
    type = SensitiveDataType.URL_WITH_SENSITIVE_QUERY,
    severity = Severity.HIGH,
    regex = Regex(
        pattern = """https?://[^\s"'<>]+[?&]""" +
            """(?:token|api[_-]?key|key|secret|password|pwd|auth|signature|sig|session)[^"' <>\r\n]*""",
        options = setOf(RegexOption.IGNORE_CASE)
    )
)
