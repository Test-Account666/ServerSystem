package me.testaccount666.serversystem.clickablesigns.executables.kit;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ActionKitSign extends AbstractSignClickAction {

    @Override
    public String getBasePermissionNode() {
        return "ClickableSigns.Kit";
    }

    @Override
    protected boolean executeAction(User user, Sign sign, FileConfiguration config) {
        var kitManager = ServerSystem.Instance.getRegistry().getService(KitManager.class);
        if (kitManager == null) {
            sign("Kit.NoKitManager", user).build();
            return false;
        }

        var kitName = config.getString("KitName", sign.getLine(1));
        kitName = ChatColor.stripColor(kitName);
        if (kitName.isEmpty()) {
            sign("Kit.NoKitSpecified", user).build();
            return false;
        }

        var kitOptional = kitManager.getKit(kitName.toLowerCase());
        if (kitOptional.isEmpty()) {
            var finalKitName = kitName;
            sign("Kit.KitNotFound", user)
                    .postModifier(message -> message.replace("<KIT>", finalKitName)).build();
            return false;
        }

        var kit = kitOptional.get();
        kit.giveKit(user.getPlayer());
        sign("Kit.KitGiven", user)
                .postModifier(message -> message.replace("<KIT>", kit.getDisplayName())).build();
        return true;
    }
}