package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    protected final ServerSystem _plugin;

    public ServerPingListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnPing(ServerListPingEvent event) {
        if (event == null)
            return;
        if (!(event instanceof Iterable))
            return;
        try {
            event.iterator();
            if (!event.iterator().hasNext())
                return;
            for (var playerIterator = event.iterator(); playerIterator.hasNext(); ) {
                var player = playerIterator.next();
                if (this._plugin.GetVanish().IsVanish(player))
                    playerIterator.remove();
            }
        } catch (UnsupportedOperationException ignored) {
        }
    }
}
