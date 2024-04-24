package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener extends CommandUtils implements Listener {

    public ServerPingListener(ServerSystem plugin) {
        super(plugin);
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
