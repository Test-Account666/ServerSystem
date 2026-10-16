package me.testaccount666.serversystem.userdata.persistence

import me.testaccount666.paperktx.extensions.getValue
import me.testaccount666.serversystem.userdata.vanish.VanishData
import org.bukkit.configuration.file.FileConfiguration

class VanishDataFieldHandler : FieldHandler<VanishData> {
    override fun save(config: FileConfiguration, path: String, value: VanishData?) {
        val actualValue = value ?: VanishData(true, true, true, true)

        config["${path}.CanMessage"] = actualValue.canMessage
        config["${path}.CanInteract"] = actualValue.canInteract
        config["${path}.CanPickup"] = actualValue.canPickup
        config["${path}.CanDrop"] = actualValue.canDrop
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: VanishData?): VanishData {
        val defValue = defaultValue ?: VanishData(true, true, true, true)

        val canMessage = config.getValue("${path}.CanMessage", defValue.canMessage)
        val canInteract = config.getValue("${path}.CanInteract", defValue.canInteract)
        val canPickup = config.getValue("${path}.CanPickup", defValue.canPickup)
        val canDrop = config.getValue("${path}.CanDrop", defValue.canDrop)

        return VanishData(canMessage, canInteract, canPickup, canDrop)
    }
}