package me.testaccount666.serversystem.commands.executables.unlimited

import me.testaccount666.paperktx.extensions.isAir
import me.testaccount666.paperktx.extensions.set
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.NamespacedKey
import org.bukkit.command.Command

@ServerSystemCommand("unlimited")
class CommandUnlimited : AbstractServerSystemCommand() {
    val unlimitedKey = NamespacedKey(instance, "unlimited")
    override fun getUsagePermission(command: Command) = "Unlimited.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val itemInHand = commandSender.getPlayer()!!.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            commandSender.commandMsg("Unlimited.NoItemInHand")
            return
        }

        var setUnlimited = false
        itemInHand.editPersistentDataContainer {
            setUnlimited = !it.has(unlimitedKey)

            if (setUnlimited) it.set(unlimitedKey, 1.toByte())
            else it.remove(unlimitedKey)
        }

        val messagePath = if (setUnlimited) "Unlimited.Success.Enabled" else "Unlimited.Success.Disabled"
        commandSender.commandMsg(messagePath)
    }
}
