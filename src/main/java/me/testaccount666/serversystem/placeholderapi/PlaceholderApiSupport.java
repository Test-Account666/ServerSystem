package me.testaccount666.serversystem.placeholderapi;

import me.testaccount666.serversystem.placeholderapi.executables.PlaceholderExpansionWrapper;
import org.bukkit.Bukkit;

public class PlaceholderApiSupport {
    private static PlaceholderExpansionWrapper _Wrapper;

    private PlaceholderApiSupport() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    public static boolean isPlaceholderApiInstalled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static void registerPlaceholders() {
        Bukkit.getLogger().info("Registering PlaceholderAPI placeholders...");

        if (!isPlaceholderApiInstalled()) return;

        Bukkit.getLogger().info("PlaceholderAPI is installed, registering placeholders...");

        if (_Wrapper != null) {
            Bukkit.getLogger().info("PlaceholderAPI placeholders are already registered, unregistering old placeholders and registering new placeholders!");
            unregisterPlaceholders();
        }

        _Wrapper = new PlaceholderExpansionWrapper();
        _Wrapper.register();

        Bukkit.getLogger().info("PlaceholderAPI placeholders registered successfully!");
    }

    public static void unregisterPlaceholders() {
        Bukkit.getLogger().info("Unregistering PlaceholderAPI placeholders...");

        if (!isPlaceholderApiInstalled()) return;
        Bukkit.getLogger().info("PlaceholderAPI is installed, unregistering placeholders...");

        if (_Wrapper == null) {
            Bukkit.getLogger().info("PlaceholderAPI placeholders are not registered, nothing to unregister!");
            return;
        }

        _Wrapper.unregister();
        _Wrapper = null;

        Bukkit.getLogger().info("PlaceholderAPI placeholders unregistered successfully!");
    }
}
