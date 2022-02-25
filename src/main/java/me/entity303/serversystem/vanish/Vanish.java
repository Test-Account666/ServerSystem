package me.entity303.serversystem.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vanish {
    private final ServerSystem plugin;
    private final List<UUID> vanishList = new ArrayList<>();
    private final List<Player> allowInteract = new ArrayList<>();
    private final List<Player> allowChat = new ArrayList<>();
    private final List<Player> allowDrop = new ArrayList<>();
    private final List<Player> allowPickup = new ArrayList<>();

    public Vanish(ServerSystem plugin) {
        this.plugin = plugin;
    }

    public List<Player> getAllowInteract() {
        if (!this.plugin.getCommandManager().isInteractActive()) return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this.allowInteract;
    }

    public List<Player> getAllowChat() {
        if (!this.plugin.getCommandManager().isChatActive()) return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this.allowChat;
    }

    public List<Player> getAllowDrop() {
        if (!this.plugin.getCommandManager().isDropActive()) return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this.allowDrop;
    }

    public List<Player> getAllowPickup() {
        if (!this.plugin.getCommandManager().isPickupActive()) return new ArrayList<>(Bukkit.getOnlinePlayers());
        return this.allowPickup;
    }

    public List<UUID> getVanishList() {
        return this.vanishList;
    }

    public Boolean isVanish(Player player) {
        if (player == null) this.plugin.error("Player cannot be null!");
        player.getMetadata("Vanish");
        return player.getMetadata("Vanish").stream().anyMatch(metadataValue -> metadataValue.asBoolean() || this.vanishList.contains(player.getUniqueId()));
    }

    public Boolean isVanish(OfflinePlayer player) {
        return player.isOnline() ? this.isVanish(player.getPlayer()) : Boolean.valueOf(this.vanishList.contains(player.getUniqueId()));
    }

    public void setVanishData(Player player, Boolean vanish) {
        if (vanish) {
            if (!this.vanishList.contains(player.getUniqueId()))
                this.vanishList.add(player.getUniqueId());
        } else
            this.vanishList.remove(player.getUniqueId());

        player.setMetadata("Vanish", this.plugin.getMetaValue().getMetaValue(vanish));
    }

    public void setVanish(Boolean vanish, UUID uuid) {
        if (vanish)
            this.vanishList.add(uuid);
        else
            this.vanishList.remove(uuid);
    }

    public void setVanish(boolean vanish, Player vanishPlayer) {
        vanishPlayer.setSleepingIgnored(vanish);
        this.plugin.getVersionStuff().getVanishPacket().setVanish(vanishPlayer, vanish);
    }
}
