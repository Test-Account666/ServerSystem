package me.testaccount666.serversystem.clickablesigns.executables.weather

import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.util.Locale.getDefault

class ConfiguratorWeatherSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Weather.Create"
    override val successMessageKey = "Weather.Created"
    override val signType = SignType.WEATHER

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val front = sign.getSide(Side.FRONT)
        val weatherType = front.getLine(1).lowercase(getDefault())
        if (weatherType.isEmpty()) {
            sign("Weather.NoWeatherSpecified", user).build()
            return false
        }

        if (!isValidWeatherType(weatherType)) {
            sign("Weather.InvalidWeather", user) {
                postModifier { it.replace("<WEATHER>", weatherType) }
            }.build()
            return false
        }

        front.line(0, translateToComponent(SignType.WEATHER.signName()))
        front.line(1, translateToComponent("&2${weatherType}"))
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var weatherType = sign.getSide(Side.FRONT).getLine(1).lowercase(getDefault())
        weatherType = stripColor(weatherType)
        config.set("WeatherType", weatherType)
    }

    private fun isValidWeatherType(weatherType: String): Boolean {
        return weatherType == "sun" ||
                weatherType == "clear" ||
                weatherType == "storm" ||
                weatherType == "thunder" ||
                weatherType == "rain"
    }
}