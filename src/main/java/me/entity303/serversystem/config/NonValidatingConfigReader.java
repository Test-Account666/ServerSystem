package me.entity303.serversystem.config;

import me.entity303.serversystem.main.ServerSystem;

import java.io.File;

public class NonValidatingConfigReader extends DefaultConfigReader {

    public NonValidatingConfigReader(File file, ServerSystem plugin) {
        super(file, plugin);
    }

    public static IConfigReader LoadConfiguration(File file) {
        return new NonValidatingConfigReader(file, ServerSystem.getPlugin(ServerSystem.class));
    }

    @Override
    protected boolean ValidateConfig() {
        return true;
    }
}
