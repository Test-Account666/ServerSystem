package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    protected final ServerSystem _plugin;

    public QuitListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        if (!this._plugin.GetVanish().GetVanishList().contains(event.getPlayer().getUniqueId())) {
            if (this._plugin.GetMessages().GetConfiguration().GetBoolean("Messages.Misc.QuitMessage.Change"))
                if (this._plugin.GetMessages().GetConfiguration().GetBoolean("Messages.Misc.QuitMessage.Send"))
                    event.setQuitMessage(
                            ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.QuitMessage.Message"))
                                     .replace("<PLAYER>", event.getPlayer().getName())
                                     .replace("<PLAYERDISPLAY>", event.getPlayer().getDisplayName())
                                     .replace("<SENDER>", event.getPlayer().getName())
                                     .replace("<SENDERDISPLAY>", event.getPlayer().getDisplayName()));
                else
                    event.setQuitMessage(null);
        } else
            event.setQuitMessage(null);
    }
}
