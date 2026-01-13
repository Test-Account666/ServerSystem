package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.userdata.OfflineUser
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import kotlin.math.max


object DurationParser {
    private const val _SECONDS_IN_MINUTE = 60L
    private const val _SECONDS_IN_HOUR = 3600L
    private const val _SECONDS_IN_DAY = 86400L
    private const val _SECONDS_IN_WEEK = 604800L
    private const val _SECONDS_IN_MONTH = 2419200L
    private const val _SECONDS_IN_YEAR = 31536000L

    @JvmStatic
    fun parseDate(durationMillis: Long, user: OfflineUser): String {
        if (durationMillis == -1L) {
            val permanent = MappingsData.moderation(user).getName("permanent")
            if (permanent == null) {
                log.warning("Permanent name could not be found! This should not happen!")
                return "Never"
            }

            return permanent
        }


        val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        return dateFormatter.format(durationMillis)
    }

    @JvmStatic
    fun parseDuration(duration: String): Long {
        if (duration.equals("permanent", ignoreCase = true)) return -1

        val regex = Pattern.compile("(\\d{1,9})(mo|y|w|d|h|m|s)")
        val matcher = regex.matcher(duration)

        var totalSeconds = 0L

        try {
            while (matcher.find()) {
                var value = matcher.group(1).toLong()
                value = max(0, value)
                val unit = matcher.group(2)

                when (unit) {
                    "y" -> totalSeconds += value * _SECONDS_IN_YEAR
                    "mo" -> totalSeconds += value * _SECONDS_IN_MONTH
                    "w" -> totalSeconds += value * _SECONDS_IN_WEEK
                    "d" -> totalSeconds += value * _SECONDS_IN_DAY
                    "h" -> totalSeconds += value * _SECONDS_IN_HOUR
                    "m" -> totalSeconds += value * _SECONDS_IN_MINUTE
                    "s" -> totalSeconds += value
                }
            }
        } catch (_: NumberFormatException) {
            return -2
        }

        totalSeconds *= 1000
        return totalSeconds
    }
}

