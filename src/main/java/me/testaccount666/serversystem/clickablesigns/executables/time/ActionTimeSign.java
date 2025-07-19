package me.testaccount666.serversystem.clickablesigns.executables.time;

import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ActionTimeSign extends AbstractSignClickAction {

    @Override
    public String getBasePermissionNode() {
        return "ClickableSigns.Time";
    }

    @Override
    protected boolean executeAction(User user, Sign sign, FileConfiguration config) {
        var timeType = config.getString("TimeType", sign.getLine(1)).toLowerCase();
        timeType = ChatColor.stripColor(timeType);
        if (timeType.isEmpty()) {
            sign("Time.NoTimeSpecified", user).build();
            return false;
        }

        var world = user.getPlayer().getWorld();
        long time;

        var finalTimeType = timeType;
        switch (timeType) {
            case "day" -> time = 1000;
            case "noon" -> time = 6000;
            case "night" -> time = 13000;
            case "midnight" -> time = 18000;
            default -> {
                try {
                    time = Long.parseLong(timeType);
                } catch (NumberFormatException ignored) {
                    sign("Time.InvalidTime", user)
                            .postModifier(message -> message.replace("<TIME>", finalTimeType)).build();
                    return false;
                }
            }
        }

        world.setTime(time);
        sign("Time.TimeSet", user)
                .postModifier(message -> message.replace("<TIME>", finalTimeType)).build();
        return true;
    }
}