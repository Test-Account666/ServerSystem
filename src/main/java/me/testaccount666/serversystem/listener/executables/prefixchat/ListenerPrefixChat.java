package me.testaccount666.serversystem.listener.executables.prefixchat;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

public class ListenerPrefixChat implements Listener {
    private final ChatVaultAPI _chatVaultAPI;
    private final boolean _enabled;

    public ListenerPrefixChat() {
        var enabled = ServerSystem.Instance.getConfigManager().getGeneralConfig().getBoolean("Chat.PrefixChat.Enabled");

        if (!ChatVaultAPI.isVaultInstalled() || !enabled) {
            _chatVaultAPI = null;
            _enabled = false;
            return;
        }

        _chatVaultAPI = new ChatVaultAPI();
        _enabled = _chatVaultAPI.setupChat();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!_enabled) return;

        var player = event.getPlayer();
        var prefix = _chatVaultAPI.getChat().getPlayerPrefix(player);
        var suffix = _chatVaultAPI.getChat().getPlayerSuffix(player);
        if (prefix == null) prefix = "";
        if (suffix == null) suffix = "";

        prefix = prefix.replace("%", "%%");
        suffix = suffix.replace("%", "%%");

        var userOptional = ServerSystem.Instance.getUserManager().getUser(player);
        if (userOptional.isEmpty()) {
            ServerSystem.getLog().warning("Couldn't cache User '${player.getName()}'! This should not happen!");
            return;
        }
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) {
            ServerSystem.getLog().warning("User '${player.getName()}' is cached as Offline User! This should not happen!");
            return;
        }
        var user = (User) cachedUser.getOfflineUser();

        var finalSuffix = suffix;
        var finalPrefix = prefix;
        var chatFormatOptional = general("ChatFormat", user)
                .sender("%1\$s").prefix(false).send(false)
                .preModifier(message -> message.replace("%", "%%")
                        .replace("<PREFIX>", finalPrefix)
                        .replace("<SUFFIX>", finalSuffix)
                        .replace("<MESSAGE>", "%2\$s")).build();
        if (chatFormatOptional.isEmpty()) return;
        var chatFormat = chatFormatOptional.get();
        event.setFormat(chatFormat);
    }
}
