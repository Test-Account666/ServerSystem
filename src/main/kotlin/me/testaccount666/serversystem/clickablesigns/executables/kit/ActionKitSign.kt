package me.testaccount666.serversystem.clickablesigns.executables.kit

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

class ActionKitSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Kit"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration): Boolean {
        val kitManager = instance.registry.getService<KitManager>()

        var kitName = config.getString("KitName", sign.getLine(1))
        kitName = stripColor(kitName)
        if (kitName.isEmpty()) {
            sign("Kit.NoKitSpecified", user).build()
            return false
        }

        val kit = kitManager.getKit(kitName.lowercase(Locale.getDefault()))
        if (kit == null) {
            sign("Kit.KitNotFound", user) {
                postModifier { it.replace("<KIT>", kitName) }
            }.build()
            return false
        }

        kit.giveKit(user.getPlayer()!!)
        sign("Kit.KitGiven", user) {
            postModifier { it.replace("<KIT>", kit.displayName) }
        }.build()
        return true
    }
}