package me.testaccount666.serversystem.clickablesigns.executables.give;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator;
import me.testaccount666.serversystem.clickablesigns.SignManager;
import me.testaccount666.serversystem.clickablesigns.SignType;
import me.testaccount666.serversystem.clickablesigns.cost.CostType;
import me.testaccount666.serversystem.clickablesigns.util.SignUtils;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.BiDirectionalHashMap;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.logging.Level;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;
import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ConfiguratorGiveSign extends AbstractSignConfigurator implements Listener {
    private static final BiDirectionalHashMap<User, Sign> _CONFIGURATORS = new BiDirectionalHashMap<>();

    @Override
    protected String getCreatePermissionNode() {
        return "ClickableSigns.Give.Create";
    }

    @Override
    protected SignType getSignType() {
        return SignType.GIVE;
    }

    @Override
    protected boolean validateConfiguration(User user, Sign sign, YamlConfiguration config) {
        return true;
    }

    @Override
    protected void addSignSpecificConfiguration(User user, Sign sign, FileConfiguration config) {
    }

    @Override
    protected String getSuccessMessageKey() {
        return "Give.Created";
    }

    @Override
    public void execute(User user, Sign sign) {
        if (!validatePermission(user)) return;

        _CONFIGURATORS.put(user, sign);
        sign("Give.Configuring", user).build();
    }

    @EventHandler
    public void onSignRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (!(clickedBlock.getState() instanceof Sign sign)) return;
        var userOptional = _CONFIGURATORS.getKey(sign);
        if (userOptional.isEmpty()) return;
        var user = userOptional.get();
        if (!user.getUuid().equals(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);

        var itemToGive = event.getPlayer().getInventory().getItemInMainHand();
        if (itemToGive.isEmpty()) {
            sign("Give.NoItem", user).build();
            return;
        }

        var signFile = SignUtils.getSignFile(sign.getLocation());
        var config = YamlConfiguration.loadConfiguration(signFile);

        config.set("Key", getSignType().name());

        config.set("Cost.Type", CostType.NONE.name());
        config.set("Cost.Amount", 0);

        config.set("Item", itemToGive);

        try {
            config.save(signFile);
        } catch (IOException exception) {
            user.sendMessage(exception.getMessage());
            ServerSystem.getLog().log(Level.SEVERE, "Failed to save sign configuration ${signFile.getAbsolutePath()}", exception);
            return;
        }

        ServerSystem.getInstance().getRegistry().getService(SignManager.class).addSignType(sign.getLocation(), getSignType());
        _CONFIGURATORS.removeByValue(sign);

        var front = sign.getSide(Side.FRONT);
        front.line(0, ComponentColor.translateToComponent(SignType.GIVE.signName()));
        front.line(1, ComponentColor.translateToComponent("&2${itemToGive.getType().name()}"));
        var back = sign.getSide(Side.BACK);
        for (var index = 0; index < 4; index++) back.line(index, front.line(index));
        sign.update();
    }

    /**
     * Validates that the user has permission to create this sign.
     *
     * @param user The user to check
     * @return true if the user has permission, false otherwise
     */
    private boolean validatePermission(User user) {
        if (!PermissionManager.hasPermission(user, getCreatePermissionNode(), false)) {
            general("NoPermission", user)
                    .postModifier(message -> message.replace("<PERMISSION>",
                            PermissionManager.getPermission(getCreatePermissionNode()))).build();
            return false;
        }
        return true;
    }
}