package me.testaccount666.serversystem.commands.executables.spawn;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandSpawn.class)
public class ListenerSpawn implements Listener {
    private CommandSpawn _commandSpawn;

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandSpawn commandSpawn)) return;

            _commandSpawn = commandSpawn;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        if (player.hasPlayedBefore()) {
            if (!_commandSpawn.teleportOnJoin) return;
        } else if (!_commandSpawn.teleportOnFirstJoin) return;

        // Delay by a second, because teleporting instantly sometimes doesn't work
        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> {
            var userOptional = ServerSystem.Instance.getUserManager().getUser(player);
            if (userOptional.isEmpty()) return;

            var cachedUser = userOptional.get();
            if (cachedUser.isOfflineUser()) return;

            var user = (User) cachedUser.getOfflineUser();

            _commandSpawn.handleSpawnCommand(user, "spawn");
        }, 20L);
    }
}
