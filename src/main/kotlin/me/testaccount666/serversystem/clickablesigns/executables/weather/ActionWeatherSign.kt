package me.testaccount666.serversystem.clickablesigns.executables.weather

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionWeatherSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Weather"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration, onSuccess: () -> Unit): Boolean {
        var weatherType = config.getString("WeatherType", sign.getLine(1))!!.lowercase()
        weatherType = stripColor(weatherType)
        if (weatherType.isEmpty()) {
            user.signMsg("Weather.NoWeatherSpecified")
            return false
        }

        val world = user.getPlayer()!!.world

        val success = when (weatherType) {
            "sun", "clear" -> {
                world.setStorm(false)
                world.isThundering = false
                user.signMsg("Weather.WeatherSet") {
                    postModifier { it.replace("<WEATHER>", "clear") }
                }
                true
            }

            "rain", "storm" -> {
                world.setStorm(true)
                world.isThundering = false
                user.signMsg("Weather.WeatherSet") {
                    postModifier { it.replace("<WEATHER>", "rain") }
                }
                true
            }

            "thunder" -> {
                world.setStorm(true)
                world.isThundering = true
                user.signMsg("Weather.WeatherSet") {
                    postModifier { it.replace("<WEATHER>", "thunder") }
                }
                true
            }

            else -> {
                user.signMsg("Weather.InvalidWeather") {
                    postModifier { it.replace("<WEATHER>", weatherType) }
                }
                false
            }
        }
        if (success) onSuccess().also { return true }
        return false
    }
}