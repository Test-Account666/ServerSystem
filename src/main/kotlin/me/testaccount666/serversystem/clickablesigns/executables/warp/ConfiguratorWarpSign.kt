package me.testaccount666.serversystem.clickablesigns.executables.warp

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.WarpManager
import me.testaccount666.serversystem.extensions.getServiceOrNull
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

class ConfiguratorWarpSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Warp.Create"
    override val signType = SignType.WARP
    override val successMessageKey = "Warp.Created"


    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val warpManager = getServiceOrNull<WarpManager>() ?: run {
            user.signMsg("Warp.NoWarpManager")
            return false
        }

        val front = sign.getSide(Side.FRONT)
        val warpName = front.getLine(1)
        if (warpName.isEmpty()) {
            user.signMsg("Warp.NoWarpSpecified")
            return false
        }

        if (!warpManager.pointExists(warpName)) {
            user.signMsg("Warp.WarpNotFound") {
                postModifier { it.replace("<WARP>", warpName) }
            }
            return false
        }

        front.line(0, SignType.WARP.signName.asComponent())
        front.line(1, "&2${warpName}".asComponent())
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var warpName = sign.getSide(Side.FRONT).getLine(1)
        warpName = stripColor(warpName)
        config["WarpName"] = warpName
    }
}