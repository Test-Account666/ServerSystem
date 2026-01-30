package me.testaccount666.serversystem.commands.executables.unlimited

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.persistence.PersistentDataType

@ServerSystemCommand("unlimited")
class CommandUnlimited : AbstractServerSystemCommand() {
    val unlimitedKey = NamespacedKey(instance, "unlimited")
    override fun getUsagePermission(command: Command) = "Unlimited.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val itemInHand = commandSender.getPlayer()!!.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Unlimited.NoItemInHand", commandSender).build()
            return
        }

        val itemMeta = itemInHand.itemMeta
        val dataContainer = itemMeta.persistentDataContainer

        val setUnlimited = !dataContainer.has(unlimitedKey, PersistentDataType.BYTE)

        if (setUnlimited) dataContainer.set(unlimitedKey, PersistentDataType.BYTE, 1.toByte())
        else dataContainer.remove(unlimitedKey)

        itemInHand.setItemMeta(itemMeta)

        val messagePath = if (setUnlimited) "Unlimited.Success.Enabled" else "Unlimited.Success.Disabled"
        command(messagePath, commandSender).build()
    }
}
