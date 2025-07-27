package me.testaccount666.serversystem.utils;


import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.messages.MappingsData;
import me.testaccount666.serversystem.userdata.OfflineUser;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class DurationParser {
    private static final long _SECONDS_IN_MINUTE = 60;
    private static final long _SECONDS_IN_HOUR = 3600;
    private static final long _SECONDS_IN_DAY = 86400;
    private static final long _SECONDS_IN_WEEK = 604800;
    private static final long _SECONDS_IN_MONTH = 2419200;
    private static final long _SECONDS_IN_YEAR = 31536000;

    public static String parseDate(long durationMillis, OfflineUser user) {
        if (durationMillis == -1) {
            var permanentOptional = MappingsData.moderation(user).getName("permanent");
            if (permanentOptional.isEmpty()) {
                ServerSystem.getLog().warning("Permanent name could not be found! This should not happen!");
                return "Never";
            }

            return permanentOptional.get();
        }


        var dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dateFormatter.format(durationMillis);
    }

    public static long parseDuration(String duration) {
        if (duration.equalsIgnoreCase("permanent")) return -1;

        var regex = Pattern.compile("(\\d{1,9})(mo|y|w|d|h|m|s)");
        var matcher = regex.matcher(duration);

        var totalSeconds = 0L;

        try {
            while (matcher.find()) {
                var value = Long.parseLong(matcher.group(1));
                value = Math.max(0, value);
                var unit = matcher.group(2);

                switch (unit) {
                    case "y":
                        totalSeconds += value * _SECONDS_IN_YEAR;
                        break;
                    case "mo":
                        totalSeconds += value * _SECONDS_IN_MONTH;
                        break;
                    case "w":
                        totalSeconds += value * _SECONDS_IN_WEEK;
                        break;
                    case "d":
                        totalSeconds += value * _SECONDS_IN_DAY;
                        break;
                    case "h":
                        totalSeconds += value * _SECONDS_IN_HOUR;
                        break;
                    case "m":
                        totalSeconds += value * _SECONDS_IN_MINUTE;
                        break;
                    case "s":
                        totalSeconds += value;
                        break;
                }
            }
        } catch (NumberFormatException ignored) {
            return -2;
        }

        totalSeconds *= 1000;
        return totalSeconds;
    }
}

