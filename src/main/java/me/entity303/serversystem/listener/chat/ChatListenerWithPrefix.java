package me.entity303.serversystem.listener.chat;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListenerWithPrefix extends CommandUtils implements Listener {
    private final String _format;
    private final boolean _withPrefix;

    public ChatListenerWithPrefix(ServerSystem plugin, boolean withPrefix, String format) {
        super(plugin);
        this._format = format;
        this._withPrefix = withPrefix;
    }

    @EventHandler
    public void OnChatWithPrefix(AsyncPlayerChatEvent event) {
        var muteManager = this._plugin.GetMuteManager();
        if (muteManager.IsMuted(event.getPlayer())) {
            var mute = muteManager.GetMute(event.getPlayer());
            if (mute.GetExpireTime() > 0)
                if (mute.GetExpireTime() <= System.currentTimeMillis())
                    muteManager.RemoveMute(event.getPlayer().getUniqueId());
            if (!mute.IsShadow()) {
                String senderName = null;
                try {
                    senderName = Bukkit.getOfflinePlayer(UUID.fromString(mute.GetSenderUuid())).getName();
                } catch (Exception ignored) {
                }
                if (senderName == null)
                    senderName = mute.GetSenderUuid();
                event.getPlayer()
                 .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                 .GetMessage("mute", "mute", senderName, event.getPlayer(), "Mute.Muted")
                                                                                 .replace("<UNMUTE_DATE>", mute.GetExpireDate()));
                event.setCancelled(true);
                return;
            }
            event.getRecipients().removeIf(all -> all != event.getPlayer());
        }

        if (!this._withPrefix)
            return;

        if (this._plugin.GetVault() == null)
            throw new NullPointerException("Cannot use vault when it doesn't exist!");

        var prefix = ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetVault().GetChat().getPlayerPrefix(event.getPlayer()));
        var suffix = ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetVault().GetChat().getPlayerSuffix(event.getPlayer()));
        if (this._plugin.GetPermissions().HasPermission(event.getPlayer(), "colorchat", true))
            event.setMessage(ChatColor.TranslateAlternateColorCodes('&', event.getMessage().replace("%", "%%")));
        else
            event.setMessage(event.getMessage().replace("%", "%%"));
        event.setFormat(ChatColor.TranslateAlternateColorCodes('&', this._format)
                             .replace("<prefix>", prefix)
                             .replace("<suffix>", suffix)
                             .replace("<player>", event.getPlayer().getDisplayName())
                             .replace("<message>", event.getMessage()));
    }
}
