package me.entity303.serversystem.listener.move;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class FreezeListener implements Listener {
    private final NamespacedKey namespacedKey;
    private final ServerSystem plugin;
    private boolean persistent = false;
    private boolean checked = false;

    public FreezeListener(ServerSystem plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(this.plugin, "freeze");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!this.isFrozen(e.getPlayer()))
            return;

        e.getPlayer().sendBlockChange(e.getPlayer().getLocation().clone().add(0, -1, 0), Material.BARRIER.createBlockData());

        Location from = e.getFrom();

        from.setYaw(e.getTo().getYaw());
        from.setPitch(e.getTo().getPitch());

        e.getPlayer().teleport(from);
        e.setTo(from);
    }

    private boolean isFrozen(Player player) {
        if (!this.checked) {
            this.checked = true;

            try {
                Class.forName("org.bukkit.persistence.PersistentDataHolder");
                this.persistent = true;
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            }
        }

        if (this.persistent) {
            org.bukkit.persistence.PersistentDataHolder dataHolder = (org.bukkit.persistence.PersistentDataHolder) player;

            if (!dataHolder.getPersistentDataContainer().has(this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE))
                return false;

            byte frozen = dataHolder.getPersistentDataContainer().get(this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE);
            return frozen >= 1;
        }

        if (!player.hasMetadata("freeze"))
            return false;

        return Objects.requireNonNull(player.getMetadata("freeze").stream().findFirst().orElse(null)).asBoolean();
    }
}
