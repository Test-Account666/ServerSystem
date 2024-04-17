package me.entity303.serversystem.bansystem;

public enum TimeUnit {
    YEAR(31540000000L, "YEAR"), MONTH(2630000000L, "MONTH"), WEEK(604800000L, "WEEK"), DAY(86400000L, "DAY"), HOUR(3600000L, "HOUR"), MINUTE(60000L, "MINUTE"),
    SECOND(1000L, "SECOND");

    public static String yearName = "YEAR";
    public static String monthName = "MONTH";
    public static String weekName = "WEEK";
    public static String dayName = "DAY";
    public static String hourName = "HOUR";
    public static String minuteName = "MINUTE";
    public static String secondName = "SECOND";
    private final Long value;
    private final String name;

    TimeUnit(Long value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getName(TimeUnit timeUnit) {
        return switch (timeUnit) {
            case YEAR -> TimeUnit.yearName;
            case MONTH -> TimeUnit.monthName;
            case WEEK -> TimeUnit.weekName;
            case DAY -> TimeUnit.dayName;
            case HOUR -> TimeUnit.hourName;
            case MINUTE -> TimeUnit.minuteName;
            case SECOND -> TimeUnit.secondName;
        };
    }

    public static TimeUnit getFromName(String name) {
        if (name.equalsIgnoreCase(TimeUnit.yearName))
            return TimeUnit.YEAR;
        else if (name.equalsIgnoreCase(TimeUnit.monthName))
            return TimeUnit.MONTH;
        else if (name.equalsIgnoreCase(TimeUnit.weekName))
            return TimeUnit.WEEK;
        else if (name.equalsIgnoreCase(TimeUnit.dayName))
            return TimeUnit.DAY;
        else if (name.equalsIgnoreCase(TimeUnit.hourName))
            return TimeUnit.HOUR;
        else if (name.equalsIgnoreCase(TimeUnit.minuteName))
            return TimeUnit.MINUTE;
        else if (name.equalsIgnoreCase(TimeUnit.secondName))
            return TimeUnit.SECOND;
        return null;
    }

    public String getName() {
        return this.name;
    }

    public Long getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "TimeUnit{" + "value=" + this.value + '}';
    }
}
