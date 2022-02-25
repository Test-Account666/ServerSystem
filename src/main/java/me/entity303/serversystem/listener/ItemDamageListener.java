package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageListener extends MessageUtils implements Listener {

    public ItemDamageListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        int damageValue = e.getItem().getType().getMaxDurability() - (e.getItem().getDurability() + e.getDamage());
        if (damageValue <= 20 && damageValue != -1)
            this.plugin.getVersionStuff().getActionBar().sendActionBar(e.getPlayer(), this.getMessage("ItemBreaking", "break", "break", e.getPlayer(), null).replace("\"", "\\\"").replace("<ITEM>", e.getItem().getType().name()).replace("<DURABILITY>", String.valueOf(damageValue + 1)).replace("<MAXDURABILITY>", String.valueOf(e.getItem().getType().getMaxDurability() + 1)));
    }
}
