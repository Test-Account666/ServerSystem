package me.testaccount666.serversystem.userdata.persistence

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.extensions.getServiceOrNull
import org.bukkit.configuration.file.FileConfiguration

class KitMapFieldHandler : FieldHandler<HashMap<String, Long>> {
    override fun save(config: FileConfiguration, path: String, value: HashMap<String, Long>?) {
        config[path] = null
        if (value.isNullOrEmpty()) return
        val kitManager = getServiceOrNull<KitManager>() ?: return

        value.forEach { (kitName: String, cooldown: Long) ->
            if (!kitManager.kitExists(kitName)) return@forEach
            config["${path}.${kitName}"] = cooldown
        }
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: HashMap<String, Long>?): HashMap<String, Long>? {
        if (!config.isSet(path)) return defaultValue
        if (!ServerSystem.instance.registry.hasService<KitManager>()) return defaultValue

        val kitCooldowns = HashMap<String, Long>()
        config.getConfigurationSection(path)!!.getKeys(false).forEach { kitCooldowns[it] = config.getLong("${path}.${it}") }
        return kitCooldowns
    }
}