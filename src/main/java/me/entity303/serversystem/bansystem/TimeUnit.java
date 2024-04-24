package me.entity303.serversystem.bansystem;

public enum TimeUnit {
    YEAR(31540000000L, "YEAR"), MONTH(2630000000L, "MONTH"), WEEK(604800000L, "WEEK"), DAY(86400000L, "DAY"), HOUR(3600000L, "HOUR"),
    MINUTE(60000L, "MINUTE"), SECOND(1000L, "SECOND");

    public static String YEAR_NAME = "YEAR";
    public static String MONTH_NAME = "MONTH";
    public static String WEEK_NAME = "WEEK";
    public static String DAY_NAME = "DAY";
    public static String HOUR_NAME = "HOUR";
    public static String MINUTE_NAME = "MINUTE";
    public static String SECOND_NAME = "SECOND";
    private final Long _value;
    private final String _name;

    TimeUnit(Long value, String name) {
        this._value = value;
        this._name = name;
    }

    public static String GetName(TimeUnit timeUnit) {
        return switch (timeUnit) {
            case YEAR -> TimeUnit.YEAR_NAME;
            case MONTH -> TimeUnit.MONTH_NAME;
            case WEEK -> TimeUnit.WEEK_NAME;
            case DAY -> TimeUnit.DAY_NAME;
            case HOUR -> TimeUnit.HOUR_NAME;
            case MINUTE -> TimeUnit.MINUTE_NAME;
            case SECOND -> TimeUnit.SECOND_NAME;
        };
    }

    public static TimeUnit GetFromName(String name) {
        if (name.equalsIgnoreCase(TimeUnit.YEAR_NAME))
            return TimeUnit.YEAR;

        if (name.equalsIgnoreCase(TimeUnit.MONTH_NAME))
            return TimeUnit.MONTH;

        if (name.equalsIgnoreCase(TimeUnit.WEEK_NAME))
            return TimeUnit.WEEK;

        if (name.equalsIgnoreCase(TimeUnit.DAY_NAME))
            return TimeUnit.DAY;

        if (name.equalsIgnoreCase(TimeUnit.HOUR_NAME))
            return TimeUnit.HOUR;

        if (name.equalsIgnoreCase(TimeUnit.MINUTE_NAME))
            return TimeUnit.MINUTE;

        if (name.equalsIgnoreCase(TimeUnit.SECOND_NAME))
            return TimeUnit.SECOND;
        return null;
    }

    public String GetName() {
        return this._name;
    }

    public Long GetValue() {
        return this._value;
    }

    @Override
    public String toString() {
        return "TimeUnit{" + "value=" + this._value + '}';
    }
}
