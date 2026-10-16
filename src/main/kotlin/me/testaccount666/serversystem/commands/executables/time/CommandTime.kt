package me.testaccount666.serversystem.commands.executables.time

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("time", ["day", "night", "noon", "midnight"], TabCompleterTime::class)
class CommandTime : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = if (command.name.lowercase() == "time") 1 else 0
    override fun getUsagePermission(command: Command) = "Time.Use"
    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Time"

        return when (val commandName = command.name.lowercase()) {
            "time" -> "Time"
            "day" -> "Day"
            "night" -> "Night"
            "noon" -> "Noon"
            "midnight" -> "Midnight"
            else -> error("(CommandTime;SyntaxPath) Unexpected value: ${commandName}")
        }
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("time", true)) {
            handleTimeCommand(commandSender, command, label, *arguments)
            return
        }

        if (commandSender is ConsoleUser && arguments.isEmpty()) {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        if (arguments.isNotEmpty() && !checkPermission(commandSender, "Time.World")) return

        val world = (if (arguments.isNotEmpty()) Bukkit.getWorld(arguments[0]) else commandSender.getPlayer()!!.world) ?: run {
            commandSender.commandMsg("Time.WorldNotFound") { target(arguments[0]) }
            return
        }

        handleTimeCommand(commandSender, command, label, command.name, world.name)
    }

    private fun handleTimeCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (commandSender is ConsoleUser && arguments.size == 1) {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        if (arguments.size >= 2 && !checkPermission(commandSender, "Time.World")) return

        val world = (if (arguments.size >= 2) Bukkit.getWorld(arguments[1]) else commandSender.getPlayer()!!.world) ?: run {
            commandSender.commandMsg("Time.WorldNotFound") { target(arguments[1]) }
            return
        }

        when (arguments[0].lowercase()) {
            "day" -> world.time = 0
            "night" -> world.time = 13000
            "noon" -> world.time = 6000
            "midnight" -> world.time = 18000
            else -> {
                world.time = 20L * (arguments[0].toLongOrNull() ?: run {
                    commandSender.generalMsg("InvalidArguments") {
                        syntax(getSyntaxPath(command))
                        label(label)
                    }
                    return
                })
            }
        }

        commandSender.commandMsg("Time.Success") {
            target(world.name)
            postModifier {
                it.replace("<TIME>", arguments[0])
                    .replace("<WORLD>", world.name)
            }
        }
    }
}
