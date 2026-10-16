package me.testaccount666.serversystem.clickablesigns

import me.testaccount666.paperktx.extensions.location
import me.testaccount666.serversystem.ServerSystem
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration

class SignManager {
    private val _signTypes = HashMap<Location, SignType>()
    private val _signDirectory = ServerSystem.instance.dataPath.resolve("data").resolve("signs").toFile()

    fun addSignType(location: Location, signType: SignType) = let { _signTypes[location] = signType }

    fun removeSignType(location: Location) = let { _signTypes.remove(location) }

    fun getSignType(location: Location) = _signTypes[location]

    fun loadSignTypes() {
        if (!_signDirectory.exists()) return
        val files = _signDirectory.listFiles() ?: return

        for (file in files) {
            val fileConfig = YamlConfiguration.loadConfiguration(file!!)
            val key = fileConfig.getString("Key") ?: continue
            val signType = SignType.getSignTypeByKey(key) ?: continue
            val locationSplit = file.nameWithoutExtension.split("_").dropLastWhile(String::isEmpty)
            if (locationSplit.size != 4) continue

            val (worldName, x, y, z) = locationSplit

            val world = Bukkit.getWorld(worldName) ?: continue
            val location = location(x.toInt(), y.toInt(), z.toInt(), world)
            addSignType(location, signType)
        }
    }
}
