package me.Entity303.ServerSystem.Events;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncMuteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final OfflinePlayer target;
    private final String reason;
    private final String unmuteDate;

    public AsyncMuteEvent(CommandSender sender, OfflinePlayer target, String reason, String unmuteDate) {
        super(true);
        this.sender = sender;
        this.target = target;
        this.reason = reason;
        this.unmuteDate = unmuteDate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getUnmuteDate() {
        return unmuteDate;
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
