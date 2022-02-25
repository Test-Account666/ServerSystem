package me.entity303.serversystem.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncBanEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final OfflinePlayer target;
    private final String reason;
    private final String unbanDate;

    public AsyncBanEvent(CommandSender sender, OfflinePlayer target, String reason, String unbanDate) {
        super(true);
        this.sender = sender;
        this.target = target;
        this.reason = reason;
        this.unbanDate = unbanDate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getUnbanDate() {
        return unbanDate;
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
