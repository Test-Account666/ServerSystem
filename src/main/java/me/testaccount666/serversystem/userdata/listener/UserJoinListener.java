package me.testaccount666.serversystem.userdata.listener;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserJoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUserJoin(PlayerJoinEvent event) {
        var cachedUserOptional = ServerSystem.Instance.getRegistry().getService(UserManager.class).getUser(event.getPlayer().getUniqueId());

        if (cachedUserOptional.isEmpty()) throw new RuntimeException("Couldn't cache User '${event.getPlayer().getName()}'! This should not happen!");

        var cachedUser = cachedUserOptional.get();

        if (cachedUser.isOnlineUser()) return;

        cachedUser.convertToOnlineUser();

        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> {
            var user = cachedUser.getOfflineUser();
            if (user.getLogoutPosition() == null) return;

            event.getPlayer().teleport(user.getLogoutPosition());
        }, 10L);
    }
}
