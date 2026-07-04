package dev.randos.promptguard.type

import org.junit.Assert.assertEquals
import org.junit.Test

class RedactionModeTest {
    @Test
    fun `values exposes supported redaction modes in declaration order`() {
        assertEquals(
            listOf(RedactionMode.REPLACE, RedactionMode.MASK, RedactionMode.HASH, RedactionMode.DROP),
            RedactionMode.values().toList()
        )
    }
}
