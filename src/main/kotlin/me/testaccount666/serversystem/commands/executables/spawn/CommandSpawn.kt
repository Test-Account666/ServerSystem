package me.testaccount666.serversystem.commands.executables.spawn

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.logging.Level

@ServerSystemCommand("spawn", ["setspawn"])
open class CommandSpawn : AbstractServerSystemCommand {
    protected val spawnConfiguration: FileConfiguration
    val teleportOnJoin: Boolean
    val teleportOnFirstJoin: Boolean

    private val _spawnFile: File = Path.of("plugins", "ServerSystem", "data", "spawn.yml").toFile()
    protected var spawnLocation: Location? = null

    constructor() {
        val legacySpawnFile = Path.of("plugins", "ServerSystem", "spawn.yml")
        if (legacySpawnFile.toFile().exists()) {
            legacySpawnFile.toFile().renameTo(_spawnFile)
            log.info("Found 'spawn.yml' in wrong directory. It was moved to '${_spawnFile.absolutePath}'.")
        }

        spawnConfiguration = YamlConfiguration.loadConfiguration(_spawnFile)

        saveDefaultConfig()
        val configManager = instance.registry.getService(ConfigurationManager::class.java)
        val config = configManager.generalConfig

        teleportOnJoin = config.getBoolean("Join.Spawn.TeleportOnJoin", false)
        teleportOnFirstJoin = config.getBoolean("Join.Spawn.TeleportOnFirstJoin", false)

        if (!spawnConfiguration.isSet("Spawn")) return

        val worldName = spawnConfiguration.getString("Spawn.World") ?: return

        val world = Bukkit.getWorld(worldName) ?: return

        val x = spawnConfiguration.getDouble("Spawn.X")
        val y = spawnConfiguration.getDouble("Spawn.Y")
        val z = spawnConfiguration.getDouble("Spawn.Z")
        val yaw = spawnConfiguration.getDouble("Spawn.Yaw").toFloat()
        val pitch = spawnConfiguration.getDouble("Spawn.Pitch").toFloat()

        spawnLocation = Location(world, x, y, z, yaw, pitch)
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("spawn", true)) {
            handleSpawnCommand(commandSender, label, *arguments)
            return
        }

        handleSetSpawnCommand(commandSender, label)
    }

    fun handleSpawnCommand(commandSender: User, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Spawn.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(null), label, arguments = arguments)) return

        if (spawnLocation == null) {
            command("Spawn.NoSpawnSet", commandSender).build()
            return
        }

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Spawn.Other", targetPlayer.name)) return

        targetPlayer.teleport(spawnLocation!!)

        val messagePath = if (isSelf) "Spawn.Success" else "Spawn.SuccessOther"
        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("Spawn.Success", targetUser) { sender(commandSender.getNameSafe()) }.build()
    }

    private fun handleSetSpawnCommand(commandSender: User, label: String) {
        if (!checkBasePermission(commandSender, "Spawn.Set")) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val currentLocation = commandSender.getPlayer()!!.location

        spawnConfiguration.set("Spawn.World", currentLocation.world.name)
        spawnConfiguration.set("Spawn.X", currentLocation.x)
        spawnConfiguration.set("Spawn.Y", currentLocation.y)
        spawnConfiguration.set("Spawn.Z", currentLocation.z)
        spawnConfiguration.set("Spawn.Yaw", currentLocation.yaw)
        spawnConfiguration.set("Spawn.Pitch", currentLocation.pitch)

        try {
            spawnConfiguration.save(_spawnFile)
        } catch (exception: IOException) {
            general("ErrorOccurred", commandSender) { label(label) }.build()
            log.log(Level.SEVERE, "Error while saving 'spawn.yml'", exception)
            return
        }

        spawnLocation = currentLocation

        command("SetSpawn.Success", commandSender).build()
    }

    private fun saveDefaultConfig() {
        if (_spawnFile.exists()) return

        try {
            _spawnFile.createNewFile()
        } catch (exception: IOException) {
            throw RuntimeException("Error while trying to create 'spawn.yml' file", exception)
        }

        spawnConfiguration.set("Config.TeleportOnJoin", true)
        spawnConfiguration.set("Config.TeleportOnFirstJoin", true)

        try {
            spawnConfiguration.save(_spawnFile)
        } catch (exception: IOException) {
            throw RuntimeException("Error while trying to spawn 'spawn.yml'", exception)
        }
    }

    override fun getSyntaxPath(command: Command?) = "Spawn"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        if (command.name.equals("spawn", true)) return hasCommandPermission(player, "Spawn.Use", false)

        return hasCommandPermission(player, "Spawn.Set", false)
    }
}
