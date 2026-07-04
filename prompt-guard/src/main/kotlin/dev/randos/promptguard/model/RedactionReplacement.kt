package dev.randos.promptguard.model

internal data class RedactionReplacement(val startIndex: Int, val endIndex: Int, val value: String)
