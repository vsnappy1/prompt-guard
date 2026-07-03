package dev.randos.promptguard.type

enum class Severity(internal val rank: Int) {
    LOW(rank = 1),
    MEDIUM(rank = 2),
    HIGH(rank = 3)
}