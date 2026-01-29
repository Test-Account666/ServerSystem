package me.testaccount666.serversystem.commands.executables.skull

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.net.URI
import java.util.*

@ServerSystemCommand("skull")
class CommandSkull : AbstractServerSystemCommand() {
    private val _skullCreator = SkullCreator()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Skull.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val other = arguments.isNotEmpty()
        if (other && !checkOtherPermission(commandSender, "Skull.Other", arguments[0])) return

        Bukkit.getScheduler().runTaskAsynchronously(instance, Runnable {
            if (other) executeOtherSkull(commandSender, *arguments)
            else executeSelfSkull(commandSender)
        })
    }

    private fun executeSelfSkull(commandSender: User) {
        val skull = _skullCreator.getSkull(commandSender.getNameSafe())
        commandSender.getPlayer()!!.inventory.addItem(skull)
    }

    private fun executeOtherSkull(commandSender: User, vararg arguments: String) {
        val skull = createSkullFromInput(commandSender, arguments[0]) ?: run {
            general("ErrorOccurred", commandSender).build()
            return
        }

        commandSender.getPlayer()!!.inventory.addItem(skull)
        command("Skull.Success", commandSender).build()
    }

    private fun createSkullFromInput(commandSender: User, input: String): ItemStack? {
        parseUuid(input)?.let { return _skullCreator.getSkull(it) }

        parseUrl(input)?.let {
            command("Skull.Fetching", commandSender).build()
            return _skullCreator.getSkullByTexture(it)
        }

        return _skullCreator.getSkull(input)
    }

    override fun getSyntaxPath(command: Command?) = "Skull"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Skull.Use", false)
    }

    companion object {
        private fun parseUuid(input: String) = runCatching { UUID.fromString(input) }.getOrNull()

        private fun parseUrl(input: String) = runCatching { URI.create(input).toURL() }.getOrNull()
    }
}
