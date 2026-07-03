package dev.randos.promptguard.scanner.detectors

import dev.randos.promptguard.type.SensitiveDataType
import dev.randos.promptguard.type.Severity

internal object PhoneNumberDetector : RegexSensitiveDetector(
    type = SensitiveDataType.PHONE_NUMBER,
    severity = Severity.LOW,
    regex = Regex(
        pattern = """(?<!\w)(?:\+?1[\s.-]?)?(?:\(?\d{3}\)?[\s.-]?)\d{3}[\s.-]?\d{4}(?!\w)"""
    )
)
