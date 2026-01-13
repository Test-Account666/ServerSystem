package me.testaccount666.serversystem.userdata.persistence

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import org.bukkit.configuration.file.FileConfiguration
import java.util.function.Consumer

class KitMapFieldHandler : FieldHandler<MutableMap<String, Long>> {
    override fun save(config: FileConfiguration, path: String, value: MutableMap<String, Long>?) {
        config.set(path, null)
        if (value.isNullOrEmpty()) return
        val kitManager = ServerSystem.instance.registry.getServiceOrNull<KitManager>() ?: return

        value.forEach { (kitName: String, cooldown: Long) ->
            if (!kitManager.kitExists(kitName)) return@forEach
            config.set("${path}.${kitName}", cooldown)
        }
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: MutableMap<String, Long>?): MutableMap<String, Long>? {
        if (!config.isSet(path)) return defaultValue
        if (!ServerSystem.instance.registry.hasService<KitManager>()) return defaultValue

        val kitCooldowns = HashMap<String, Long>()
        config.getConfigurationSection(path)!!.getKeys(false)
            .forEach(Consumer { kitName -> kitCooldowns[kitName] = config.getLong("${path}.${kitName}") })
        return kitCooldowns
    }
}