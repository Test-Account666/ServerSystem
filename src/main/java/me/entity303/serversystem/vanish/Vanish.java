package me.entity303.serversystem.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vanish {
    private final ServerSystem _plugin;
    private final List<UUID> _vanishList = new ArrayList<>();
    private final List<Player> _allowInteract = new ArrayList<>();
    private final List<Player> _allowChat = new ArrayList<>();
    private final List<Player> _allowDrop = new ArrayList<>();
    private final List<Player> _allowPickup = new ArrayList<>();

    public Vanish(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public List<Player> GetAllowInteract() {
        if (!this._plugin.GetCommandManager().IsInteractActive())
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this._allowInteract;
    }

    public List<Player> GetAllowChat() {
        if (!this._plugin.GetCommandManager().IsChatActive())
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this._allowChat;
    }

    public List<Player> GetAllowDrop() {
        if (!this._plugin.GetCommandManager().IsDropActive())
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this._allowDrop;
    }

    public List<Player> GetAllowPickup() {
        if (!this._plugin.GetCommandManager().IsPickupActive())
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this._allowPickup;
    }

    public List<UUID> GetVanishList() {
        return this._vanishList;
    }

    public Boolean IsVanish(OfflinePlayer player) {
        return player.isOnline()? this.IsVanish(player.getPlayer()) : Boolean.valueOf(this._vanishList.contains(player.getUniqueId()));
    }

    public Boolean IsVanish(Player player) {
        if (player == null)
            this._plugin.Error("Player cannot be null!");
        player.getMetadata("Vanish");
        return player.getMetadata("Vanish").stream().anyMatch(metadataValue -> metadataValue.asBoolean() || this._vanishList.contains(player.getUniqueId()));
    }

    public void SetVanishData(Player player, Boolean vanish) {
        if (!vanish)
            this._vanishList.remove(player.getUniqueId());
        else if (!this._vanishList.contains(player.getUniqueId()))
            this._vanishList.add(player.getUniqueId());

        player.setMetadata("Vanish", this._plugin.GetMetaValue().GetMetaValue(vanish));
        player.setMetadata("vanished", this._plugin.GetMetaValue().GetMetaValue(vanish));
    }

    public void SetVanish(Boolean vanish, UUID uuid) {
        if (vanish)
            this._vanishList.add(uuid);
        else
            this._vanishList.remove(uuid);
    }

    public void SetVanish(boolean vanish, Player vanishPlayer) {
        vanishPlayer.setSleepingIgnored(vanish);
        this._plugin.GetVersionStuff().GetVanishPacket().SetVanish(vanishPlayer, vanish);
    }
}
