package me.testaccount666.serversystem.commands.executables.spawn

import me.testaccount666.paperktx.extensions.location
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.teleport.TeleportRunnable.Companion.teleportSmart
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException
import java.util.logging.Level

@ServerSystemCommand("spawn", ["setspawn"])
open class CommandSpawn : AbstractServerSystemCommand {
    override fun getUsagePermission(command: Command): String {
        if (command.name.equals("spawn", true)) return "Spawn.Use"
        return "Spawn.Set"
    }

    protected val spawnConfiguration: FileConfiguration
    val teleportOnJoin: Boolean
    val teleportOnFirstJoin: Boolean

    private val _spawnFile = instance.dataPath.resolve("data").resolve("spawn.yml").toFile()
    protected var spawnLocation: Location? = null
    override fun getSyntaxPath(command: Command?): String {
        command ?: return "Spawn"
        if (command.name.equals("spawn", true)) return "Spawn"
        return "Generic"
    }

    constructor() {
        val legacySpawnFile = instance.dataPath.resolve("spawn.yml").toFile()
        if (legacySpawnFile.exists()) {
            legacySpawnFile.renameTo(_spawnFile)
            log.info("Found 'spawn.yml' in wrong directory. It was moved to '${_spawnFile.absolutePath}'.")
        }

        spawnConfiguration = YamlConfiguration.loadConfiguration(_spawnFile)

        saveDefaultConfig()
        val config = getService<ConfigurationManager>().generalConfig

        teleportOnJoin = config.getBoolean("Join.Spawn.TeleportOnJoin")
        teleportOnFirstJoin = config.getBoolean("Join.Spawn.TeleportOnFirstJoin")

        if (!spawnConfiguration.isSet("Spawn")) return

        val worldName = spawnConfiguration.getString("Spawn.World") ?: return
        val world = Bukkit.getWorld(worldName) ?: return

        val x = spawnConfiguration.getDouble("Spawn.X")
        val y = spawnConfiguration.getDouble("Spawn.Y")
        val z = spawnConfiguration.getDouble("Spawn.Z")
        val yaw = spawnConfiguration.getDouble("Spawn.Yaw")
        val pitch = spawnConfiguration.getDouble("Spawn.Pitch")

        spawnLocation = location(x, y, z, world, yaw, pitch)
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("spawn", true)) {
            handleSpawnCommand(commandSender, label, true, *arguments)
            return
        }

        handleSetSpawnCommand(commandSender, label)
    }

    fun handleSpawnCommand(commandSender: User, label: String, fromCommand: Boolean, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(null), label, arguments = arguments)) return

        val spawnLocation = spawnLocation ?: run {
            commandSender.commandMsg("Spawn.NoSpawnSet")
            return
        }

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Spawn.Other", targetUser.nameSafe)) return
        val instantTeleport = !fromCommand || !isSelf || hasCommandPermission(commandSender, "Spawn.InstantTeleport", false)

        targetUser.teleportSmart(
            spawnLocation,
            instantTeleport,
            { commandSender.commandMsg("Spawn.Teleporting") { target(targetUser.nameSafe) } },
            { sendSuccessMessage(commandSender, targetUser, isSelf) },
            { commandSender.commandMsg("Spawn.Moved") }
        )
    }

    private fun sendSuccessMessage(commandSender: User, targetUser: User, isSelf: Boolean) {
        val messagePath = if (isSelf) "Spawn.Success" else "Spawn.SuccessOther"
        commandSender.commandMsg(messagePath) { target(targetUser.nameSafe) }

        if (isSelf) return
        targetUser.commandMsg("Spawn.Success") { sender(commandSender.nameSafe) }
    }

    private fun handleSetSpawnCommand(commandSender: User, label: String) {
        if (!isPlayer(commandSender)) return

        val currentLocation = commandSender.getPlayer()!!.location

        spawnConfiguration["Spawn.World"] = currentLocation.world.name
        spawnConfiguration["Spawn.X"] = currentLocation.x
        spawnConfiguration["Spawn.Y"] = currentLocation.y
        spawnConfiguration["Spawn.Z"] = currentLocation.z
        spawnConfiguration["Spawn.Yaw"] = currentLocation.yaw
        spawnConfiguration["Spawn.Pitch"] = currentLocation.pitch

        try {
            spawnConfiguration.save(_spawnFile)
        } catch (exception: IOException) {
            commandSender.generalMsg("ErrorOccurred") { label(label) }
            log.log(Level.SEVERE, "Error while saving 'spawn.yml'", exception)
            return
        }

        spawnLocation = currentLocation

        commandSender.commandMsg("SetSpawn.Success")
    }

    private fun saveDefaultConfig() {
        if (_spawnFile.exists()) return

        try {
            _spawnFile.createNewFile()
        } catch (exception: IOException) {
            throw RuntimeException("Error while trying to create 'spawn.yml' file", exception)
        }

        spawnConfiguration["Config.TeleportOnJoin"] = true
        spawnConfiguration["Config.TeleportOnFirstJoin"] = true

        try {
            spawnConfiguration.save(_spawnFile)
        } catch (exception: IOException) {
            throw RuntimeException("Error while trying to spawn 'spawn.yml'", exception)
        }
    }
}
