package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener extends CommandUtils implements Listener {
    private final boolean resetGameMode;
    private final boolean resetGodMode;
    private final boolean resetFly;

    public WorldChangeListener(ServerSystem plugin, boolean resetGameMode, boolean resetGodMode, boolean resetFly) {
        super(plugin);
        this.resetGameMode = resetGameMode;
        this.resetGodMode = resetGodMode;
        this.resetFly = resetFly;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (this.resetGameMode)
            if (!this.plugin.getPermissions().hasPermission(e.getPlayer(), "worldchange.bypassreset.gamemode", true))
                e.getPlayer().setGameMode(Bukkit.getDefaultGameMode());
        if (this.resetGodMode)
            if (!this.plugin.getPermissions().hasPermission(e.getPlayer(), "worldchange.bypassreset.god", true))
                this.plugin.getGodList().remove(e.getPlayer());
        if (this.resetFly)
            if (!this.plugin.getPermissions().hasPermission(e.getPlayer(), "worldchange.bypassreset.fly", true)) {
                e.getPlayer().setFlying(false);
                e.getPlayer().setAllowFlight(false);
            }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (!this.plugin.getPermissions().hasPermission(e.getPlayer(), "vanish.see", true))
                for (var all : Bukkit.getOnlinePlayers())
                    if (this.plugin.getVanish().isVanish(all))
                        e.getPlayer().hidePlayer(all);
        }, 1L);
    }
}
