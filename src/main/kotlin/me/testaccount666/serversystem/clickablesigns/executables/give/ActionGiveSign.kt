package me.testaccount666.serversystem.clickablesigns.executables.give

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.extensions.isAir
import me.testaccount666.paperktx.extensions.set
import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionGiveSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Give"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration, onSuccess: () -> Unit): Boolean {
        val item = config.getItemStack("Item").takeUnless { it.isAir() } ?: run {
            user.signMsg("Give.NoItem")
            return false
        }

        val inventory = Bukkit.createInventory(null, 27, "Give Sign".asComponent())
        inventory[0..<inventory.size] = item

        user.getPlayer()!!.openInventory(inventory)
        onSuccess()
        return true
    }
}
