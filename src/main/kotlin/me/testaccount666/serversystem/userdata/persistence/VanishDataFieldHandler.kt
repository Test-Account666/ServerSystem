package me.testaccount666.serversystem.userdata.persistence

import me.testaccount666.serversystem.userdata.vanish.VanishData
import org.bukkit.configuration.file.FileConfiguration

class VanishDataFieldHandler : FieldHandler<VanishData> {
    override fun save(config: FileConfiguration, path: String, value: VanishData?) {
        val actualValue = value ?: VanishData(true, true, true, true);

        config.set("${path}.CanMessage", actualValue.canMessage())
        config.set("${path}.CanInteract", actualValue.canInteract())
        config.set("${path}.CanPickup", actualValue.canPickup())
        config.set("${path}.CanDrop", actualValue.canDrop())
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: VanishData?): VanishData {
        val defValue = defaultValue ?: VanishData(true, true, true, true);

        val canMessage = config.getBoolean("${path}.CanMessage", defValue.canMessage())
        val canInteract = config.getBoolean("${path}.CanInteract", defValue.canInteract())
        val canPickup = config.getBoolean("${path}.CanPickup", defValue.canPickup())
        val canDrop = config.getBoolean("${path}.CanDrop", defValue.canDrop())

        return VanishData(canMessage, canInteract, canPickup, canDrop)
    }
}