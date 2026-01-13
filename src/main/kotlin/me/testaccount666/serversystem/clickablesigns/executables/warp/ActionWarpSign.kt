package me.testaccount666.serversystem.clickablesigns.executables.warp

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionWarpSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Warp"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration): Boolean {
        val warpManager = instance.registry.getServiceOrNull<WarpManager>()
        if (warpManager == null) {
            sign("Warp.NoWarpManager", user).build()
            return false
        }

        var warpName = config.getString("WarpName", sign.getLine(1))
        warpName = stripColor(warpName)
        if (warpName.isEmpty()) {
            sign("Warp.NoWarpSpecified", user).build()
            return false
        }

        val warp = warpManager.getWarpByName(warpName)
        if (warp == null) {
            sign("Warp.WarpNotFound", user) {
                postModifier { it.replace("<WARP>", warpName) }
            }.build()
            return false
        }

        user.getPlayer()?.teleport(warp.location)
        sign("Warp.Teleported", user) {
            postModifier { it.replace("<WARP>", warp.displayName) }
        }.build()
        return true
    }
}