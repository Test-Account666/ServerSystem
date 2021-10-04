package me.Entity303.ServerSystem.Listener;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener extends ServerSystemCommand implements Listener {

    public QuitListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!this.plugin.getVanish().getVanishList().contains(e.getPlayer().getUniqueId())) {
            if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.QuitMessage.Change"))
                if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.QuitMessage.Send"))
                    e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.QuitMessage.Message")).replace("<PLAYER>", e.getPlayer().getName()).replace("<PLAYERDISPLAY>", e.getPlayer().getDisplayName()).replace("<SENDER>", e.getPlayer().getName()).replace("<SENDERDISPLAY>", e.getPlayer().getDisplayName()));
                else
                    e.setQuitMessage(null);
        } else e.setQuitMessage(null);
    }
}
