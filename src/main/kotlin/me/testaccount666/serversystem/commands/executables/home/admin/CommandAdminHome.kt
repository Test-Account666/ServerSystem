package me.testaccount666.serversystem.commands.executables.home.admin

import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.Home.Companion.of
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

class CommandAdminHome : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val commandName = command.name.substring("admin".length)

        if (commandName.equals("sethome", true)) {
            handleSetHomeCommand(commandSender, command, label, *arguments)
            return
        }

        if (commandName.equals("deletehome", true)) {
            handleDeleteHomeCommand(commandSender, command, label, *arguments)
            return
        }

        handleHomeCommand(commandSender, command, label, *arguments)
    }

    private fun handleHomeCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "AdminHome.Use")) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.size <= 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val homeManager = targetUser.homeManager
        val home = homeManager.getHomeByName(arguments[1])

        if (home == null) {
            command("Home.DoesNotExist", targetUser) {
                target(targetUser.getNameOrNull())
                postModifier { it.replace("<HOME>", arguments[1]) }
            }.build()
            return
        }

        val homeLocation = home.location

        commandSender.getPlayer()!!.teleport(homeLocation)

        command("Home.Success", targetUser) {
            target(targetUser.getNameOrNull())
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    private fun handleDeleteHomeCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "AdminHome.Delete")) return

        if (arguments.size <= 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }


        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val homeManager = targetUser.homeManager
        val home = homeManager.getHomeByName(arguments[1])

        if (home == null) {
            general("Home.DoesNotExist", commandSender) {
                target(targetUser.getNameOrNull())
                postModifier { it.replace("<HOME>", arguments[1]) }
            }.build()
            return
        }

        homeManager.removeHome(home)

        general("DeleteHome.Success", commandSender) {
            target(targetUser.getNameOrNull())
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    private fun handleSetHomeCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "AdminHome.Set")) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.size <= 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val homeManager = targetUser.homeManager
        val maxHomes = homeManager.maxHomeCount

        if (maxHomes == null) {
            general("ErrorOccurred", targetUser) { label(label) }.build()
            return
        }

        val homeName: String = arguments[1]
        if (homeManager.hasHome(homeName)) {
            command("SetHome.AlreadyExists", targetUser) {
                label(label)
                target(targetUser.getNameOrNull())
                postModifier { it.replace("<HOME>", homeName) }
            }.build()
            return
        }

        val newHome = of(homeName, commandSender.getPlayer()!!.location)

        if (newHome == null) {
            command("SetHome.InvalidName", commandSender) {
                label(label)
                target(targetUser.getNameOrNull())
            }.build()
            return
        }

        homeManager.addHome(newHome)

        command("SetHome.Success", targetUser) {
            label(label)
            target(targetUser.getNameOrNull())
            postModifier { it.replace("<HOME>", newHome.displayName) }
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "AdminHome"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val commandName = command.name.substring("admin".length)

        if (commandName.equals("sethome", true)) return hasCommandPermission(player, "AdminHome.Set", false)

        if (commandName.equals("deletehome", true)) return hasCommandPermission(player, "AdminHome.Delete", false)

        return hasCommandPermission(player, "AdminHome.Use", false)
    }
}
