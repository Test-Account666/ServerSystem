package me.testaccount666.serversystem.clickablesigns.executables.time

import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

class ActionTimeSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Time"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration): Boolean {
        var timeType = config.getString("TimeType", sign.getLine(1))?.lowercase(Locale.getDefault())
        timeType = stripColor(timeType)
        if (timeType.isEmpty()) {
            sign("Time.NoTimeSpecified", user).build()
            return false
        }

        val world = user.getPlayer()?.world ?: return false

        val time = when (timeType) {
            "day" -> 1000
            "noon" -> 6000
            "night" -> 13000
            "midnight" -> 18000
            else -> {
                try {
                    timeType.toLong()
                } catch (_: NumberFormatException) {
                    sign("Time.InvalidTime", user) {
                        postModifier { it.replace("<TIME>", timeType) }
                    }.build()
                    return false
                }
            }
        }

        world.time = time
        sign("Time.TimeSet", user) {
            postModifier { it.replace("<TIME>", timeType) }
        }.build()
        return true
    }
}