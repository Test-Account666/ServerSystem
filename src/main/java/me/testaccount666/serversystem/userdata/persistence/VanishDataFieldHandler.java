package me.testaccount666.serversystem.userdata.persistence;

import me.testaccount666.serversystem.userdata.vanish.VanishData;
import org.bukkit.configuration.file.FileConfiguration;

public class VanishDataFieldHandler implements FieldHandler<VanishData> {

    @Override
    public void save(FileConfiguration config, String path, VanishData value) {
        config.set("${path}.CanMessage", value.isCanMessage());
        config.set("${path}.CanInteract", value.isCanInteract());
        config.set("${path}.CanPickup", value.isCanPickup());
        config.set("${path}.CanDrop", value.isCanDrop());
    }

    @Override
    public VanishData load(FileConfiguration config, String path, VanishData defaultValue) {
        var canMessage = config.getBoolean("${path}.CanMessage", defaultValue.isCanMessage());
        var canInteract = config.getBoolean("${path}.CanInteract", defaultValue.isCanInteract());
        var canPickup = config.getBoolean("${path}.CanPickup", defaultValue.isCanPickup());
        var canDrop = config.getBoolean("${path}.CanDrop", defaultValue.isCanDrop());

        return new VanishData(canMessage, canInteract, canPickup, canDrop);
    }
}
