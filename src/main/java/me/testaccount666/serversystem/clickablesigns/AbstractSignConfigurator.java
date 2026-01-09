package me.testaccount666.serversystem.clickablesigns;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.cost.CostType;
import me.testaccount666.serversystem.clickablesigns.util.SignUtils;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.logging.Level;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;
import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

/**
 * Abstract base class for sign configurators.
 * Handles common functionality such as permission checking and configuration saving.
 */
public abstract class AbstractSignConfigurator implements SignConfigurator {

    /**
     * The permission node for the permission required to create this sign.
     *
     * @return The permission node
     */
    protected abstract String getCreatePermissionNode();

    /**
     * The sign type for this configurator.
     *
     * @return The sign type
     */
    protected abstract SignType getSignType();

    /**
     * Validates the sign configuration.
     * This method is called before saving the configuration.
     *
     * @param user   The user who is configuring the sign
     * @param sign   The sign being configured
     * @param config The sign configuration
     * @return true if the configuration is valid, false otherwise
     */
    protected abstract boolean validateConfiguration(User user, Sign sign, YamlConfiguration config);

    /**
     * Adds sign-specific configuration.
     * This method is called after basic configuration has been set.
     *
     * @param user   The user who is configuring the sign
     * @param sign   The sign being configured
     * @param config The sign configuration
     */
    protected abstract void addSignSpecificConfiguration(User user, Sign sign, FileConfiguration config);

    /**
     * Gets the success message key for when the sign is successfully created.
     *
     * @return The message key
     */
    protected abstract String getSuccessMessageKey();

    @Override
    public void execute(User user, Sign sign) {
        if (!PermissionManager.hasPermission(user, getCreatePermissionNode(), false)) {
            general("NoPermission", user)
                    .postModifier(message -> message.replace("<PERMISSION>",
                            PermissionManager.getPermission(getCreatePermissionNode()))).build();
            return;
        }

        var signFile = SignUtils.getSignFile(sign.getLocation());
        var config = YamlConfiguration.loadConfiguration(signFile);

        config.set("Key", getSignType().name());

        config.set("Cost.Type", CostType.NONE.name());
        config.set("Cost.Amount", 0);

        if (!validateConfiguration(user, sign, config)) return;
        addSignSpecificConfiguration(user, sign, config);

        try {
            config.save(signFile);
        } catch (IOException exception) {
            user.sendMessage(exception.getMessage());
            ServerSystem.getLog().log(Level.SEVERE, "Failed to save sign configuration ${signFile.getAbsolutePath()}", exception);
            return;
        }

        ServerSystem.getInstance().getRegistry().getService(SignManager.class).addSignType(sign.getLocation(), getSignType());
        sign(getSuccessMessageKey(), user).build();
    }
}