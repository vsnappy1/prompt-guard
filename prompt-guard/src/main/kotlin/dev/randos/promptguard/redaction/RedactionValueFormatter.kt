package dev.randos.promptguard.redaction

import dev.randos.promptguard.model.SensitiveFinding
import dev.randos.promptguard.type.RedactionMode
import dev.randos.promptguard.type.SensitiveDataType
import java.security.MessageDigest

internal object RedactionValueFormatter {
    fun format(finding: SensitiveFinding, mode: RedactionMode, index: Int): String = when (mode) {
        RedactionMode.REPLACE -> "[${finding.type.name}_$index]"
        RedactionMode.MASK -> finding.maskedValue()
        RedactionMode.HASH -> "sha256:${finding.value.sha256()}"
        RedactionMode.DROP -> ""
    }

    private fun SensitiveFinding.maskedValue(): String = when (type) {
        SensitiveDataType.EMAIL -> value.maskEmail()
        SensitiveDataType.CREDIT_CARD -> value.maskDigitsKeepingLast(visibleDigits = 4)
        SensitiveDataType.PHONE_NUMBER -> value.maskDigitsKeepingLast(visibleDigits = 2)
        SensitiveDataType.TOKEN,
        SensitiveDataType.API_KEY,
        SensitiveDataType.AUTHORIZATION_HEADER,
        SensitiveDataType.URL_WITH_SENSITIVE_QUERY,
        SensitiveDataType.PASSWORD -> "********"
    }

    private fun String.maskEmail(): String {
        val atIndex = indexOf('@')
        if (atIndex <= 0 || atIndex == lastIndex) return "********"

        val localPart = substring(startIndex = 0, endIndex = atIndex)
        val domain = substring(startIndex = atIndex + 1)

        return "${localPart.first()}***@$domain"
    }

    private fun String.maskDigitsKeepingLast(visibleDigits: Int): String {
        val totalDigits = count { it.isDigit() }
        var seenDigits = 0

        return map { char ->
            if (!char.isDigit()) {
                char
            } else {
                seenDigits += 1
                if (seenDigits > totalDigits - visibleDigits) char else '*'
            }
        }.joinToString(separator = "")
    }

    private fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}
