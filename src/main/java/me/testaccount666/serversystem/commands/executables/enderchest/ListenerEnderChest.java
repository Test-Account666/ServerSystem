package me.testaccount666.serversystem.commands.executables.enderchest;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.executables.enderchest.offline.CommandOfflineEnderChest;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandEnderChest.class)
public class ListenerEnderChest implements Listener {
    private CommandOfflineEnderChest _enderChest;

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandEnderChest commandEnderChest)) return;
            var enderChestLoader = commandEnderChest.offlineEnderChest.enderChestLoader;
            if (enderChestLoader == null) return;

            _enderChest = commandEnderChest.offlineEnderChest;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler
    public void onViewedQuit(PlayerQuitEvent event) {
        var viewedPlayer = event.getPlayer();
        var inventory = viewedPlayer.getEnderChest();

        var viewers = new ArrayList<>(inventory.getViewers());
        inventory.close();

        Bukkit.getScheduler().runTaskLater(ServerSystem.getInstance(), () -> viewers.forEach(viewer -> {
            if (!(viewer instanceof Player player)) return;

            var optionalUser = ServerSystem.getInstance().getRegistry().getService(UserManager.class).getUser(player);
            if (optionalUser.isEmpty()) return;

            var cachedUser = optionalUser.get();
            if (cachedUser.isOfflineUser()) return;
            var user = (User) cachedUser.getOfflineUser();

            _enderChest.executeEnderChestCommand(user, viewedPlayer.getName());
        }), 10L);
    }
}
