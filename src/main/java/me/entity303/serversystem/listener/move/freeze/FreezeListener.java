package me.entity303.serversystem.listener.move.freeze;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.Objects;

public class FreezeListener implements Listener {
    private Object namespacedKey;
    private final ServerSystem plugin;
    private boolean persistent = false;
    private boolean checked = false;

    public FreezeListener(ServerSystem plugin) {
        this.plugin = plugin;
        try {
            Class clazz = Class.forName("org.bukkit.NamespacedKey");
            this.namespacedKey = clazz.getConstructor(Plugin.class, String.class).newInstance(plugin, "freeze");
        } catch (Throwable ignored) {
            this.namespacedKey = null;
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!this.isFrozen(e.getPlayer()))
            return;

        e.getPlayer().sendBlockChange(e.getPlayer().getLocation().clone().add(0, -1, 0), Material.BARRIER.createBlockData());

        Location from = e.getFrom();

        from.setYaw(e.getTo().getYaw());
        from.setPitch(e.getTo().getPitch());

        LinkedList<Entity> passengers = new LinkedList<>();
        for (Entity passenger : e.getPlayer().getPassengers()) {
            passenger.eject();
            passengers.add(passenger);
        }

        e.getPlayer().teleport(from);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (Entity passenger : passengers)
                e.getPlayer().addPassenger(passenger);
        }, 10L);

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

            if (!dataHolder.getPersistentDataContainer().has((org.bukkit.NamespacedKey) this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE))
                return false;

            byte frozen = dataHolder.getPersistentDataContainer().get((org.bukkit.NamespacedKey) this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE);
            return frozen >= 1;
        }

        if (!player.hasMetadata("freeze"))
            return false;

        return Objects.requireNonNull(player.getMetadata("freeze").stream().findFirst().orElse(null)).asBoolean();
    }
}
