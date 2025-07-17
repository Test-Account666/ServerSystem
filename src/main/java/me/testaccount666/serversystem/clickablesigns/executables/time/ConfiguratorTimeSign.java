package me.testaccount666.serversystem.clickablesigns.executables.time;

import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator;
import me.testaccount666.serversystem.clickablesigns.SignType;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ConfiguratorTimeSign extends AbstractSignConfigurator {

    @Override
    protected String getCreatePermissionNode() {
        return "ClickableSigns.Time.Create";
    }

    @Override
    protected SignType getSignType() {
        return SignType.TIME;
    }

    @Override
    protected boolean validateConfiguration(User user, Sign sign, YamlConfiguration config) {
        var front = sign.getSide(Side.FRONT);
        var timeType = front.getLine(1).toLowerCase();
        if (timeType.isEmpty()) {
            sign("Time.NoTimeSpecified", user).build();
            return false;
        }

        if (!isValidTimeType(timeType)) {
            sign("Time.InvalidTime", user)
                    .postModifier(message -> message.replace("<TIME>", timeType)).build();
            return false;
        }

        front.line(0, ComponentColor.translateToComponent(SignType.TIME.signName()));
        front.line(1, ComponentColor.translateToComponent("&2${timeType}"));
        var back = sign.getSide(Side.BACK);
        for (var index = 0; index < 4; index++) back.line(index, front.line(index));
        sign.update();
        return true;
    }

    @Override
    protected void addSignSpecificConfiguration(User user, Sign sign, FileConfiguration config) {
        var timeType = sign.getSide(Side.FRONT).getLine(1).toLowerCase();
        timeType = ChatColor.stripColor(timeType);
        config.set("TimeType", timeType);
    }

    @Override
    protected String getSuccessMessageKey() {
        return "Time.Created";
    }

    private boolean isValidTimeType(String timeType) {
        return timeType.equals("day") ||
                timeType.equals("night") ||
                timeType.equals("noon") ||
                timeType.equals("midnight") ||
                timeType.matches("\\d+");
    }
}