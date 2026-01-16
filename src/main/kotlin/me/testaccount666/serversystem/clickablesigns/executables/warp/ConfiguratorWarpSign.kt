package me.testaccount666.serversystem.clickablesigns.executables.warp

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

class ConfiguratorWarpSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Warp.Create"
    override val signType = SignType.WARP
    override val successMessageKey = "Warp.Created"


    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val warpManager = instance.registry.getServiceOrNull<WarpManager>()
        if (warpManager == null) {
            sign("Warp.NoWarpManager", user).build()
            return false
        }

        val front = sign.getSide(Side.FRONT)
        val warpName = front.getLine(1)
        if (warpName.isEmpty()) {
            sign("Warp.NoWarpSpecified", user).build()
            return false
        }

        if (!warpManager.warpExists(warpName)) {
            sign("Warp.WarpNotFound", user) {
                postModifier { it.replace("<WARP>", warpName) }
            }.build()
            return false
        }

        front.line(0, translateToComponent(SignType.WARP.signName))
        front.line(1, translateToComponent("&2${warpName}"))
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var warpName = sign.getSide(Side.FRONT).getLine(1)
        warpName = stripColor(warpName)
        config.set("WarpName", warpName)
    }
}