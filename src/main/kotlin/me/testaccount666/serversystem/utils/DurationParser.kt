package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.userdata.OfflineUser
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


object DurationParser {
    fun parseDate(durationMillis: Long, user: OfflineUser): String {
        if (durationMillis == -1L) {
            val permanent = MappingsData.moderation(user).getName("permanent") ?: run {
                log.warning("Permanent name could not be found! This should not happen!")
                return "Never"
            }

            return permanent
        }

        return SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(durationMillis)
    }

    fun parseDuration(duration: String): Long {
        if (duration.equals("permanent", true)) return -1

        val regex = Pattern.compile("(\\d{1,9})(mo|y|w|d|h|m|s)")
        val matcher = regex.matcher(duration)

        var totalMillis = -2L

        try {
            while (matcher.find()) {
                var value = matcher.group(1).toLong()
                value = max(0, value)
                val unit = matcher.group(2)

                when (unit) {
                    "y" -> totalMillis += value.days.times(365).inWholeMilliseconds
                    "mo" -> totalMillis += value.days.times(30).inWholeMilliseconds
                    "w" -> totalMillis += value.days.times(7).inWholeMilliseconds
                    "d" -> totalMillis += value.days.inWholeMilliseconds
                    "h" -> totalMillis += value.hours.inWholeMilliseconds
                    "m" -> totalMillis += value.minutes.inWholeMilliseconds
                    "s" -> totalMillis += value.seconds.inWholeMilliseconds
                }
            }
        } catch (_: NumberFormatException) {
            return -2
        }

        if (totalMillis <= -2) return -2

        return totalMillis + 2
    }
}

