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
    private final ServerSystem _plugin;
    private Object _namespacedKey;

    public FreezeListener(ServerSystem plugin) {
        this._plugin = plugin;
        try {
            var clazz = Class.forName("org.bukkit.NamespacedKey");
            this._namespacedKey = clazz.getConstructor(Plugin.class, String.class).newInstance(plugin, "freeze");
        } catch (Throwable ignored) {
            this._namespacedKey = null;
        }
    }

    @EventHandler
    public void OnMove(PlayerMoveEvent event) {
        if (!this.IsFrozen(event.getPlayer())) return;

        event.getPlayer().sendBlockChange(event.getPlayer().getLocation().clone().add(0, -1, 0), Material.BARRIER.createBlockData());

        var from = event.getFrom();

        from.setYaw(event.getTo().getYaw());
        from.setPitch(event.getTo().getPitch());

        var passengers = new LinkedList<Entity>();
        for (var passenger : event.getPlayer().getPassengers()) {
            passenger.eject();
            passengers.add(passenger);
        }

        event.getPlayer().teleport(from);

        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
            for (var passenger : passengers)
                event.getPlayer().addPassenger(passenger);
        }, 10L);

        event.setTo(from);
    }

    private boolean IsFrozen(Player player) {
        return FreezeCommand.IsFrozen(player, (org.bukkit.NamespacedKey) this._namespacedKey);
    }
}
