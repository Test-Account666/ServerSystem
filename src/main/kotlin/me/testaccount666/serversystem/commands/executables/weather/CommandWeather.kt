package me.testaccount666.serversystem.commands.executables.weather

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
import java.util.concurrent.ThreadLocalRandom

@ServerSystemCommand("weather", ["sun", "storm", "rain"], TabCompleterWeather::class)
class CommandWeather : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("weather", true)) {
            handleWeatherCommand(commandSender, command, label, *arguments)
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
            if (!checkBasePermission(commandSender, "Weather.World")) return
            world = Bukkit.getWorld(arguments[0])
        } else world = commandSender.getPlayer()!!.world

        if (world == null) {
            command("Weather.WorldNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        handleWeatherCommand(commandSender, command, label, command.name, world.name)
    }

    private fun handleWeatherCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Weather.Use")) return

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
            if (!checkBasePermission(commandSender, "Weather.World")) return
            world = Bukkit.getWorld(arguments[1])
        } else world = commandSender.getPlayer()!!.world

        if (world == null) {
            command("Weather.WorldNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }

        val random = ThreadLocalRandom.current()

        // Random duration between 300 and 900 seconds (5 to 15 minutes)
        var weatherDuration = random.nextInt(300, 900) + 1
        // Convert to ticks
        weatherDuration *= 20

        when (arguments[0].lowercase(getDefault())) {
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
                general("InvalidArguments", commandSender) {
                    syntax(getSyntaxPath(command))
                    label(label)
                }.build()
                return
            }
        }

        command("Weather.Success", commandSender) {
            target(world.name)
            postModifier {
                it.replace("<WEATHER>", arguments[0])
                    .replace("<WORLD>", world.name)
            }
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Weather"

        return when (val commandName = command.name.lowercase(getDefault())) {
            "weather" -> "Weather"
            "sun" -> "Sun"
            "storm" -> "Storm"
            "rain" -> "Rain"
            else -> error("(CommandWeather;SyntaxPath) Unexpected value: ${commandName}")
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Weather.Use", false)
    }
}
