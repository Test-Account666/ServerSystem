package me.Entity303.ServerSystem.Listener.Vanish;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeChangeListener extends MessageUtils implements Listener {

    public GameModeChangeListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        if (this.plugin.getVanish().isVanish(e.getPlayer()))
            this.plugin.getVersionStuff().getVanishPacket().setVanish(e.getPlayer(), true);
    }
}
