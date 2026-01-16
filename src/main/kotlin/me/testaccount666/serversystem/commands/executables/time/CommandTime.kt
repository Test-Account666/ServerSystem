package me.testaccount666.serversystem.commands.executables.time

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

@ServerSystemCommand("time", ["day", "night", "noon", "midnight"], TabCompleterTime::class)
class CommandTime : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("time", true)) {
            handleTimeCommand(commandSender, command, label, *arguments)
            return
        }

        if (commandSender is ConsoleUser && arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val world: World?

        if (arguments.isNotEmpty()) {
            if (!checkBasePermission(commandSender, "Time.World")) return
            world = Bukkit.getWorld(arguments[0])
        } else world = commandSender.getPlayer()!!.world

        if (world == null) {
            command("Time.WorldNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        handleTimeCommand(commandSender, command, label, command.name, world.name)
    }

    private fun handleTimeCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Time.Use")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        if (commandSender is ConsoleUser && arguments.size == 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val world: World?

        if (arguments.size >= 2) {
            if (!checkBasePermission(commandSender, "Time.World")) return
            world = Bukkit.getWorld(arguments[1])
        } else world = commandSender.getPlayer()!!.world

        if (world == null) {
            command("Time.WorldNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }

        when (arguments[0].lowercase(getDefault())) {
            "day" -> world.time = 0
            "night" -> world.time = 13000
            "noon" -> world.time = 6000
            "midnight" -> world.time = 18000
            else -> {
                try {
                    var time = arguments[0].toLong()
                    time *= 20

                    world.time = time
                } catch (_: NumberFormatException) {
                    general("InvalidArguments", commandSender) {
                        syntax(getSyntaxPath(command))
                        label(label)
                    }.build()
                    return
                }
            }
        }

        command("Time.Success", commandSender) {
            target(world.name)
            postModifier {
                it.replace("<TIME>", arguments[0])
                    .replace("<WORLD>", world.name)
            }
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Time"

        return when (val commandName = command.name.lowercase(getDefault())) {
            "time" -> "Time"
            "day" -> "Day"
            "night" -> "Night"
            "noon" -> "Noon"
            "midnight" -> "Midnight"
            else -> error("(CommandTime;SyntaxPath) Unexpected value: ${commandName}")
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Time.Use", false)
    }
}
