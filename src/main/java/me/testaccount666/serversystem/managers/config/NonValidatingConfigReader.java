package me.testaccount666.serversystem.managers.config;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;

public class NonValidatingConfigReader extends DefaultConfigReader {
    /**
     * Creates a new DefaultConfigReader for the specified file and plugin.
     *
     * @param file   The configuration file to read
     * @param plugin The plugin associated with this configuration
     * @throws FileNotFoundException If the default configuration cannot be found
     */
    public NonValidatingConfigReader(File file, Plugin plugin) throws FileNotFoundException {
        super(file, plugin);
    }

    public static ConfigReader loadConfiguration(File file) throws FileNotFoundException {
        return new NonValidatingConfigReader(file, ServerSystem.Instance);
    }

    @Override
    protected boolean validateAndFixConfig() {
        return true;
    }
}
