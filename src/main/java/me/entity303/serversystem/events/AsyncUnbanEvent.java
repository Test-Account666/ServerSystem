package me.entity303.serversystem.events;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncUnbanEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final OfflinePlayer target;
    private final String reason;

    public AsyncUnbanEvent(CommandSender sender, OfflinePlayer target) {
        this(sender, target, ServerSystem.getPlugin(ServerSystem.class).getMessages().getMessageWithStringTarget("unban", "unban", sender, target.getName(), "Ban.DefaultReason"));
    }

    public AsyncUnbanEvent(CommandSender sender, OfflinePlayer target, String reason) {
        super(true);
        this.sender = sender;
        this.target = target;
        this.reason = reason;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public OfflinePlayer getBannedPlayer() {
        return this.target;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
