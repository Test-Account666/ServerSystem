package me.testaccount666.serversystem.clickablesigns.listener;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.clickablesigns.SignManager;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerSignUse implements Listener {
    @EventHandler
    public void onSignUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (!(clickedBlock.getState() instanceof Sign sign)) return;

        var registry = ServerSystem.getInstance().getRegistry();
        var signManager = registry.getService(SignManager.class);
        var location = clickedBlock.getLocation();
        var signOptional = signManager.getSignType(location);
        if (signOptional.isEmpty()) return;
        var signType = signOptional.get();

        var player = event.getPlayer();
        var userManager = registry.getService(UserManager.class);
        var userOptional = userManager.getUser(player);
        if (userOptional.isEmpty()) return;
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) return;
        var user = (User) cachedUser.getOfflineUser();

        event.setCancelled(true);
        signType.clickAction().execute(user, sign);
    }
}
