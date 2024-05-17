package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {
    protected final ServerSystem _plugin;
    private final boolean _resetGameMode;
    private final boolean _resetGodMode;
    private final boolean _resetFly;

    public WorldChangeListener(ServerSystem plugin, boolean resetGameMode, boolean resetGodMode, boolean resetFly) {
        this._plugin = plugin;
        this._resetGameMode = resetGameMode;
        this._resetGodMode = resetGodMode;
        this._resetFly = resetFly;
    }

    @EventHandler
    public void OnWorldChange(PlayerChangedWorldEvent event) {
        if (this._resetGameMode)
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "worldchange.bypassreset.gamemode", true))
                event.getPlayer().setGameMode(Bukkit.getDefaultGameMode());
        if (this._resetGodMode)
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "worldchange.bypassreset.god", true))
                this._plugin.GetGodList().remove(event.getPlayer());
        if (this._resetFly)
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "worldchange.bypassreset.fly", true)) {
                event.getPlayer().setFlying(false);
                event.getPlayer().setAllowFlight(false);
            }

        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "vanish.see", true))
                for (var all : Bukkit.getOnlinePlayers())
                    if (this._plugin.GetVanish().IsVanish(all))
                        event.getPlayer().hidePlayer(all);
        }, 1L);
    }
}
