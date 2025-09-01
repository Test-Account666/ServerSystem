package me.testaccount666.serversystem.listener.executables.joinquitnotifier;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.managers.messages.MessageManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerJoin implements Listener {
    private final boolean _modifyMessage;
    private final boolean _sendMessage;
    private final String _message;
    private boolean _playSound;
    private Sound _sound;

    public ListenerJoin() {
        var configManager = ServerSystem.Instance.getRegistry().getService(ConfigurationManager.class);
        var config = configManager.getGeneralConfig();
        _modifyMessage = config.getBoolean("Join.Message.Enabled");
        _sendMessage = config.getBoolean("Join.Message.SendMessage");
        _message = config.getString("Join.Message.Message", "");
        _playSound = config.getBoolean("Join.Sound.Enabled");
        if (!_playSound) {
            _sound = null;
            return;
        }

        var soundString = config.getString("Join.Sound.Sound", "");

        var isMinecraft = true;
        if (soundString.contains(":")) {
            var space = soundString.substring(0, soundString.indexOf(":"));
            soundString = soundString.substring(soundString.indexOf(":") + 1);
            isMinecraft = space.equalsIgnoreCase("minecraft");
        }

        var soundKey = isMinecraft? NamespacedKey.minecraft(soundString) : NamespacedKey.fromString(soundString);
        if (soundKey == null) {
            _playSound = false;
            ServerSystem.getLog().warning("Failed to parse sound '${soundString}' for join message!");
            return;
        }

        _sound = Registry.SOUND_EVENT.get(soundKey);
        if (_sound == null) {
            _playSound = false;
            ServerSystem.getLog().warning("Failed to find sound '${soundString}' for join message!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleMessage(event);
        playSound();
    }

    private void playSound() {
        if (!_playSound) return;

        Bukkit.getOnlinePlayers().forEach(everyone -> everyone.playSound(everyone, _sound, 1F, 1F));
    }

    private void handleMessage(PlayerJoinEvent event) {
        if (!_modifyMessage) return;

        if (!_sendMessage) {
            event.setJoinMessage(null);
            return;
        }
        var player = event.getPlayer();
        var userOptional = ServerSystem.Instance.getRegistry().getService(UserManager.class).getUser(player);
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

        var message = MessageManager.formatMessage(_message, user, null, null, false);
        event.setJoinMessage(message);
    }
}
