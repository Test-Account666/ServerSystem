package me.testaccount666.serversystem.managers.config;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;

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

    @Override
    protected void loadDefaultConfig() {
        // Nothing to do
    }

    @Override
    public void load(File file) {
        try {
            _newReader = new NonValidatingConfigReader(file, ServerSystem.Instance);
        } catch (Exception exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to load configuration file '${file.getName()}'", exception);
        }
    }
}
