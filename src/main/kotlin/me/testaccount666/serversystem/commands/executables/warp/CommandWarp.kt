package me.testaccount666.serversystem.commands.executables.warp

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

@ServerSystemCommand("warp", ["setwarp", "deletewarp"], TabCompleterWarp::class)
class CommandWarp : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        when (command.name.lowercase(getDefault())) {
            "warp" -> handleWarpCommand(commandSender, *arguments)
            "setwarp" -> handleSetWarpCommand(commandSender, *arguments)
            "deletewarp" -> handleDeleteWarpCommand(commandSender, *arguments)
        }
    }


    private fun handleWarpCommand(commandSender: User, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Warp.Use")) return
        val registry = instance.registry
        val warpManager = registry.getService<WarpManager>()

        val warp = warpManager.getWarpByName(arguments[0])
        if (warp == null) {
            command("Warp.WarpNotFound", commandSender) {
                postModifier { it.replace("<WARP>", arguments[0]) }
            }.build()
            return
        }
        val player = commandSender.getPlayer()!!

        playAnimation(player.location)
        player.teleport(warp.location)
        playAnimation(player.location)

        command("Warp.Success", commandSender) {
            postModifier { it.replace("<WARP>", warp.displayName) }
        }.build()
    }

    private fun handleSetWarpCommand(commandSender: User, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Warp.Set")) return

        val registry = instance.registry
        val warpManager = registry.getService<WarpManager>()
        val warpName = arguments[0]
        val warpLocation = commandSender.getPlayer()!!.location

        val existingWarp = warpManager.getWarpByName(warpName)

        if (existingWarp != null) {
            command("Warp.Set.WarpAlreadyExists", commandSender) {
                postModifier { it.replace("<WARP>", arguments[0]) }
            }.build()
            return
        }

        try {
            val warp = warpManager.addWarp(warpName, warpLocation)
            command("Warp.Set.Success", commandSender) {
                postModifier { it.replace("<WARP>", warp.displayName) }
            }.build()
        } catch (_: IllegalArgumentException) {
            command("Warp.Set.InvalidName", commandSender) {
                postModifier { it.replace("<WARP>", arguments[0]) }
            }.build()
        }
    }

    private fun handleDeleteWarpCommand(commandSender: User, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Warp.Delete")) return

        val registry = instance.registry
        val warpManager = registry.getService<WarpManager>()
        val warpName: String = arguments[0]
        val warp = warpManager.getWarpByName(warpName)

        if (warp == null) {
            command("Warp.WarpNotFound", commandSender) {
                postModifier { it.replace("<WARP>", warpName) }
            }.build()
            return
        }

        warpManager.removeWarp(warp)
        command("Warp.Delete.Success", commandSender) {
            postModifier { it.replace("<WARP>", warp.displayName) }
        }.build()
    }

    /**
     * Plays a teleportation animation effect at the given location
     *
     * @param location The location to play the animation at
     */
    private fun playAnimation(location: Location) {
        location.world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
        location.world.spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05)
    }

    override fun getSyntaxPath(command: Command?) = "Warp"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = when (command.name.lowercase(getDefault())) {
            "warp" -> "Warp.Use"
            "setwarp" -> "Warp.Set"
            "deletewarp" -> "Warp.Delete"
            else -> null
        } ?: return false

        return PermissionManager.hasCommandPermission(player, permissionPath, false)
    }
}
