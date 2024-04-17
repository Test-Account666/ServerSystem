package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageListener extends CommandUtils implements Listener {

    public ItemDamageListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        var currentDurability = e.getItem().getType().getMaxDurability() - e.getDamage();
        if (currentDurability > 20 || currentDurability <= -1)
            return;

        this.plugin.getVersionStuff()
                   .getActionBar()
                   .sendActionBar(e.getPlayer(), this.plugin.getMessages()
                                                            .getMessage("break", "break", e.getPlayer(), null, "ItemBreaking")
                                                            .replace("\"", "\\\"")
                                                            .replace("<ITEM>", e.getItem().getType().name())
                                                            .replace("<DURABILITY>", String.valueOf(currentDurability + 1))
                                                            .replace("<MAXDURABILITY>", String.valueOf(e.getItem().getType().getMaxDurability() + 1)));
    }
}
