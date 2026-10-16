package me.testaccount666.serversystem.clickablesigns.executables.weather

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.paperktx.extensions.ChecksExtensions.isAny
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

class ConfiguratorWeatherSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Weather.Create"
    override val successMessageKey = "Weather.Created"
    override val signType = SignType.WEATHER

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val front = sign.getSide(Side.FRONT)
        val weatherType = front.getLine(1).lowercase()
        if (weatherType.isEmpty()) {
            user.signMsg("Weather.NoWeatherSpecified")
            return false
        }

        if (!isValidWeatherType(weatherType)) {
            user.signMsg("Weather.InvalidWeather") {
                postModifier { it.replace("<WEATHER>", weatherType) }
            }
            return false
        }

        front.line(0, SignType.WEATHER.signName.asComponent())
        front.line(1, "&2${weatherType}".asComponent())
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var weatherType = sign.getSide(Side.FRONT).getLine(1).lowercase()
        weatherType = stripColor(weatherType)
        config["WeatherType"] = weatherType
    }

    private fun isValidWeatherType(weatherType: String) = weatherType.isAny("sun", "clear", "storm", "thunder", "rain")
}