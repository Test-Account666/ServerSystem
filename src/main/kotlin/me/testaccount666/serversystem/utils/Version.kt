package me.testaccount666.serversystem.utils

import kotlin.math.min

class Version(version: String) : Comparable<Version> {
    val version: String

    init {
        require((version.trim { it <= ' ' }.isNotEmpty())) { "Version string cannot be null or empty" }

        val normalizedVersion = version.trim { it <= ' ' }

        require(normalizedVersion.matches("^\\d+(\\.\\d+)*$".toRegex())) {
            "Invalid version format: ${version}. Expected format: x.y.z (numeric segments separated by dots)"
        }

        this.version = normalizedVersion
    }

    override fun compareTo(other: Version): Int {
        val thisVersion: Array<String> = version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val otherVersion: Array<String> = other.version.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (index in 0..<min(thisVersion.size, otherVersion.size)) try {
            val thisVersionInt = thisVersion[index].toInt()
            val otherVersionInt = otherVersion[index].toInt()

            if (thisVersionInt != otherVersionInt) return thisVersionInt.compareTo(otherVersionInt)
        } catch (exception: NumberFormatException) {
            throw IllegalStateException("Invalid numeric segment in version: $version or ${other.version}", exception)
        }
        return thisVersion.size.compareTo(otherVersion.size)
    }

    override fun toString() = version
}