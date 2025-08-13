package me.testaccount666.serversystem.userdata.persistence;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class KitMapFieldHandler implements FieldHandler<Map<String, Long>> {
    @Override
    public void save(FileConfiguration config, String path, Map<String, Long> value) {
        config.set(path, null);
        if (value == null || value.isEmpty()) return;
        var kitManager = ServerSystem.Instance.getRegistry().getService(KitManager.class);
        if (kitManager == null) return;

        value.forEach((kitName, cooldown) -> {
            if (!kitManager.kitExists(kitName)) return;
            config.set("${path}.${kitName}", cooldown);
        });
    }

    @Override
    public Map<String, Long> load(FileConfiguration config, String path, Map<String, Long> defaultValue) {
        if (!config.isSet(path)) return defaultValue;
        if (ServerSystem.Instance.getRegistry().getService(KitManager.class) == null) return defaultValue;

        var kitCooldowns = new HashMap<String, Long>();
        config.getConfigurationSection(path).getKeys(false).forEach(kitName -> kitCooldowns.put(kitName, config.getLong("${path}.${kitName}")));
        return kitCooldowns;
    }
}
