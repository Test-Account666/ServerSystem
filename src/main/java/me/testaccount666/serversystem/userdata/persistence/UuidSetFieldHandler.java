package me.testaccount666.serversystem.userdata.persistence;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A field handler for sets of UUIDs.
 * This handler saves UUIDs as strings and loads them back as UUIDs.
 */
public class UuidSetFieldHandler implements FieldHandler<Set<UUID>> {

    @Override
    public void save(FileConfiguration config, String path, Set<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            config.set(path, null);
            return;
        }

        var uuidStrings = uuids.stream().map(UUID::toString).collect(Collectors.toList());

        config.set(path, uuidStrings);
    }

    @Override
    public Set<UUID> load(FileConfiguration config, String path, Set<UUID> defaultValue) {
        if (!config.isSet(path)) return defaultValue != null? defaultValue : new HashSet<>();

        var uuidStrings = config.getStringList(path);
        if (uuidStrings.isEmpty()) return defaultValue != null? defaultValue : new HashSet<>();

        return uuidStrings.stream().map(UUID::fromString).collect(Collectors.toSet());
    }
}