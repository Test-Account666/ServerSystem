package me.testaccount666.serversystem.commands.executables.home

import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.home.Home
import me.testaccount666.serversystem.userdata.home.HomeManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import me.testaccount666.serversystem.utils.tuples.Tuple
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.command.Command

abstract class AbstractCommandHome : AbstractServerSystemCommand() {

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, getBasePermission(command))) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.size < argsBeforeHome(command) + 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val (target, homeName) = resolveTargetAndHome(commandSender, command, *arguments) ?: return
        val homeManager = target.homeManager

        when (getHomeType(command)) {
            HomeType.SET -> handleSet(commandSender, target, homeManager, homeName)
            HomeType.DELETE -> handleDelete(commandSender, target, homeManager, homeName)
            HomeType.TELEPORT -> handleTeleport(commandSender, target, homeManager, homeName)
        }
    }

    private fun resolveTargetAndHome(commandSender: User, command: Command, vararg arguments: String): Tuple<User, String>? {
        val target = fetchTarget(commandSender, command, *arguments) ?: return null

        val homeName = arguments[argsBeforeHome(command)]
        return Tuple(target, homeName)
    }

    fun handleSet(commandSender: User, target: User, homeManager: HomeManager, homeName: String) {
        if (homeManager.hasHome(homeName)) {
            command("SetHome.AlreadyExists", commandSender) {
                target(target.getNameSafe())
                postModifier { it.replace("<HOME>", homeName) }
            }.build()
            return
        }

        val location = commandSender.getPlayer()!!.location
        val home = Home.of(homeName, location)

        if (home == null) {
            command("SetHome.InvalidName", commandSender) {
                target(target.getNameSafe())
            }.build()
            return
        }

        homeManager.addHome(home)

        command("SetHome.Success", commandSender) {
            target(target.getNameOrNull())
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    fun handleDelete(commandSender: User, target: User, homeManager: HomeManager, homeName: String) {
        val home = getHome(commandSender, target, homeManager, homeName) ?: return

        homeManager.removeHome(home)

        command("DeleteHome.Success", commandSender) {
            target(target.getNameSafe())
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    fun handleTeleport(commandSender: User, target: User, homeManager: HomeManager, homeName: String) {
        val home = getHome(commandSender, target, homeManager, homeName) ?: return
        val homeLocation = home.location
        val player = commandSender.getPlayer()!!

        playAnimation(player.location)
        player.teleport(homeLocation)
        playAnimation(homeLocation)

        command("Home.Success", commandSender) {
            target(target.getNameSafe())
            postModifier { it.replace("<HOME>", home.displayName) }
        }.build()
    }

    private fun getHome(commandSender: User, target: User, homeManager: HomeManager, homeName: String): Home? {
        val home = homeManager.getHomeByName(homeName)

        if (home == null) {
            command("Home.DoesNotExist", commandSender) {
                target(target.getNameSafe())
                postModifier { it.replace("<HOME>", homeName) }
            }.build()
            return null
        }

        return home
    }

    private fun fetchTarget(commandSender: User, command: Command, vararg arguments: String): User? {
        if (argsBeforeHome(command) == 0) return commandSender

        val index = argsBeforeHome(command) - 1

        val target = getTargetUser(commandSender, index, false, *arguments)
        if (target == null) general("PlayerNotFound", commandSender) { target(arguments[index]) }.build()

        return target
    }

    abstract fun getBasePermission(command: Command): String
    abstract fun argsBeforeHome(command: Command): Int
    abstract fun getHomeType(command: Command): HomeType

    /**
     * Plays a teleportation animation effect at the given location
     *
     * @param location The location to play the animation at
     */
    private fun playAnimation(location: Location) {
        location.world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
        location.world.spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05)
    }
}

enum class HomeType { SET, DELETE, TELEPORT }
