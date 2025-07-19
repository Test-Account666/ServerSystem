package me.testaccount666.serversystem.userdata.persistence;

import org.bukkit.configuration.file.FileConfiguration;

public class EnumFieldHandler implements FieldHandler<Enum<?>> {

    @Override
    public void save(FileConfiguration config, String path, Enum<?> value) {
        if (value == null) {
            config.set(path, null);
            return;
        }

        config.set(path, value.name());
    }

    @Override
    public Enum<?> load(FileConfiguration config, String path, Enum<?> defaultValue) {
        if (!config.isSet(path)) return defaultValue;

        var enumName = config.getString(path);
        if (enumName == null) return defaultValue;

        return Enum.valueOf(defaultValue.getDeclaringClass(), enumName);
    }
}
