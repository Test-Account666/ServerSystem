package me.testaccount666.serversystem.extensions

import java.text.DecimalFormat

private val defaultDecimalFormat = DecimalFormat("0.##")

fun Double.format(pattern: String = "0.##"): String {
    val formatter = if (pattern == "0.##") defaultDecimalFormat else DecimalFormat(pattern)
    return formatter.format(this).replace(",", ".")
}
