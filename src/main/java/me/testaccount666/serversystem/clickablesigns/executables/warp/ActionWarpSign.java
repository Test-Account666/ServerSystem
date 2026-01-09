package me.testaccount666.serversystem.clickablesigns.executables.warp;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ActionWarpSign extends AbstractSignClickAction {

    @Override
    public String getBasePermissionNode() {
        return "ClickableSigns.Warp";
    }

    @Override
    protected boolean executeAction(User user, Sign sign, FileConfiguration config) {
        var warpManager = ServerSystem.getInstance().getRegistry().getService(WarpManager.class);
        if (warpManager == null) {
            sign("Warp.NoWarpManager", user).build();
            return false;
        }

        var warpName = config.getString("WarpName", sign.getLine(1));
        warpName = ChatColor.stripColor(warpName);
        if (warpName.isEmpty()) {
            sign("Warp.NoWarpSpecified", user).build();
            return false;
        }

        var warpOptional = warpManager.getWarpByName(warpName);
        if (warpOptional.isEmpty()) {
            var finalWarpName = warpName;
            sign("Warp.WarpNotFound", user)
                    .postModifier(message -> message.replace("<WARP>", finalWarpName)).build();
            return false;
        }

        var warp = warpOptional.get();
        user.getPlayer().teleport(warp.getLocation());
        sign("Warp.Teleported", user)
                .postModifier(message -> message.replace("<WARP>", warp.getDisplayName())).build();
        return true;
    }
}