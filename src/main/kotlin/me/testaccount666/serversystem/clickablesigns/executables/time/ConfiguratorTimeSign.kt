package me.testaccount666.serversystem.clickablesigns.executables.time

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

class ConfiguratorTimeSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Time.Create"
    override val successMessageKey = "Time.Created"
    override val signType = SignType.TIME

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val front = sign.getSide(Side.FRONT)
        val timeType = front.getLine(1).lowercase()
        if (timeType.isEmpty()) {
            user.signMsg("Time.NoTimeSpecified")
            return false
        }

        if (!isValidTimeType(timeType)) {
            user.signMsg("Time.InvalidTime") {
                postModifier { it.replace("<TIME>", timeType) }
            }
            return false
        }

        front.line(0, SignType.TIME.signName.asComponent())
        front.line(1, "&2${timeType}".asComponent())
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        val timeType = sign.getSide(Side.FRONT).getLine(1).lowercase()
        config["TimeType"] = stripColor(timeType)
    }

    private fun isValidTimeType(timeType: String): Boolean {
        return timeType.isAny("day", "night", "noon", "midnight") || timeType.matches("\\d+".toRegex())
    }
}