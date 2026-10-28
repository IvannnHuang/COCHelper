package com.cochelper.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerTagTest {

    @Test
    fun normalize_addsHashUppercasesAndTrims() {
        assertEquals("#GVJGG2JQV", PlayerTag.normalize(" gvjgg2jqv "))
        assertEquals("#GVJGG2JQV", PlayerTag.normalize("#GVJGG2JQV"))
    }

    @Test
    fun normalize_readsLetterOAsZero() {
        assertEquals("#2PP0", PlayerTag.normalize("2ppo"))
    }

    @Test
    fun isValid_acceptsRealTag() {
        assertTrue(PlayerTag.isValid("#GVJGG2JQV"))
    }

    @Test
    fun isValid_rejectsCharactersTagsNeverUse() {
        assertFalse(PlayerTag.isValid("#GVJGG2JQ1"))
        assertFalse(PlayerTag.isValid("#ABC"))
        assertFalse(PlayerTag.isValid(""))
    }
}
