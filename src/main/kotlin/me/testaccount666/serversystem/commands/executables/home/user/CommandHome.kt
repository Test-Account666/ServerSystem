package me.testaccount666.serversystem.commands.executables.home.user

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.home.admin.CommandAdminHome
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.Home.Companion.of
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.*

@ServerSystemCommand(
    "home",
    ["sethome", "deletehome", "adminhome", "adminsethome", "admindeletehome"],
    TabCompleterHome::class
)
class CommandHome : AbstractServerSystemCommand() {
    private val _commandAdminHome = CommandAdminHome()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.lowercase(Locale.getDefault()).startsWith("admin")) {
            _commandAdminHome.execute(commandSender, command, label, *arguments)
            return
        }

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (command.name.equals("sethome", true)) {
            handleSetHomeCommand(commandSender, label, *arguments)
            return
        }

        if (command.name.equals("deletehome", true)) {
            handleDeleteHomeCommand(commandSender, label, *arguments)
            return
        }

        handleHomeCommand(commandSender, label, *arguments)
    }

    private fun handleHomeCommand(commandSender: User, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Home.Use")) return

        if (arguments.isEmpty()) {
            //TODO: List homes instead
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(null))
                label(label)
            }.build()
            return
        }

        val homeManager = commandSender.homeManager
        val home = homeManager.getHomeByName(arguments[0])

        if (home == null) {
            command("Home.DoesNotExist", commandSender) {
                postModifier { it.replace("<HOME>", arguments[0]) }
            }.build()
            return
        }

        val homeLocation = home.location

        commandSender.getPlayer()!!.teleport(homeLocation)

        command("Home.Success", commandSender) {
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    private fun handleDeleteHomeCommand(commandSender: User, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Home.Delete")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(null))
                label(label)
            }.build()
            return
        }

        val homeManager = commandSender.homeManager
        val home = homeManager.getHomeByName(arguments[0])

        if (home == null) {
            command("Home.DoesNotExist", commandSender) {
                postModifier { it.replace("<HOME>", arguments[0]) }
            }.build()
            return
        }

        homeManager.removeHome(home)

        command("DeleteHome.Success", commandSender) {
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    private fun handleSetHomeCommand(commandSender: User, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Home.Set")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(null))
                label(label)
            }.build()
            return
        }

        val homeManager = commandSender.homeManager
        val maxHomes = homeManager.maxHomeCount

        if (maxHomes == null) {
            general("ErrorOccurred", commandSender) { label(label) }.build()
            return
        }

        val currentHomeCount = homeManager.homes.size

        if (maxHomes <= currentHomeCount) {
            command("SetHome.MaxHomes", commandSender) {
                postModifier {
                    it.replace("<MAX_HOMES>", maxHomes.toString())
                        .replace("<CURRENT_HOMES>", currentHomeCount.toString())
                }
            }.build()
            return
        }

        val homeName: String = arguments[0]

        if (homeManager.hasHome(homeName)) {
            command("SetHome.AlreadyExists", commandSender) {
                postModifier { it.replace("<HOME>", homeName) }
            }.build()
            return
        }

        val newHome = of(homeName, commandSender.getPlayer()!!.location)

        if (newHome == null) {
            command("SetHome.InvalidName", commandSender).build()
            return
        }

        homeManager.addHome(newHome)

        command("SetHome.Success", commandSender) {
            postModifier { it.replace("<HOME>", newHome.displayName) }
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String = "Home"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        if (command.name.lowercase(Locale.getDefault()).startsWith("admin")) return _commandAdminHome.hasCommandAccess(player, command)

        if (command.name.equals("sethome", true)) return hasCommandPermission(player, "Home.Set", false)

        if (command.name.equals("deletehome", true)) return hasCommandPermission(player, "Home.Delete", false)

        return hasCommandPermission(player, "Home.Use", false)
    }
}
