package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final ServerSystem _plugin;
    private final String _resourceId;

    public UpdateChecker(ServerSystem plugin, String resourceId) {
        this._plugin = plugin;
        this._resourceId = resourceId;
    }

    public void GetVersion(final Consumer<? super String> consumer) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, () -> {
            try (var inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this._resourceId).openStream();
                 var scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) consumer.accept(scanner.next());
            } catch (IOException exception) {
                this._plugin.Error("Cannot look for updates: " + exception.getMessage());
            }
        }, 3L * 20L);
    }
}
