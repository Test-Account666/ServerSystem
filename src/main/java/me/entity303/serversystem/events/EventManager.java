package me.entity303.serversystem.events;

import me.entity303.serversystem.listener.*;
import me.entity303.serversystem.listener.chat.ChatListenerWithPrefix;
import me.entity303.serversystem.listener.command.CommandListener;
import me.entity303.serversystem.listener.join.JoinListener;
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
        if (!this.listeners.isEmpty())
            this.listeners.forEach(HandlerList::unregisterAll);

        this.listeners.clear();

        this.registerEvent(new BackListener(this.serverSystem));
        this.registerEvent(new LoginListener(this.serverSystem));
        this.registerEvent(new MoveListener(this.serverSystem));
        this.registerEvent(new JoinListener(this.serverSystem));
        this.registerEvent(new QuitListener(this.serverSystem));
        this.registerEvent(new VanishListener(this.serverSystem));
        this.registerEvent(new KillListener(this.serverSystem));
        this.registerEvent(new CommandListener(this.serverSystem));
        this.registerEvent(new SignListener(this.serverSystem));
        this.registerEvent(new GodListener(this.serverSystem));

        this.registerEvent(new ChatListenerWithPrefix(this.serverSystem, this.serverSystem.getConfigReader().getBoolean("chat.active"),
                                                      this.serverSystem.getConfigReader().getString("chat.format")));


        this.registerEvent(new InventoryClickListener(this.serverSystem));

        this.registerEvent(new SomeVanishListener(this.serverSystem));
        this.registerEvent(new GameModeChangeListener(this.serverSystem));
        this.registerEvent(new ServerPingListener(this.serverSystem));

        var resetGameMode = this.serverSystem.getConfigReader().getBoolean("worldChange.resetGameMode");
        var resetGodMode = this.serverSystem.getConfigReader().getBoolean("worldChange.resetGod");
        var resetFly = this.serverSystem.getConfigReader().getBoolean("worldChange.resetFly");

        if (resetGameMode || resetGodMode || resetFly)
            this.registerEvent(new WorldChangeListener(this.serverSystem, resetGameMode, resetGodMode, resetFly));

        if (this.serverSystem.getConfigReader().getBoolean("deactivateEntityCollision"))
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.registerEvent(new EntitySpawnListener(this.serverSystem)), 5L);

        if (this.serverSystem.getConfigReader().getBoolean("no-redstone"))
            this.registerEvent(new RedstoneListener(this.serverSystem));

        if (this.serverSystem.getConfigReader().getBoolean("spawn.respawn"))
            this.registerEvent(new RespawnListener(this.serverSystem));

        this.registerEvent(new UnlimitedListener());
        this.registerEvent(new FreezeListener(this.serverSystem));

        if (this.serverSystem.getConfigReader().getBoolean("afk.enabled")) {
            var maxDuration = this.serverSystem.getConfigReader().getLong("afk.maxDuration");
            maxDuration = TimeUnit.SECONDS.toMillis(maxDuration);

            var kickDuration = this.serverSystem.getConfigReader().getLong("afk.kickDuration");
            kickDuration = TimeUnit.SECONDS.toMillis(kickDuration);
            kickDuration += maxDuration;

            this.registerEvent(new AwayFromKeyboardListener(this.serverSystem, maxDuration, kickDuration));
        }
    }

    public void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.serverSystem);
        this.listeners.add(listener);
    }
}
