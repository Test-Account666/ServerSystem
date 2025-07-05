package me.testaccount666.serversystem.commands.executables.back;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandBack.class)
public class ListenerBack implements Listener {
    private CommandBack _commandBack;

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandBack commandBack)) return;

            _commandBack = commandBack;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(event.getPlayer());
        if (userOptional.isEmpty()) return;

        var cachedUser = userOptional.get();
        var user = cachedUser.getOfflineUser();

        user.setLastTeleportLocation(event.getFrom());
        user.setLastBackType(CommandBack.BackType.TELEPORT);
        user.save();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(event.getEntity());
        if (userOptional.isEmpty()) return;

        var cachedUser = userOptional.get();
        var user = cachedUser.getOfflineUser();

        user.setLastDeathLocation(event.getPlayer().getLocation());
        user.setLastBackType(CommandBack.BackType.DEATH);
        user.save();
    }
}
