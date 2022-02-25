package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;

public class ServerPingListener extends MessageUtils implements Listener {

    public ServerPingListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        if (e == null) return;
        if (!(e instanceof Iterable)) return;
        try {
            e.iterator();
            if (!e.iterator().hasNext()) return;
            for (Iterator<Player> it = e.iterator(); it.hasNext(); ) {
                Player player = it.next();
                if (this.plugin.getVanish().isVanish(player)) it.remove();
            }
        } catch (UnsupportedOperationException ignored) {
        }
    }
}
