package me.testaccount666.serversystem.clickablesigns;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.block.Sign;

public interface SignClickAction {
    void execute(User user, Sign sign);

    /**
     * The permission node for the permission required to use this sign.
     *
     * @return The permission node
     */
    String getUsePermissionNode();

    /**
     * The permission node for the permission required to break this+ sign.
     *
     * @return The permission node
     */
    String getDestroyPermissionNode();

    String getBasePermissionNode();
}
