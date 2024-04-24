package me.entity303.serversystem.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings({ "NewMethodNamingConvention", "FieldNamingConvention" })
public class AsyncMuteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender _sender;
    private final OfflinePlayer _target;
    private final String _reason;
    private final String _unmuteDate;

    public AsyncMuteEvent(CommandSender sender, OfflinePlayer target, String reason, String unmuteDate) {
        super(true);
        this._sender = sender;
        this._target = target;
        this._reason = reason;
        this._unmuteDate = unmuteDate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getUnmuteDate() {
        return this._unmuteDate;
    }

    public String getReason() {
        return this._reason;
    }

    public CommandSender getSender() {
        return this._sender;
    }

    public OfflinePlayer getBannedPlayer() {
        return this._target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
