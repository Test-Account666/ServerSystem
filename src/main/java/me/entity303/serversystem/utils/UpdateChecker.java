package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final ServerSystem plugin;
    private final String resourceId;

    public UpdateChecker(ServerSystem plugin, String resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            try (var inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 var scanner = new Scanner(inputStream)) {
                if (scanner.hasNext())
                    consumer.accept(scanner.next());
            } catch (IOException exception) {
                this.plugin.error("Cannot look for updates: " + exception.getMessage());
            }
        }, 3L * 20L);
    }
}
