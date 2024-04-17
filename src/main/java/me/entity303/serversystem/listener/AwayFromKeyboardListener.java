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
    private final ServerSystem plugin;
    private final HashMap<String, Long> awayFromKeyboardMap = new HashMap<>();
    private final long maxDuration;


    public AwayFromKeyboardListener(ServerSystem plugin, long maxDuration, long kickDuration) {
        this.plugin = plugin;
        this.maxDuration = maxDuration;

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.plugin, () -> {
            for (var entry : new HashSet<>(this.awayFromKeyboardMap.entrySet())) {
                var uuid = UUID.fromString(entry.getKey());
                var duration = System.currentTimeMillis() - entry.getValue();

                if (duration < maxDuration)
                    continue;

                var player = Bukkit.getPlayer(uuid);

                if (player == null) {
                    this.awayFromKeyboardMap.remove(uuid.toString());
                    continue;
                }

                if (this.isAwayFromKeyboard(player)) {

                    if (kickDuration <= 0)
                        continue;

                    if (duration < kickDuration)
                        continue;

                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        this.awayFromKeyboardMap.remove(uuid.toString());
                        player.kickPlayer(this.plugin.getMessages().getMessage("afk", "afk", player, null, "Afk.Kick"));
                    });
                    continue;
                }

                player.removeMetadata("afk", this.plugin);
                player.setMetadata("afk", this.plugin.getMetaValue().getMetaValue(true));

                player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("afk", "afk", player, null, "Afk.Enabled"));
            }
        }, 20L, 20L);


        for (var all : Bukkit.getOnlinePlayers())
            this.onJoin(new PlayerJoinEvent(all, ""));
    }

    private boolean isAwayFromKeyboard(Player player) {
        var awayFromKeyboard = false;

        awayFromKeyboard = AwayFromKeyboardCommand.isAwayFromKeyboard(player);

        if (this.awayFromKeyboardMap.containsKey(player.getUniqueId().toString())) {
            var duration = System.currentTimeMillis() - this.awayFromKeyboardMap.get(player.getUniqueId().toString());

            return awayFromKeyboard && duration > this.maxDuration;
        }

        return awayFromKeyboard;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().removeMetadata("afk", this.plugin);
        e.getPlayer().setMetadata("afk", this.plugin.getMetaValue().getMetaValue(false));

        var currentTime = System.currentTimeMillis();
        this.awayFromKeyboardMap.put(e.getPlayer().getUniqueId().toString(), currentTime);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.getPlayer().removeMetadata("afk", this.plugin);
        this.awayFromKeyboardMap.remove(e.getPlayer().getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getLocation().getYaw() == e.getTo().getYaw())
            if (e.getPlayer().getLocation().getPitch() == e.getTo().getPitch())
                return;

        var awayFromKeyboard = this.isAwayFromKeyboard(e.getPlayer());

        var currentTime = System.currentTimeMillis();

        this.awayFromKeyboardMap.put(e.getPlayer().getUniqueId().toString(), currentTime);

        if (!awayFromKeyboard)
            return;

        e.getPlayer().removeMetadata("afk", this.plugin);
        e.getPlayer().setMetadata("afk", this.plugin.getMetaValue().getMetaValue(false));

        e.getPlayer()
         .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("afk", "afk", e.getPlayer(), null, "Afk.Disabled"));
    }
}
