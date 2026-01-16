package me.testaccount666.serversystem.clickablesigns

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Path

class SignManager {
    private val _signTypes = HashMap<Location, SignType>()
    private val _signDirectory: File = Path.of("plugins", "ServerSystem", "data", "signs").toFile()

    fun addSignType(location: Location, signType: SignType) {
        _signTypes[location] = signType
    }

    fun removeSignType(location: Location) {
        _signTypes.remove(location)
    }

    fun getSignType(location: Location): SignType? {
        return _signTypes[location]
    }

    fun loadSignTypes() {
        if (!_signDirectory.exists()) return
        val files = _signDirectory.listFiles() ?: return

        for (file in files) {
            val fileConfig = YamlConfiguration.loadConfiguration(file!!)
            val key = fileConfig.getString("Key") ?: continue
            val signType = SignType.getSignTypeByKey(key) ?: continue
            val locationString = file.name.substring(0, file.name.indexOf("."))
            val locationSplit = locationString.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (locationSplit.size != 4) continue

            val worldName = locationSplit[0]
            val xString = locationSplit[1]
            val yString = locationSplit[2]
            val zString = locationSplit[3]

            val world = Bukkit.getWorld(worldName) ?: continue
            val x = xString.toInt()
            val y = yString.toInt()
            val z = zString.toInt()
            val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
            addSignType(location, signType)
        }
    }
}
