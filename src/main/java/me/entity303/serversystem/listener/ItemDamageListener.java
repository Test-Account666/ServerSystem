package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageListener implements Listener {

    protected final ServerSystem _plugin;

    public ItemDamageListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnItemDamage(PlayerItemDamageEvent event) {
        var currentDurability = event.getItem().getType().getMaxDurability() - event.getDamage();
        if (currentDurability > 20 || currentDurability <= -1)
            return;

        this._plugin.GetVersionStuff()
                   .GetActionBar()
                   .SendActionBar(event.getPlayer(), this._plugin.GetMessages()
                                                            .GetMessage("break", "break", event.getPlayer(), null, "ItemBreaking")
                                                            .replace("\"", "\\\"")
                                                            .replace("<ITEM>", event.getItem().getType().name())
                                                            .replace("<DURABILITY>", String.valueOf(currentDurability + 1))
                                                            .replace("<MAXDURABILITY>", String.valueOf(event.getItem().getType().getMaxDurability() + 1)));
    }
}
