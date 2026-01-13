package me.testaccount666.serversystem.clickablesigns.executables.time

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
import java.util.*

class ConfiguratorTimeSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Time.Create"
    override val successMessageKey = "Time.Created"
    override val signType = SignType.TIME

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val front = sign.getSide(Side.FRONT)
        val timeType = front.getLine(1).lowercase(Locale.getDefault())
        if (timeType.isEmpty()) {
            sign("Time.NoTimeSpecified", user).build()
            return false
        }

        if (!isValidTimeType(timeType)) {
            sign("Time.InvalidTime", user) {
                postModifier { it.replace("<TIME>", timeType) }
            }.build()
            return false
        }

        front.line(0, translateToComponent(SignType.TIME.signName()))
        front.line(1, translateToComponent("&2${timeType}"))
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var timeType = sign.getSide(Side.FRONT).getLine(1).lowercase(Locale.getDefault())
        timeType = stripColor(timeType)
        config.set("TimeType", timeType)
    }

    private fun isValidTimeType(timeType: String): Boolean {
        return timeType == "day" ||
                timeType == "night" ||
                timeType == "noon" ||
                timeType == "midnight" ||
                timeType.matches("\\d+".toRegex())
    }
}