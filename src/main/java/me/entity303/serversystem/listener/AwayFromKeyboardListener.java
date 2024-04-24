package me.entity303.serversystem.listener;

import me.entity303.serversystem.commands.executable.AwayFromKeyboardCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class AwayFromKeyboardListener implements Listener {
    public static final String AFK_ENABLED = "Afk.Enabled";
    public static final String AFK_DISABLED = "Afk.Disabled";
    private final ServerSystem _plugin;
    private final HashMap<String, Long> _awayFromKeyboardMap = new HashMap<>();
    private final long _maxDuration;


    public AwayFromKeyboardListener(ServerSystem plugin, long maxDuration, long kickDuration) {
        this._plugin = plugin;
        this._maxDuration = maxDuration;

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this._plugin, () -> {
            var entrySet = this._awayFromKeyboardMap.entrySet();
            for (var entry : new HashSet<>(entrySet)) {
                var entryKey = entry.getKey();
                var uuid = UUID.fromString(entryKey);
                var duration = System.currentTimeMillis() - entry.getValue();

                if (duration < maxDuration)
                    continue;

                var player = Bukkit.getPlayer(uuid);

                var uuidString = uuid.toString();
                if (player == null) {
                    this._awayFromKeyboardMap.remove(uuidString);
                    continue;
                }

                var pluginMessages = this._plugin.GetMessages();
                if (this.IsAwayFromKeyboard(player)) {

                    if (kickDuration <= 0)
                        continue;

                    if (duration < kickDuration)
                        continue;

                    Bukkit.getScheduler().runTask(this._plugin, () -> {
                        this._awayFromKeyboardMap.remove(uuidString);
                        var message = pluginMessages.GetMessage("afk", "afk", player, null, "Afk.Kick");
                        player.kickPlayer(message);
                    });
                    continue;
                }

                player.removeMetadata("afk", this._plugin);
                var metaValueGenerator = this._plugin.GetMetaValue();
                var metaValue = metaValueGenerator.GetMetaValue(true);
                player.setMetadata("afk", metaValue);

                var prefix = pluginMessages.GetPrefix();
                var message = pluginMessages.GetMessage("afk", "afk", player, null, AFK_ENABLED);
                player.sendMessage(prefix + message);
            }
        }, 20L, 20L);


        for (var all : Bukkit.getOnlinePlayers())
            this.OnJoin(new PlayerJoinEvent(all, ""));
    }

    private boolean IsAwayFromKeyboard(Player player) {
        var awayFromKeyboard = false;

        awayFromKeyboard = AwayFromKeyboardCommand.IsAwayFromKeyboard(player);

        var uniqueId = player.getUniqueId();
        var uuidString = uniqueId.toString();
        if (this._awayFromKeyboardMap.containsKey(uuidString)) {
            var duration = System.currentTimeMillis() - this._awayFromKeyboardMap.get(uuidString);

            return awayFromKeyboard && duration > this._maxDuration;
        }

        return awayFromKeyboard;
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        event.getPlayer().removeMetadata("afk", this._plugin);
        var metaValue = this._plugin.GetMetaValue().GetMetaValue(false);
        event.getPlayer().setMetadata("afk", metaValue);

        var currentTime = System.currentTimeMillis();
        var uuidString = event.getPlayer().getUniqueId().toString();
        this._awayFromKeyboardMap.put(uuidString, currentTime);
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("afk", this._plugin);
        var uuidString = event.getPlayer().getUniqueId().toString();
        this._awayFromKeyboardMap.remove(uuidString);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnMove(PlayerMoveEvent event) {
        var player = event.getPlayer();
        if (player.getLocation().getYaw() == event.getTo().getYaw())
            if (player.getLocation().getPitch() == event.getTo().getPitch())
                return;

        var awayFromKeyboard = this.IsAwayFromKeyboard(player);

        var currentTime = System.currentTimeMillis();

        var uuidString = player.getUniqueId().toString();
        this._awayFromKeyboardMap.put(uuidString, currentTime);

        if (!awayFromKeyboard)
            return;

        player.removeMetadata("afk", this._plugin);
        var metaValue = this._plugin.GetMetaValue().GetMetaValue(false);
        player.setMetadata("afk", metaValue);

        var pluginMessages = this._plugin.GetMessages();
        var prefix = pluginMessages.GetPrefix();
        var message = pluginMessages.GetMessage("afk", "afk", player, null, AFK_DISABLED);
        player.sendMessage(prefix + message);
    }
}
