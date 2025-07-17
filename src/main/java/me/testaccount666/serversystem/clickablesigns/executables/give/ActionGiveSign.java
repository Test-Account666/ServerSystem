package me.testaccount666.serversystem.clickablesigns.executables.give;

import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ActionGiveSign extends AbstractSignClickAction {

    @Override
    public String getBasePermissionNode() {
        return "ClickableSigns.Give";
    }

    @Override
    protected boolean executeAction(User user, Sign sign, FileConfiguration config) {
        var item = config.getItemStack("Item");
        if (item == null) {
            sign("Give.NoItem", user).build();
            return false;
        }

        var inventory = Bukkit.createInventory(null, 27, "Give Sign");
        for (var index = 0; index < inventory.getSize(); index++) inventory.setItem(index, item);

        user.getPlayer().openInventory(inventory);
        return true;
    }
}
