package me.entity303.serversystem.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TpaData {
    private final boolean _tpahere;
    private final OfflinePlayer _sender;
    private final Long _end;

    public TpaData(boolean tpahere, Player sender, Long end) {
        this._tpahere = tpahere;
        this._sender = sender;
        this._end = end;
    }

    public boolean IsTpahere() {
        return this._tpahere;
    }

    public OfflinePlayer GetSender() {
        return this._sender;
    }

    public Long GetEnd() {
        return this._end;
    }
}
