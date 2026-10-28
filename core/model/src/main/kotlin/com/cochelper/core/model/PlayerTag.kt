package com.cochelper.core.model

/** Player and clan tags use only these characters (no 1, I, O, etc.). */
object PlayerTag {
    private val VALID = Regex("^#[0289PYLQGRJCUV]{3,12}$")

    /** Uppercases, trims, and adds the leading `#`. "gvjgg2jqv " → "#GVJGG2JQV". */
    fun normalize(input: String): String = "#" + input.trim().removePrefix("#").uppercase().replace('O', '0')

    fun isValid(input: String): Boolean = VALID.matches(normalize(input))
}
