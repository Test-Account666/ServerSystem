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
    private final FileConfiguration _configuration;
    private final File _wantsTeleportFile = new File("plugins" + File.separator + "ServerSystem", "wantstp.yml");

    public WantsTeleport(ServerSystem plugin) {
        this._configuration = YamlConfiguration.loadConfiguration(this._wantsTeleportFile);
    }

    public boolean DoesPlayerWantTeleport(Player player) {
        return this.DoesPlayerWantTeleport(player.getUniqueId());
    }

    public boolean DoesPlayerWantTeleport(UUID uuid) {
        if (!this._wantsTeleportFile.exists())
            return true;

        if (!this._configuration.isSet("WantsTp." + uuid.toString()))
            return true;

        return this._configuration.getBoolean("WantsTp." + uuid);
    }

    public boolean DoesPlayerWantTeleport(OfflinePlayer offlinePlayer) {
        return this.DoesPlayerWantTeleport(offlinePlayer.getUniqueId());
    }

    public void SetWantsTeleport(Player player, Boolean wants) {
        this.SetWantsTeleport(player.getUniqueId(), wants);
    }

    public void SetWantsTeleport(UUID uuid, Boolean wants) {
        this._configuration.set("WantsTp." + uuid, wants);

        try {
            this._configuration.save(this._wantsTeleportFile);

            this._configuration.load(this._wantsTeleportFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void SetWantsTeleport(OfflinePlayer offlinePlayer, Boolean wants) {
        this.SetWantsTeleport(offlinePlayer.getUniqueId(), wants);
    }
}
