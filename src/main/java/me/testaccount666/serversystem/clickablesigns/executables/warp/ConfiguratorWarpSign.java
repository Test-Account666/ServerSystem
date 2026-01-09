package me.testaccount666.serversystem.clickablesigns.executables.warp;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator;
import me.testaccount666.serversystem.clickablesigns.SignType;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ConfiguratorWarpSign extends AbstractSignConfigurator {

    @Override
    protected String getCreatePermissionNode() {
        return "ClickableSigns.Warp.Create";
    }

    @Override
    protected SignType getSignType() {
        return SignType.WARP;
    }

    @Override
    protected boolean validateConfiguration(User user, Sign sign, YamlConfiguration config) {
        var warpManager = ServerSystem.getInstance().getRegistry().getService(WarpManager.class);
        if (warpManager == null) {
            sign("Warp.NoWarpManager", user).build();
            return false;
        }

        var front = sign.getSide(Side.FRONT);
        var warpName = front.getLine(1);
        if (warpName.isEmpty()) {
            sign("Warp.NoWarpSpecified", user).build();
            return false;
        }

        if (!warpManager.warpExists(warpName.toLowerCase())) {
            sign("Warp.WarpNotFound", user)
                    .postModifier(message -> message.replace("<WARP>", warpName)).build();
            return false;
        }

        front.line(0, ComponentColor.translateToComponent(SignType.WARP.signName()));
        front.line(1, ComponentColor.translateToComponent("&2${warpName}"));
        var back = sign.getSide(Side.BACK);
        for (var index = 0; index < 4; index++) back.line(index, front.line(index));
        sign.update();
        return true;
    }

    @Override
    protected void addSignSpecificConfiguration(User user, Sign sign, FileConfiguration config) {
        var warpName = sign.getSide(Side.FRONT).getLine(1);
        warpName = ChatColor.stripColor(warpName);
        config.set("WarpName", warpName);
    }

    @Override
    protected String getSuccessMessageKey() {
        return "Warp.Created";
    }
}