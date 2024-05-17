package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeChangeListener implements Listener {

    protected final ServerSystem _plugin;

    public GameModeChangeListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnGameModeChange(PlayerGameModeChangeEvent event) {
        if (this._plugin.GetVanish().IsVanish(event.getPlayer()))
            this._plugin.GetVersionStuff().GetVanishPacket().SetVanish(event.getPlayer(), true);
    }
}
