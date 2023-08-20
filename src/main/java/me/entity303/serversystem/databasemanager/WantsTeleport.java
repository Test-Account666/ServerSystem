package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import me.entity303.serversystem.utils.LegacyWantsTP;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

public class WantsTeleport {
    private final FileConfiguration cfg;
    private final File wantsTpFile = new File("plugins" + File.separator + "ServerSystem", "wantstp.yml");

    public WantsTeleport(ServerSystem plugin) {
        this.cfg = YamlConfiguration.loadConfiguration(this.wantsTpFile);

        File legacyWantsTeleportFile = new File("plugins" + File.separator + "ServerSystem", "wantstp.h2.mv.db");

        if (!legacyWantsTeleportFile.exists())
            return;

        plugin.log("Found legacy WantsTp database!");
        plugin.log("Trying to convert...");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            FileUtils.copyFile(legacyWantsTeleportFile, new File("plugins" + File.separator + "ServerSystem", legacyWantsTeleportFile.getName() + ".backup-" + date));
        } catch (IOException e) {
            e.printStackTrace();

            plugin.error("Failed to convert legacy WantsTp database!");
            return;
        }

        try {
            LegacyWantsTP legacyWantsTP = new LegacyWantsTP(plugin);

            for (Map.Entry<UUID, Boolean> wantsTpEntry : legacyWantsTP.listWantsTp().entrySet())
                this.setWantsTeleport(wantsTpEntry.getKey(), wantsTpEntry.getValue());

            legacyWantsTP.close();

            legacyWantsTeleportFile.delete();
        } catch (Throwable throwable) {
            throwable.printStackTrace();

            plugin.error("Failed to convert legacy WantsTp database!");
            return;
        }

        plugin.log("Legacy WantsTp database was successfully converted!");
    }

    public boolean wantsTeleport(Player player) {
        return this.wantsTeleport(player.getUniqueId());
    }

    public boolean wantsTeleport(OfflinePlayer offlinePlayer) {
        return this.wantsTeleport(offlinePlayer.getUniqueId());
    }

    public boolean wantsTeleport(UUID uuid) {
        if (!this.wantsTpFile.exists())
            return true;

        if (!this.cfg.isSet("WantsTp." + uuid.toString()))
            return true;

        return this.cfg.getBoolean("WantsTp." + uuid.toString());
    }

    public void setWantsTeleport(Player player, Boolean wants) {
        this.setWantsTeleport(player.getUniqueId(), wants);
    }

    public void setWantsTeleport(OfflinePlayer offlinePlayer, Boolean wants) {
        this.setWantsTeleport(offlinePlayer.getUniqueId(), wants);
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
}
