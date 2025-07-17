package me.testaccount666.serversystem.clickablesigns;

import me.testaccount666.serversystem.clickablesigns.cost.CostHandler;
import me.testaccount666.serversystem.clickablesigns.util.SignUtils;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

/**
 * Abstract base class for sign click actions.
 * Handles common functionality such as permission checking and cost handling.
 */
public abstract class AbstractSignClickAction implements SignClickAction {

    @Override
    public String getUsePermissionNode() {
        return "${getBasePermissionNode()}.Use";
    }

    @Override
    public String getDestroyPermissionNode() {
        return "${getBasePermissionNode()}.Destroy";
    }

    /**
     * Executes the sign-specific action.
     * This method is called after permission and cost checks have passed.
     *
     * @param user   The user who clicked the sign
     * @param sign   The sign that was clicked
     * @param config The sign configuration
     * @return true if the action was successful, false otherwise
     */
    protected abstract boolean executeAction(User user, Sign sign, FileConfiguration config);

    @Override
    public void execute(User user, Sign sign) {
        if (!PermissionManager.hasPermission(user, getUsePermissionNode(), false)) {
            general("NoPermission", user)
                    .postModifier(message -> message.replace("<PERMISSION>",
                            PermissionManager.getPermission(getUsePermissionNode()))).build();
            return;
        }

        var config = SignUtils.loadSignConfig(sign.getLocation());

        if (!CostHandler.deductCost(user, config)) return;
        if (!executeAction(user, sign, config)) CostHandler.refundCost(user, config);
    }
}