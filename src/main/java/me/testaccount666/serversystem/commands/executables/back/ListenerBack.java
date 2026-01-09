package me.testaccount666.serversystem.commands.executables.back;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;

@RequiredCommands(requiredCommands = CommandBack.class)
public class ListenerBack implements Listener {

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        return requiredCommands.stream().anyMatch(CommandBack.class::isInstance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        var userOptional = ServerSystem.getInstance().getRegistry().getService(UserManager.class).getUser(event.getPlayer());
        if (userOptional.isEmpty()) return;

        var cachedUser = userOptional.get();
        var user = cachedUser.getOfflineUser();

        user.setLastTeleportLocation(event.getFrom());
        user.setLastBackType(CommandBack.BackType.TELEPORT);
        user.save();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        var userOptional = ServerSystem.getInstance().getRegistry().getService(UserManager.class).getUser(event.getEntity());
        if (userOptional.isEmpty()) return;

        var cachedUser = userOptional.get();
        var user = cachedUser.getOfflineUser();

        user.setLastDeathLocation(event.getPlayer().getLocation());
        user.setLastBackType(CommandBack.BackType.DEATH);
        user.save();
    }
}
