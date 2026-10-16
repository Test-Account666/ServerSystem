package me.testaccount666.serversystem.extensions

fun Array<out String>.join(drop: Int = 0, separator: String = " ") = drop(drop).joinToString(separator).trim()
