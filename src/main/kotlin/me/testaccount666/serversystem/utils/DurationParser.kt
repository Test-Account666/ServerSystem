package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.userdata.OfflineUser
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import kotlin.math.max


object DurationParser {
    private const val _SECONDS_IN_MINUTE: Long = 60
    private const val _SECONDS_IN_HOUR: Long = 3600
    private const val _SECONDS_IN_DAY: Long = 86400
    private const val _SECONDS_IN_WEEK: Long = 604800
    private const val _SECONDS_IN_MONTH: Long = 2419200
    private const val _SECONDS_IN_YEAR: Long = 31536000

    @JvmStatic
    fun parseDate(durationMillis: Long, user: OfflineUser): String {
        if (durationMillis == -1L) {
            val permanentOptional = MappingsData.moderation(user).getName("permanent")
            if (permanentOptional.isEmpty) {
                log.warning("Permanent name could not be found! This should not happen!")
                return "Never"
            }

            return permanentOptional.get()
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

