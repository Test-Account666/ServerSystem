package me.testaccount666.serversystem.userdata.persistence;

import me.testaccount666.serversystem.userdata.vanish.VanishData;
import org.bukkit.configuration.file.FileConfiguration;

public class VanishDataFieldHandler implements FieldHandler<VanishData> {

    @Override
    public void save(FileConfiguration config, String path, VanishData value) {
        config.set("${path}.CanMessage", value.canMessage());
        config.set("${path}.CanInteract", value.canInteract());
        config.set("${path}.CanPickup", value.canPickup());
        config.set("${path}.CanDrop", value.canDrop());
    }

    @Override
    public VanishData load(FileConfiguration config, String path, VanishData defaultValue) {
        var canMessage = config.getBoolean("${path}.CanMessage", defaultValue.canMessage());
        var canInteract = config.getBoolean("${path}.CanInteract", defaultValue.canInteract());
        var canPickup = config.getBoolean("${path}.CanPickup", defaultValue.canPickup());
        var canDrop = config.getBoolean("${path}.CanDrop", defaultValue.canDrop());

        return new VanishData(canMessage, canInteract, canPickup, canDrop);
    }
}
