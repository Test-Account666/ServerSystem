package me.testaccount666.serversystem.commands.executables.teleportask;

import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandTeleportAsk.class)
public class ListenerTeleportRequest implements Listener {
    private CommandTeleportAsk _commandTeleportAsk;

    private static double getDistance(PlayerMoveEvent event) {
        var fromX = event.getFrom().getX();
        var toX = event.getTo().getX();

        var fromY = event.getFrom().getY();
        var toY = event.getTo().getY();

        var fromZ = event.getFrom().getZ();
        var toZ = event.getTo().getZ();

        var fromWorld = event.getFrom().getWorld();
        var toWorld = event.getTo().getWorld();

        var from = new Location(fromWorld, fromX, fromY, fromZ);
        var to = new Location(toWorld, toX, toY, toZ);

        return from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())? from.distance(to) : Double.MAX_VALUE;
    }

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandTeleportAsk commandTeleportAsk)) return;

            _commandTeleportAsk = commandTeleportAsk;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler
    public void onTeleporterMove(PlayerMoveEvent event) {
        var distance = getDistance(event);
        if (distance < .1) return;

        Collections.unmodifiableSet(_commandTeleportAsk.activeTeleportRequests).forEach(teleportRequest -> {
            var teleporter = teleportRequest.isTeleportHere()? teleportRequest.getReceiver() : teleportRequest.getSender();

            if (teleporter.getPlayer() == null) return;
            var teleporterPlayer = teleporter.getPlayer();

            if (!event.getPlayer().getUniqueId().equals(teleporterPlayer.getUniqueId())) return;

            Bukkit.getScheduler().cancelTask(teleportRequest.getTimerId());
            teleportRequest.setCancelled(true);
            _commandTeleportAsk.activeTeleportRequests.remove(teleportRequest);

            var messageOptional = MessageManager.getCommandMessage(teleporter, "TeleportAsk.Moved", null, null);
            messageOptional.ifPresent(teleporterPlayer::sendMessage);
        });
    }
}
