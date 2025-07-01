package me.testaccount666.serversystem.userdata.listener;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserJoinListener implements Listener {

    @EventHandler
    public void onUserJoin(PlayerJoinEvent event) {
        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(event.getPlayer());

        if (cachedUserOptional.isEmpty()) throw new RuntimeException("Couldn't cache User '${event.getPlayer().getName()}'! This should not happen!");

        var user = cachedUserOptional.get();

        if (user.isOnlineUser()) return;

        user.convertToOnlineUser();
    }
}
