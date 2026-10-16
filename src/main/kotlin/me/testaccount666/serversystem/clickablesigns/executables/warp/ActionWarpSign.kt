package me.testaccount666.serversystem.clickablesigns.executables.warp

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.paperktx.extensions.getValue
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.commands.executables.waypoints.warp.manager.WarpManager
import me.testaccount666.serversystem.extensions.getServiceOrNull
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionWarpSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Warp"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration, onSuccess: () -> Unit): Boolean {
        val warpManager = getServiceOrNull<WarpManager>() ?: run {
            user.signMsg("Warp.NoWarpManager")
            return false
        }

        val warpName = stripColor(config.getValue("WarpName", sign.getLine(1)))
        if (warpName.isEmpty()) {
            user.signMsg("Warp.NoWarpSpecified")
            return false
        }

        val warp = warpManager.getPointByName(warpName) ?: run {
            user.signMsg("Warp.WarpNotFound") {
                postModifier { it.replace("<WARP>", warpName) }
            }
            return false
        }

        user.getPlayer()?.teleport(warp.location)
        user.signMsg("Warp.Teleported") {
            postModifier { it.replace("<WARP>", warp.displayName) }
        }
        onSuccess()
        return true
    }
}