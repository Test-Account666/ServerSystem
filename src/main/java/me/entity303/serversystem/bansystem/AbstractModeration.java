package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class AbstractModeration {
    protected final ServerSystem _plugin;
    protected final String _dateFormat;

    public AbstractModeration(ServerSystem plugin, String dateFormat) {
        this._plugin = plugin;
        this._dateFormat = dateFormat;
    }

    public String ConvertLongToDate(Long longDate) {
        if (longDate < 1)
            return this.GetPermanentBanName();

        var calendar = Calendar.getInstance();

        calendar.setTimeInMillis(longDate);

        var dateFormat = new SimpleDateFormat("yyyy:MM:dd:kk:mm:ss");
        var dates = dateFormat.format(calendar.getTime()).split(":");

        var year = dates[0];
        var month = dates[1];
        var day = dates[2];
        var hour = dates[3];
        var minute = dates[4];
        var second = dates[5];

        if (month.chars().count() == 1)
            month = "0" + month;

        if (day.chars().count() == 1)
            day = "0" + day;

        if (hour.chars().count() == 1)
            hour = "0" + hour;

        if (minute.chars().count() == 1)
            minute = "0" + minute;

        if (second.chars().count() == 1)
            second = "0" + second;

        return this._dateFormat.replace("<YEAR>", year)
                               .replace("<MONTH>", month)
                               .replace("<DAY>", day)
                               .replace("<HOUR>", hour)
                               .replace("<MINUTE>", minute)
                               .replace("<SECOND>", second);
    }

    protected String GetPermanentBanName() {
        return this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "PermaBan");
    }

    public abstract void Close();
}
