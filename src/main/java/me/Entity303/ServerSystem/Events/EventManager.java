package me.Entity303.ServerSystem.Events;

import me.Entity303.ServerSystem.Listener.*;
import me.Entity303.ServerSystem.Listener.Chat.ChatListenerWithPrefix;
import me.Entity303.ServerSystem.Listener.Chat.ChatListenerWithoutPrefix;
import me.Entity303.ServerSystem.Listener.Command.CommandListener;
import me.Entity303.ServerSystem.Listener.Join.JoinListener;
import me.Entity303.ServerSystem.Listener.Move.MoveListener;
import me.Entity303.ServerSystem.Listener.Vanish.*;
import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final ss serverSystem;
    private final List<Listener> listeners = new ArrayList<>();

    public EventManager(ss serverSystem) {
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

        if (this.serverSystem.getConfig().getBoolean("chat.active"))
            this.re(new ChatListenerWithPrefix(this.serverSystem, this.serverSystem.getConfig().getString("chat.format")));
        else
            this.re(new ChatListenerWithoutPrefix(this.serverSystem));

        this.re(new InventoryClickListener(this.serverSystem));

        this.re(new SomeVanishListener(this.serverSystem));
        this.re(new GameModeChangeListener(this.serverSystem));
        this.re(new ServerPingListener(this.serverSystem));

        boolean resetGameMode = this.serverSystem.getConfig().getBoolean("worldChange.resetgamemode");
        boolean resetGodMode = this.serverSystem.getConfig().getBoolean("worldChange.resetgod");
        boolean resetFly = this.serverSystem.getConfig().getBoolean("worldChange.resetfly");

        if (resetGameMode || resetGodMode || resetFly)
            this.re(new WorldChangeListener(this.serverSystem, resetGameMode, resetGodMode, resetFly));

        if (this.serverSystem.getConfig().getBoolean("deactivateentitycollision"))
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.re(new EntitySpawnListener(this.serverSystem));
            }, 5L);

        if (this.serverSystem.getConfig().getBoolean("no-redstone")) this.re(new RedstoneListener(this.serverSystem));

        if (this.serverSystem.getConfig().getBoolean("spawn.respawn"))
            this.re(new RespawnListener(this.serverSystem));
    }

    public void re(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.serverSystem);
        this.listeners.add(listener);
    }
}
