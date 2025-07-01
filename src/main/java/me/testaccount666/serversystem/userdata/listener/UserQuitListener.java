package me.testaccount666.serversystem.userdata.listener;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserQuitListener implements Listener {

    @EventHandler
    public void onUserQuit(PlayerQuitEvent event) {
        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(event.getPlayer());

        if (cachedUserOptional.isEmpty()) {
            Bukkit.getLogger().warning("User '${event.getPlayer().getName()}' is not cached! This should not happen!");
            return;
        }

        var user = cachedUserOptional.get();

        //TODO: Should we really still save in this case?
        if (user.isOfflineUser()) Bukkit.getLogger().warning("User '${event.getPlayer().getName()}' is not an online user! This should not happen!");

        user.getOfflineUser().save();
    }
}
