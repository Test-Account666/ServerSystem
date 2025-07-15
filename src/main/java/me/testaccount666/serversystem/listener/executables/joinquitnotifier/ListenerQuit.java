package me.testaccount666.serversystem.listener.executables.joinquitnotifier;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerQuit implements Listener {
    private final boolean _modifyMessage;
    private final boolean _sendMessage;
    private final String _message;
    private boolean _playSound;
    private Sound _sound;

    public ListenerQuit() {
        var config = ServerSystem.Instance.getConfigManager().getGeneralConfig();
        _modifyMessage = config.getBoolean("Quit.Message.Enabled");
        _sendMessage = config.getBoolean("Quit.Message.SendMessage");
        _message = config.getString("Quit.Message.Message", "");
        _playSound = config.getBoolean("Quit.Sound.Enabled");
        if (!_playSound) {
            _sound = null;
            return;
        }

        var soundString = config.getString("Quit.Sound.Sound", "");

        var isMinecraft = true;
        if (soundString.contains(":")) {
            var space = soundString.substring(0, soundString.indexOf(":"));
            soundString = soundString.substring(soundString.indexOf(":") + 1);
            isMinecraft = space.equalsIgnoreCase("minecraft");
        }

        var soundKey = isMinecraft? NamespacedKey.minecraft(soundString) : NamespacedKey.fromString(soundString);
        if (soundKey == null) {
            _playSound = false;
            Bukkit.getLogger().warning("Failed to parse sound '${soundString}' for quit message!");
            return;
        }

        _sound = Registry.SOUND_EVENT.get(soundKey);
        if (_sound == null) {
            _playSound = false;
            Bukkit.getLogger().warning("Failed to find sound '${soundString}' for quit message!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        handleMessage(event);
        playSound();
    }

    private void playSound() {
        if (!_playSound) return;

        Bukkit.getOnlinePlayers().forEach(everyone -> everyone.playSound(everyone, _sound, 1F, 1F));
    }

    private void handleMessage(PlayerQuitEvent event) {
        if (!_modifyMessage) return;

        if (!_sendMessage) {
            event.setQuitMessage(null);
            return;
        }
        var player = event.getPlayer();
        var userOptional = ServerSystem.Instance.getUserManager().getUser(player);
        if (userOptional.isEmpty()) {
            Bukkit.getLogger().warning("Couldn't cache User '${player.getName()}'! This should not happen!");
            return;
        }
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) {
            Bukkit.getLogger().warning("User '${player.getName()}' is cached as Offline User! This should not happen!");
            return;
        }
        var user = (User) cachedUser.getOfflineUser();

        var message = MessageManager.formatMessage(_message, user, null, null, false);
        event.setQuitMessage(message);
    }
}
