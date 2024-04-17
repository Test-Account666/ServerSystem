package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener extends CommandUtils implements Listener {

    public QuitListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!this.plugin.getVanish().getVanishList().contains(e.getPlayer().getUniqueId())) {
            if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.QuitMessage.Change"))
                if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.QuitMessage.Send"))
                    e.setQuitMessage(
                            ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.QuitMessage.Message"))
                                     .replace("<PLAYER>", e.getPlayer().getName())
                                     .replace("<PLAYERDISPLAY>", e.getPlayer().getDisplayName())
                                     .replace("<SENDER>", e.getPlayer().getName())
                                     .replace("<SENDERDISPLAY>", e.getPlayer().getDisplayName()));
                else
                    e.setQuitMessage(null);
        } else
            e.setQuitMessage(null);
    }
}
