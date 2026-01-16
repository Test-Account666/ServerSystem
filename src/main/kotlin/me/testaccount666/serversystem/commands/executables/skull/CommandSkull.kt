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
import java.net.URL
import java.util.*

@ServerSystemCommand("skull")
class CommandSkull : AbstractServerSystemCommand() {
    private val _skullCreator = SkullCreator()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, Runnable {
            if (!checkBasePermission(commandSender, "Skull.Use")) return@Runnable
            if (commandSender is ConsoleUser) {
                general("NotPlayer", commandSender).build()
                return@Runnable
            }

            if (arguments.isEmpty()) {
                executeSelfSkull(commandSender)
                return@Runnable
            }

            if (!checkOtherPermission(commandSender, "Skull.Other", arguments[0])) return@Runnable
            executeOtherSkull(commandSender, *arguments)
        })
    }

    private fun executeSelfSkull(commandSender: User) {
        val skull = _skullCreator.getSkull(commandSender.getNameSafe())
        commandSender.getPlayer()!!.inventory.addItem(skull)
    }

    private fun executeOtherSkull(commandSender: User, vararg arguments: String) {
        val skull = createSkullFromInput(commandSender, arguments[0])
        if (skull == null) {
            general("ErrorOccurred", commandSender).build()
            return
        }

        commandSender.getPlayer()!!.inventory.addItem(skull)
        command("Skull.Success", commandSender).build()
    }

    private fun createSkullFromInput(commandSender: User, input: String): ItemStack? {
        val uuid = parseUuid(input)
        if (uuid != null) return _skullCreator.getSkull(uuid)

        val parsedUrl = parseUrl(input)
        if (parsedUrl != null) {
            command("Skull.Fetching", commandSender).build()
            return _skullCreator.getSkullByTexture(parsedUrl)
        }

        return _skullCreator.getSkull(input)
    }

    override fun getSyntaxPath(command: Command?) = "Skull"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Skull.Use", false)
    }

    companion object {
        private fun parseUuid(input: String): UUID? {
            return try {
                UUID.fromString(input)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        private fun parseUrl(input: String): URL? {
            return try {
                URI.create(input).toURL()
            } catch (_: Exception) {
                null
            }
        }
    }
}
