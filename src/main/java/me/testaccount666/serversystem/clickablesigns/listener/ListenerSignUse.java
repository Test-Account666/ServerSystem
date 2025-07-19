package me.testaccount666.serversystem.clickablesigns.listener;

import me.testaccount666.serversystem.ServerSystem;
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

        var signManager = ServerSystem.Instance.getSignManager();
        var location = clickedBlock.getLocation();
        var signOptional = signManager.getSignType(location);
        if (signOptional.isEmpty()) return;
        var signType = signOptional.get();

        var player = event.getPlayer();
        var userOptional = ServerSystem.Instance.getUserManager().getUser(player);
        if (userOptional.isEmpty()) return;
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) return;
        var user = (User) cachedUser.getOfflineUser();

        event.setCancelled(true);
        signType.clickAction().execute(user, sign);
    }
}
