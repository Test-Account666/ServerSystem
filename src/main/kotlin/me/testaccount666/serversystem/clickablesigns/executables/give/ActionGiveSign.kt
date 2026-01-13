package me.testaccount666.serversystem.clickablesigns.executables.give

import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

class ActionGiveSign : AbstractSignClickAction() {
    override val basePermissionNode = "ClickableSigns.Give"

    override fun executeAction(user: User, sign: Sign, config: FileConfiguration): Boolean {
        val item = config.getItemStack("Item")
        if (item.isAir()) {
            sign("Give.NoItem", user).build()
            return false
        }

        val inventory = Bukkit.createInventory(null, 27, "Give Sign")
        for (index in 0..<inventory.size) inventory.setItem(index, item)

        user.getPlayer()?.openInventory(inventory)
        return true
    }
}
