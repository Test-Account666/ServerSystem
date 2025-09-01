package me.testaccount666.serversystem.clickablesigns.executables.kit;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator;
import me.testaccount666.serversystem.clickablesigns.SignType;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ConfiguratorKitSign extends AbstractSignConfigurator {

    @Override
    protected String getCreatePermissionNode() {
        return "ClickableSigns.Kit.Create";
    }

    @Override
    protected SignType getSignType() {
        return SignType.KIT;
    }

    @Override
    protected boolean validateConfiguration(User user, Sign sign, YamlConfiguration config) {
        var kitManager = ServerSystem.Instance.getRegistry().getService(KitManager.class);
        if (kitManager == null) {
            sign("Kit.NoKitManager", user).build();
            return false;
        }

        var front = sign.getSide(Side.FRONT);
        var kitName = ComponentColor.componentToString(front.line(1));
        if (kitName.isEmpty()) {
            sign("Kit.NoKitSpecified", user).build();
            return false;
        }

        if (!kitManager.kitExists(kitName.toLowerCase())) {
            sign("Kit.KitNotFound", user)
                    .postModifier(message -> message.replace("<KIT>", kitName)).build();
            return false;
        }

        front.line(0, ComponentColor.translateToComponent(SignType.KIT.signName()));
        front.line(1, ComponentColor.translateToComponent("&2${kitName}"));
        var back = sign.getSide(Side.BACK);
        for (var index = 0; index < 4; index++) back.line(index, front.line(index));
        sign.update();
        return true;
    }

    @Override
    protected void addSignSpecificConfiguration(User user, Sign sign, FileConfiguration config) {
        var kitName = sign.getSide(Side.FRONT).getLine(1);
        kitName = ChatColor.stripColor(kitName);
        config.set("KitName", kitName);
    }

    @Override
    protected String getSuccessMessageKey() {
        return "Kit.Created";
    }
}