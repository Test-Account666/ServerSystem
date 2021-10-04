package me.Entity303.ServerSystem.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TpaData {
    private final boolean tpahere;
    private final OfflinePlayer sender;
    private final Long end;

    public TpaData(boolean tpahere, Player sender, Long end) {
        this.tpahere = tpahere;
        this.sender = sender;
        this.end = end;
    }

    public boolean isTpahere() {
        return this.tpahere;
    }

    public OfflinePlayer getSender() {
        return this.sender;
    }

    public Long getEnd() {
        return this.end;
    }
}
