package me.testaccount666.migration.essentials;

import com.earth2me.essentials.Essentials;
import lombok.SneakyThrows;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.nio.file.Path;
import java.util.UUID;

public abstract class AbstractMigrator {

    protected Essentials essentials() {
        return Essentials.getPlugin(Essentials.class);
    }

    protected UserManager userManager() {
        return ServerSystem.Instance.getRegistry().getService(UserManager.class);
    }

    protected OfflinePlayer[] offlinePlayers() {
        return Bukkit.getOfflinePlayers();
    }

    @SneakyThrows
    protected void ensureUserDataExists(UUID uuid) {
        var userDataDirectory = Path.of("plugins", "Essentials", "userdata");
        if (!userDataDirectory.toFile().exists()) userDataDirectory.toFile().mkdirs();

        var userFile = userDataDirectory.resolve("${uuid}.yml").toFile();
        if (!userFile.exists()) userFile.createNewFile();
    }

    public abstract int migrateFrom();

    public abstract int migrateTo();
}
