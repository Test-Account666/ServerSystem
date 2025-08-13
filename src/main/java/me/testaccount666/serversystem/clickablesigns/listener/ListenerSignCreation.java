package me.testaccount666.serversystem.clickablesigns.listener;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.SignType;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ListenerSignCreation implements Listener {

    @EventHandler
    public void onSignCreation(SignChangeEvent event) {
        var firstLine = event.lines().getFirst();
        if (firstLine == null) return;
        var firstLineString = ComponentColor.componentToString(firstLine);
        if (!firstLineString.contains("[") || !firstLineString.contains("]")) return;

        var key = firstLineString.substring(firstLineString.indexOf("[") + 1, firstLineString.indexOf("]"));
        var optionalSignType = SignType.getSignTypeByKey(key);
        if (optionalSignType.isEmpty()) return;
        var signType = optionalSignType.get();

        var block = event.getBlock();
        var sign = (Sign) block.getState();


        var player = event.getPlayer();
        var registry = ServerSystem.Instance.getRegistry();
        var userManager = registry.getService(UserManager.class);
        var userOptional = userManager.getUser(player);
        if (userOptional.isEmpty()) return;
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) return;
        var user = (User) cachedUser.getOfflineUser();

        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> {
            var finalSign = (Sign) block.getState();
            signType.configurator().execute(user, finalSign);
        }, 1L);
    }
}
