package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class AwayFromKeyboardListener implements Listener {
    private final ServerSystem plugin;
    private final HashMap<UUID, Long> awayFromKeyboardMap = new HashMap<>();


    public AwayFromKeyboardListener(ServerSystem plugin, long maxDuration, long kickDuration) {
        this.plugin = plugin;

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.plugin, () -> {
            for (Map.Entry<UUID, Long> entry : new HashSet<>(this.awayFromKeyboardMap.entrySet())) {
                UUID uuid = entry.getKey();
                long duration = System.currentTimeMillis() - entry.getValue();

                if (duration < maxDuration)
                    continue;

                Player player = Bukkit.getPlayer(uuid);

                if (player == null) {
                    this.awayFromKeyboardMap.remove(uuid);
                    continue;
                }

                if (this.isAwayFromKeyboard(player)) {

                    if (kickDuration <= 0)
                        continue;

                    if (duration < kickDuration)
                        continue;

                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        this.awayFromKeyboardMap.remove(uuid);
                        player.kickPlayer(this.plugin.getMessages().getMessage("afk", "afk", player, null, "Afk.Kick"));
                    });
                    continue;
                }

                player.removeMetadata("afk", this.plugin);
                player.setMetadata("afk", this.plugin.getMetaValue().getMetaValue(true));

                player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("afk", "afk", player, null, "Afk.Enabled"));
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.getPlayer().removeMetadata("afk", this.plugin);
        this.awayFromKeyboardMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().removeMetadata("afk", this.plugin);
        e.getPlayer().setMetadata("afk", this.plugin.getMetaValue().getMetaValue(false));

        long currentTime = System.currentTimeMillis();
        this.awayFromKeyboardMap.put(e.getPlayer().getUniqueId(), currentTime);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getYaw() == e.getTo().getYaw())
            if (e.getFrom().getPitch() == e.getTo().getPitch())
                return;

        long currentTime = System.currentTimeMillis();

        this.awayFromKeyboardMap.put(e.getPlayer().getUniqueId(), currentTime);

        boolean awayFromKeyboard = this.isAwayFromKeyboard(e.getPlayer());

        if (!awayFromKeyboard)
            return;

        e.getPlayer().removeMetadata("afk", this.plugin);
        e.getPlayer().setMetadata("afk", this.plugin.getMetaValue().getMetaValue(false));

        e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("afk", "afk", e.getPlayer(), null, "Afk.Disabled"));
    }

    private boolean isAwayFromKeyboard(Player player) {
        boolean awayFromKeyboard = false;

        for (MetadataValue metadataValue : player.getMetadata("afk")) {
            if (metadataValue == null)
                continue;

            if (metadataValue.getOwningPlugin() == null)
                continue;

            if (!metadataValue.getOwningPlugin().getName().equalsIgnoreCase("ServerSystem"))
                continue;

            awayFromKeyboard = metadataValue.asBoolean();
            break;
        }

        return awayFromKeyboard;
    }
}
