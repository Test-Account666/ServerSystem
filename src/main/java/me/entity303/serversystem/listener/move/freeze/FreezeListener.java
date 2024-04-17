package me.entity303.serversystem.listener.move.freeze;

import me.entity303.serversystem.commands.executable.FreezeCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;

public class FreezeListener implements Listener {
    private final ServerSystem plugin;
    private Object namespacedKey;

    public FreezeListener(ServerSystem plugin) {
        this.plugin = plugin;
        try {
            var clazz = Class.forName("org.bukkit.NamespacedKey");
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

        var from = e.getFrom();

        from.setYaw(e.getTo().getYaw());
        from.setPitch(e.getTo().getPitch());

        var passengers = new LinkedList<Entity>();
        for (var passenger : e.getPlayer().getPassengers()) {
            passenger.eject();
            passengers.add(passenger);
        }

        e.getPlayer().teleport(from);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (var passenger : passengers)
                e.getPlayer().addPassenger(passenger);
        }, 10L);

        e.setTo(from);
    }

    private boolean isFrozen(Player player) {
        return FreezeCommand.isFrozen(player, (org.bukkit.NamespacedKey) this.namespacedKey);
    }
}
