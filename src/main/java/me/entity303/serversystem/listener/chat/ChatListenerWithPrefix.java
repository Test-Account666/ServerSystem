package me.entity303.serversystem.listener.chat;

import me.entity303.serversystem.bansystem.ManagerMute;
import me.entity303.serversystem.bansystem.Mute;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListenerWithPrefix extends MessageUtils implements Listener {
    private final String format;

    public ChatListenerWithPrefix(ServerSystem plugin, String format) {
        super(plugin);
        this.format = format;
    }

    @EventHandler
    public void onChatWithPrefix(AsyncPlayerChatEvent e) {
        ManagerMute muteManager = this.plugin.getMuteManager();
        if (muteManager.isMuted(e.getPlayer())) {
            Mute mute = muteManager.getMute(e.getPlayer());
            if (mute.getUNMUTE_TIME() > 0) if (mute.getUNMUTE_TIME() <= System.currentTimeMillis())
                muteManager.removeMute(e.getPlayer().getUniqueId());
            if (!mute.isSHADOW()) {
                String senderName = null;
                try {
                    senderName = Bukkit.getOfflinePlayer(UUID.fromString(mute.getSENDER_UUID())).getName();
                } catch (Exception ignored) {
                }
                if (senderName == null) senderName = mute.getSENDER_UUID();
                e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Mute.Muted", "mute", "mute", senderName, e.getPlayer()).replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));
                e.setCancelled(true);
                return;
            }
            e.getRecipients().removeIf(all -> all != e.getPlayer());
        }

        if (this.plugin.getVault() == null) throw new NullPointerException("Cannot use vault when it doesn't exist!");

        String prefix = ChatColor.translateAlternateColorCodes('&', this.plugin.getVault().getChat().getPlayerPrefix(e.getPlayer()));
        String suffix = ChatColor.translateAlternateColorCodes('&', this.plugin.getVault().getChat().getPlayerSuffix(e.getPlayer()));
        if (this.plugin.getPermissions().hasPerm(e.getPlayer(), "colorchat", true))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage().replace("%", "%%")));
        else
            e.setMessage(e.getMessage().replace("%", "%%"));
        e.setFormat(ChatColor.translateAlternateColorCodes('&', this.format).replace("<prefix>", prefix).replace("<suffix>", suffix).replace("<player>", e.getPlayer().getDisplayName()).replace("<message>", e.getMessage()));
    }
}
