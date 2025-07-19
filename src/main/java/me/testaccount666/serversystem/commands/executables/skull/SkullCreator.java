package me.testaccount666.serversystem.commands.executables.skull;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class SkullCreator {

    public ItemStack getSkull(UUID uuid) {
        var playerProfile = Bukkit.createProfile(uuid);

        return getSkullByPlayerProfile(playerProfile);
    }

    public ItemStack getSkull(String name) {
        var playerProfile = Bukkit.createProfile(name);

        return getSkullByPlayerProfile(playerProfile);
    }

    public ItemStack getSkull(OfflinePlayer offlinePlayer) {
        var playerProfile = offlinePlayer.getPlayerProfile();

        return getSkullByPlayerProfile(playerProfile);
    }

    public ItemStack getSkullByTexture(String base64) {
        try {
            return getSkullByTexture(URI.create(new String(Base64.getDecoder().decode(base64))).toURL());
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public ItemStack getSkullByTexture(URL textureURL) {
        var playerProfile = Bukkit.getServer().createProfile(UUID.randomUUID(), "NiceSkull");

        var playerTextures = playerProfile.getTextures();

        playerTextures.setSkin(textureURL, PlayerTextures.SkinModel.CLASSIC);
        playerProfile.setTextures(playerTextures);

        return getSkullByPlayerProfile(playerProfile);
    }

    private ItemStack getSkullByPlayerProfile(PlayerProfile playerProfile) {
        var skullItem = new ItemStack(Material.PLAYER_HEAD);

        var skullMeta = (SkullMeta) skullItem.getItemMeta();

        assert skullMeta != null;
        skullMeta.setPlayerProfile(playerProfile);

        skullItem.setItemMeta(skullMeta);

        return skullItem;
    }
}
