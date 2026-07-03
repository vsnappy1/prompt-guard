package dev.randos.promptguard.scanner

import dev.randos.promptguard.scanner.detectors.ApiKeyDetector
import dev.randos.promptguard.scanner.detectors.AuthorizationHeaderDetector
import dev.randos.promptguard.scanner.detectors.CreditCardDetector
import dev.randos.promptguard.scanner.detectors.EmailDetector
import dev.randos.promptguard.scanner.detectors.PasswordDetector
import dev.randos.promptguard.scanner.detectors.PhoneNumberDetector
import dev.randos.promptguard.scanner.detectors.SensitiveUrlDetector
import dev.randos.promptguard.scanner.detectors.TokenDetector

internal object DefaultSensitiveDetectors {
    val all: List<SensitiveDetector> = listOf(
        AuthorizationHeaderDetector,
        ApiKeyDetector,
        PasswordDetector,
        EmailDetector,
        SensitiveUrlDetector,
        CreditCardDetector,
        PhoneNumberDetector,
        TokenDetector
    )
}