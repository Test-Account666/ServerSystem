package me.entity303.serversystem.events;

import me.entity303.serversystem.listener.*;
import me.entity303.serversystem.listener.chat.ChatListenerWithPrefix;
import me.entity303.serversystem.listener.chat.ChatListenerWithoutPrefix;
import me.entity303.serversystem.listener.command.CommandListener;
import me.entity303.serversystem.listener.join.JoinListener;
import me.entity303.serversystem.listener.AwayFromKeyboardListener;
import me.entity303.serversystem.listener.move.MoveListener;
import me.entity303.serversystem.listener.move.freeze.FreezeListener;
import me.entity303.serversystem.listener.vanish.*;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventManager {
    private final ServerSystem serverSystem;
    private final List<Listener> listeners = new ArrayList<>();

    public EventManager(ServerSystem serverSystem) {
        this.serverSystem = serverSystem;
    }

    public void registerEvents() {
        if (this.listeners.size() >= 1) this.listeners.forEach(HandlerList::unregisterAll);
        this.listeners.clear();

        this.re(new BackListener(this.serverSystem));
        this.re(new LoginListener(this.serverSystem));
        this.re(new MoveListener(this.serverSystem));
        this.re(new JoinListener(this.serverSystem));
        this.re(new QuitListener(this.serverSystem));
        this.re(new VanishListener(this.serverSystem));
        this.re(new KillListener(this.serverSystem));
        this.re(new CommandListener(this.serverSystem));
        this.re(new SignListener(this.serverSystem));
        this.re(new GodListener(this.serverSystem));

        if (this.serverSystem.getConfigReader().getBoolean("chat.active"))
            this.re(new ChatListenerWithPrefix(this.serverSystem, this.serverSystem.getConfigReader().getString("chat.format")));
        else
            this.re(new ChatListenerWithoutPrefix(this.serverSystem));

        this.re(new InventoryClickListener(this.serverSystem));

        this.re(new SomeVanishListener(this.serverSystem));
        this.re(new GameModeChangeListener(this.serverSystem));
        this.re(new ServerPingListener(this.serverSystem));

        boolean resetGameMode = this.serverSystem.getConfigReader().getBoolean("worldChange.resetGameMode");
        boolean resetGodMode = this.serverSystem.getConfigReader().getBoolean("worldChange.resetGod");
        boolean resetFly = this.serverSystem.getConfigReader().getBoolean("worldChange.resetFly");

        if (resetGameMode || resetGodMode || resetFly)
            this.re(new WorldChangeListener(this.serverSystem, resetGameMode, resetGodMode, resetFly));

        if (this.serverSystem.getConfigReader().getBoolean("deactivateEntityCollision"))
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.re(new EntitySpawnListener(this.serverSystem)), 5L);

        if (this.serverSystem.getConfigReader().getBoolean("no-redstone"))
            this.re(new RedstoneListener(this.serverSystem));

        if (this.serverSystem.getConfigReader().getBoolean("spawn.respawn"))
            this.re(new RespawnListener(this.serverSystem));

        this.re(new UnlimitedListener());
        this.re(new FreezeListener(this.serverSystem));

        if (this.serverSystem.getConfigReader().getBoolean("afk.enabled")) {
            long maxDuration = this.serverSystem.getConfigReader().getLong("afk.maxDuration");
            maxDuration = TimeUnit.SECONDS.toMillis(maxDuration);

            long kickDuration = this.serverSystem.getConfigReader().getLong("afk.kickDuration");
            kickDuration = TimeUnit.SECONDS.toMillis(kickDuration);
            kickDuration += maxDuration;

            this.re(new AwayFromKeyboardListener(this.serverSystem, maxDuration, kickDuration));
        }
    }

    public void re(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.serverSystem);
        this.listeners.add(listener);
    }
}
