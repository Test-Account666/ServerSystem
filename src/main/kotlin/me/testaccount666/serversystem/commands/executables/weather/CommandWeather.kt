package me.testaccount666.serversystem.commands.executables.weather

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command
import java.util.concurrent.ThreadLocalRandom

@ServerSystemCommand("weather", ["sun", "storm", "rain"], TabCompleterWeather::class)
class CommandWeather : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = if (command.name.equals("weather", true)) 1 else 0
    override fun getUsagePermission(command: Command) = "Weather.Use"
    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Weather"

        return when (val commandName = command.name.lowercase()) {
            "weather" -> "Weather"
            "sun" -> "Sun"
            "storm" -> "Storm"
            "rain" -> "Rain"
            else -> error("(CommandWeather;SyntaxPath) Unexpected value: ${commandName}")
        }
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("weather", true)) {
            handleWeatherCommand(commandSender, command, label, *arguments)
            return
        }

        if (commandSender is ConsoleUser && arguments.isEmpty()) {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        if (arguments.isNotEmpty() && !checkPermission(commandSender, "Weather.World")) return

        val world = (if (arguments.isNotEmpty()) Bukkit.getWorld(arguments[0]) else commandSender.getPlayer()!!.world) ?: run {
            commandSender.commandMsg("Weather.WorldNotFound") { target(arguments[0]) }
            return
        }

        handleWeatherCommand(commandSender, command, label, command.name, world.name)
    }

    private fun handleWeatherCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (commandSender is ConsoleUser && arguments.size == 1) {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        if (arguments.size >= 2 && !checkPermission(commandSender, "Weather.World")) return

        val world = (if (arguments.size >= 2) Bukkit.getWorld(arguments[1]) else commandSender.getPlayer()!!.world) ?: run {
            commandSender.commandMsg("Weather.WorldNotFound") { target(arguments[1]) }
            return
        }

        // Random duration between 300 and 900 seconds (5 to 15 minutes)
        val weatherDuration = (ThreadLocalRandom.current().nextInt(300, 900) + 1) * 20

        when (arguments[0].lowercase()) {
            "sun", "clear" -> {
                world.isThundering = false
                world.setStorm(false)
                world.clearWeatherDuration = weatherDuration
            }

            "storm", "thunder" -> {
                world.clearWeatherDuration = 0
                world.setStorm(true)
                world.isThundering = true
                world.weatherDuration = weatherDuration
            }

            "rain" -> {
                world.clearWeatherDuration = 0
                world.isThundering = false
                world.setStorm(true)
                world.weatherDuration = weatherDuration
            }

            else -> {
                commandSender.generalMsg("InvalidArguments") {
                    syntax(getSyntaxPath(command))
                    label(label)
                }
                return
            }
        }

        commandSender.commandMsg("Weather.Success") {
            target(world.name)
            postModifier {
                it.replace("<WEATHER>", arguments[0])
                    .replace("<WORLD>", world.name)
            }
        }
    }
}
