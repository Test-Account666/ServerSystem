package me.testaccount666.serversystem.clickablesigns.executables.kit

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionKitSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Kit"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration, onSuccess: () -> Unit): Boolean {
        val kitManager = getService<KitManager>()

        val kitName = stripColor(config.getString("KitName", sign.getLine(1))).takeUnless { it.isEmpty() } ?: run {
            user.signMsg("Kit.NoKitSpecified")
            return false
        }

        val kit = kitManager.getKit(kitName) ?: run {
            user.signMsg("Kit.KitNotFound") {
                postModifier { it.replace("<KIT>", kitName) }
            }
            return false
        }

        if (user.isOnKitCooldown(kit.name)) {
            val cooldown = user.getKitCooldown(kitName)

            user.commandMsg("Kit.OnCooldown") {
                postModifier {
                    it.replace("<KIT>", kit.displayName)
                        .replace("<DATE>", parseDate(cooldown, user))
                }
            }
            return false
        }

        user.setKitCooldown(kit.name, kit.coolDown).also { user.save() }

        kit.giveKit(user.getPlayer()!!)
        user.signMsg("Kit.KitGiven") {
            postModifier { it.replace("<KIT>", kit.displayName) }
        }
        onSuccess()
        return true
    }
}