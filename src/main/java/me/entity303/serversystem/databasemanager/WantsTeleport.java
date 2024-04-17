package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class WantsTeleport {
    private final FileConfiguration cfg;
    private final File wantsTpFile = new File("plugins" + File.separator + "ServerSystem", "wantstp.yml");

    public WantsTeleport(ServerSystem plugin) {
        this.cfg = YamlConfiguration.loadConfiguration(this.wantsTpFile);
    }

    public boolean wantsTeleport(Player player) {
        return this.wantsTeleport(player.getUniqueId());
    }

    public boolean wantsTeleport(UUID uuid) {
        if (!this.wantsTpFile.exists())
            return true;

        if (!this.cfg.isSet("WantsTp." + uuid.toString()))
            return true;

        return this.cfg.getBoolean("WantsTp." + uuid);
    }

    public boolean wantsTeleport(OfflinePlayer offlinePlayer) {
        return this.wantsTeleport(offlinePlayer.getUniqueId());
    }

    public void setWantsTeleport(Player player, Boolean wants) {
        this.setWantsTeleport(player.getUniqueId(), wants);
    }

    public void setWantsTeleport(UUID uuid, Boolean wants) {
        this.cfg.set("WantsTp." + uuid, wants);

        try {
            this.cfg.save(this.wantsTpFile);

            this.cfg.load(this.wantsTpFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setWantsTeleport(OfflinePlayer offlinePlayer, Boolean wants) {
        this.setWantsTeleport(offlinePlayer.getUniqueId(), wants);
    }
}
