package dev.randos.promptguard.type

enum class SensitiveDataType {
    EMAIL,
    PHONE_NUMBER,
    TOKEN,
    API_KEY,
    AUTHORIZATION_HEADER,
    CREDIT_CARD,
    URL_WITH_SENSITIVE_QUERY,
    PASSWORD
}
