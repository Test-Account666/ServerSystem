package me.testaccount666.serversystem.userdata.listener;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUserQuit(PlayerQuitEvent event) {
        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(event.getPlayer().getUniqueId());

        if (cachedUserOptional.isEmpty()) {
            ServerSystem.getLog().warning("(UserQuitListener) User '${event.getPlayer().getName()}' is not cached! This should not happen!");
            return;
        }

        var user = cachedUserOptional.get();
        var offlineUser = user.getOfflineUser();

        //TODO: Should we really still save in this case?
        if (user.isOfflineUser()) ServerSystem.getLog().warning("User '${event.getPlayer().getName()}' is not an online user! This should not happen!");

        offlineUser.setLogoutPosition(event.getPlayer().getLocation());

        offlineUser.save();
        user.convertToOfflineUser();
    }
}
