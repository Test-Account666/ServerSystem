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
    private final ServerSystem _serverSystem;
    private final List<Listener> _listeners = new ArrayList<>();

    public EventManager(ServerSystem serverSystem) {
        this._serverSystem = serverSystem;
    }

    public void RegisterEvents() {
        if (!this._listeners.isEmpty())
            this._listeners.forEach(HandlerList::unregisterAll);

        this._listeners.clear();

        this.RegisterEvent(new BackListener(this._serverSystem));
        this.RegisterEvent(new LoginListener(this._serverSystem));
        this.RegisterEvent(new MoveListener(this._serverSystem));
        this.RegisterEvent(new JoinListener(this._serverSystem));
        this.RegisterEvent(new QuitListener(this._serverSystem));
        this.RegisterEvent(new VanishListener(this._serverSystem));
        this.RegisterEvent(new KillListener(this._serverSystem));
        this.RegisterEvent(new CommandListener(this._serverSystem));
        this.RegisterEvent(new SignListener(this._serverSystem));
        this.RegisterEvent(new GodListener(this._serverSystem));

        this.RegisterEvent(new ChatListenerWithPrefix(this._serverSystem, this._serverSystem.GetConfigReader().GetBoolean("chat.active"),
                                                      this._serverSystem.GetConfigReader().GetString("chat.format")));


        this.RegisterEvent(new InventoryClickListener(this._serverSystem));

        this.RegisterEvent(new SomeVanishListener(this._serverSystem));
        this.RegisterEvent(new GameModeChangeListener(this._serverSystem));
        this.RegisterEvent(new ServerPingListener(this._serverSystem));

        var resetGameMode = this._serverSystem.GetConfigReader().GetBoolean("worldChange.resetGameMode");
        var resetGodMode = this._serverSystem.GetConfigReader().GetBoolean("worldChange.resetGod");
        var resetFly = this._serverSystem.GetConfigReader().GetBoolean("worldChange.resetFly");

        if (resetGameMode || resetGodMode || resetFly)
            this.RegisterEvent(new WorldChangeListener(this._serverSystem, resetGameMode, resetGodMode, resetFly));

        if (this._serverSystem.GetConfigReader().GetBoolean("deactivateEntityCollision"))
            Bukkit.getScheduler().runTaskLater(this._serverSystem, () -> this.RegisterEvent(new EntitySpawnListener(this._serverSystem)), 5L);

        if (this._serverSystem.GetConfigReader().GetBoolean("no-redstone"))
            this.RegisterEvent(new RedstoneListener(this._serverSystem));

        if (this._serverSystem.GetConfigReader().GetBoolean("spawn.respawn"))
            this.RegisterEvent(new RespawnListener(this._serverSystem));

        this.RegisterEvent(new UnlimitedListener());
        this.RegisterEvent(new FreezeListener(this._serverSystem));

        if (this._serverSystem.GetConfigReader().GetBoolean("afk.enabled")) {
            var maxDuration = this._serverSystem.GetConfigReader().GetLong("afk.maxDuration");
            maxDuration = TimeUnit.SECONDS.toMillis(maxDuration);

            var kickDuration = this._serverSystem.GetConfigReader().GetLong("afk.kickDuration");
            kickDuration = TimeUnit.SECONDS.toMillis(kickDuration);
            kickDuration += maxDuration;

            this.RegisterEvent(new AwayFromKeyboardListener(this._serverSystem, maxDuration, kickDuration));
        }
    }

    public void RegisterEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this._serverSystem);
        this._listeners.add(listener);
    }
}
