package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object EmailDetector : RegexSensitiveDetector(
    type = SensitiveDataType.EMAIL,
    severity = Severity.MEDIUM,
    regex = Regex(
        pattern = """\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b""",
        options = setOf(RegexOption.IGNORE_CASE)
    )
)
