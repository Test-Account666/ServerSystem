package me.testaccount666.serversystem.commands.executables.skull

import me.testaccount666.paperktx.scheduler.skedule.okkero.SynchronizationContext
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.inventory.ItemStack
import java.net.URI
import java.util.*

@ServerSystemCommand("skull")
class CommandSkull : AbstractServerSystemCommand() {
    private val _skullCreator = SkullCreator()
    override fun getUsagePermission(command: Command) = "Skull.Use"
    override fun getSyntaxPath(command: Command?) = "Skull"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val other = arguments.isNotEmpty()
        if (other && !checkPermission(commandSender, "Skull.Other", arguments[0])) return

        instance.schedule {
            switchContext(SynchronizationContext.ASYNC)
            if (other) executeOtherSkull(commandSender, *arguments)
            else executeSelfSkull(commandSender)
        }
    }

    private fun executeSelfSkull(commandSender: User) {
        val skull = _skullCreator.getSkull(commandSender.nameSafe)
        commandSender.getPlayer()!!.inventory.addItem(skull)
    }

    private fun executeOtherSkull(commandSender: User, vararg arguments: String) {
        val skull = createSkullFromInput(commandSender, arguments[0]) ?: run {
            commandSender.generalMsg("ErrorOccurred")
            return
        }

        commandSender.getPlayer()!!.inventory.addItem(skull)
        commandSender.commandMsg("Skull.Success")
    }

    private fun createSkullFromInput(commandSender: User, input: String): ItemStack? {
        parseUuid(input)?.let { return _skullCreator.getSkull(it) }

        parseUrl(input)?.let {
            commandSender.commandMsg("Skull.Fetching")
            return _skullCreator.getSkullByTexture(it)
        }

        return _skullCreator.getSkull(input)
    }

    companion object {
        private fun parseUuid(input: String) = runCatching { UUID.fromString(input) }.getOrNull()

        private fun parseUrl(input: String) = runCatching { URI.create(input).toURL() }.getOrNull()
    }
}
