package me.testaccount666.serversystem.clickablesigns.executables.weather

import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import java.util.Locale.getDefault

class ActionWeatherSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Weather"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration): Boolean {
        var weatherType = config.getString("WeatherType", sign.getLine(1))?.lowercase(getDefault())
        weatherType = stripColor(weatherType)
        if (weatherType.isEmpty()) {
            sign("Weather.NoWeatherSpecified", user).build()
            return false
        }

        val world = user.getPlayer()?.world ?: return false

        val finalWeatherType = weatherType
        return when (weatherType) {
            "sun", "clear" -> {
                world.setStorm(false)
                world.isThundering = false
                sign("Weather.WeatherSet", user) {
                    postModifier { it.replace("<WEATHER>", "clear") }
                }.build()
                true
            }

            "rain", "storm" -> {
                world.setStorm(true)
                world.isThundering = false
                sign("Weather.WeatherSet", user) {
                    postModifier { it.replace("<WEATHER>", "rain") }
                }.build()
                true
            }

            "thunder" -> {
                world.setStorm(true)
                world.isThundering = true
                sign("Weather.WeatherSet", user) {
                    postModifier { it.replace("<WEATHER>", "thunder") }
                }.build()
                true
            }

            else -> {
                sign("Weather.InvalidWeather", user) {
                    postModifier { it.replace("<WEATHER>", finalWeatherType) }
                }.build()
                false
            }
        }
    }
}