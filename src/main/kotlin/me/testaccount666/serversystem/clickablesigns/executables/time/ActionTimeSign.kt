package me.testaccount666.serversystem.clickablesigns.executables.time

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionTimeSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Time"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration, onSuccess: () -> Unit): Boolean {
        var timeType = config.getString("TimeType", sign.getLine(1))!!.lowercase()
        timeType = stripColor(timeType)
        if (timeType.isEmpty()) {
            user.signMsg("Time.NoTimeSpecified")
            return false
        }

        val world = user.getPlayer()!!.world

        val time = when (timeType) {
            "day" -> 1000L
            "noon" -> 6000L
            "night" -> 13000L
            "midnight" -> 18000L
            else -> {
                try {
                    timeType.toLong()
                } catch (_: NumberFormatException) {
                    user.signMsg("Time.InvalidTime") {
                        postModifier { it.replace("<TIME>", timeType) }
                    }
                    return false
                }
            }
        }

        world.time = time
        user.signMsg("Time.TimeSet") {
            postModifier { it.replace("<TIME>", timeType) }
        }
        onSuccess()
        return true
    }
}