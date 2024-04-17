package me.entity303.serversystem.events;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncUnmuteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final OfflinePlayer target;
    private final String reason;

    public AsyncUnmuteEvent(CommandSender sender, OfflinePlayer target) {
        this(sender, target, ServerSystem.getPlugin(ServerSystem.class)
                                         .getMessages()
                                         .getMessageWithStringTarget("unmute", "unmute", sender, target.getName(), "Mute.DefaultReason"));
    }

    public AsyncUnmuteEvent(CommandSender sender, OfflinePlayer target, String reason) {
        super(true);
        this.sender = sender;
        this.target = target;
        this.reason = reason;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getReason() {
        return reason;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public OfflinePlayer getBannedPlayer() {
        return this.target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
