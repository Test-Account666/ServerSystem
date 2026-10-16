package me.testaccount666.serversystem.commands.executables.serversystem

import me.testaccount666.migration.plugins.MigratorRegistry
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.serverVersion
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.updates.UpdateManager
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("serversystem", [], TabCompleterServerSystem::class)
class CommandServerSystem : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "ServerSystem.Use"
    override fun getSyntaxPath(command: Command?) = "ServerSystem"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        when (arguments[0].lowercase()) {
            "version" -> version(commandSender, label)
            "reload" -> reload(commandSender)
            "migrate" -> migrate(commandSender, label, *arguments.drop(1).toTypedArray())
            else -> commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
        }
    }

    fun version(commandSender: User, label: String) {
        if (!checkPermission(commandSender, "ServerSystem.Version")) return

        commandSender.commandMsg("ServerSystem.Version.Checking")

        getService<UpdateManager>().updateChecker.getLatestVersion().thenAccept { latestVersion ->
            commandSender.commandMsg("ServerSystem.Version.Success") {
                prefix(false)
                postModifier { applyVersion(it, latestVersion.version) }
            }
        }.exceptionally { throwable ->
            throwable.printStackTrace()
            commandSender.generalMsg("ErrorOccurred") { label(label) }

            commandSender.commandMsg("ServerSystem.Version.Success") {
                prefix(false)
                postModifier { applyVersion(it, null) }
            }
            null
        }
    }

    private fun applyVersion(message: String, latestVersion: String?): String {
        val latestVersion = latestVersion ?: "?"

        val currentVersion = instance.description.version
        val serverVersion = serverVersion.version

        return message.replace("<LATEST_VERSION>", latestVersion)
            .replace("<CURRENT_VERSION>", currentVersion)
            .replace("<SERVER_VERSION>", serverVersion)
    }

    fun reload(commandSender: User) {
        if (!checkPermission(commandSender, "ServerSystem.Reload")) return

        runCatching {
            with(instance) {
                Bukkit.getScheduler().cancelTasks(this)
                onDisable()
                onEnable()
            }
        }.onFailure { it.printStackTrace() }

        if (!instance.isEnabled) {
            commandSender.generalMsg("ErrorOccurred")
            return
        }

        commandSender.commandMsg("ServerSystem.Reload.Success")
    }

    fun migrate(commandSender: User, label: String, vararg arguments: String) {
        if (!checkPermission(commandSender, "ServerSystem.Migrate")) return

        if (arguments.size <= 1) {
            commandSender.generalMsg("InvalidArguments") {
                label(label)
                syntax(getSyntaxPath(null))
            }
            return
        }

        val migratorRegistry = getService<MigratorRegistry>()
        val migratorName = arguments[1]

        val migrator = migratorRegistry.getMigrator(migratorName) ?: run {
            commandSender.commandMsg("ServerSystem.Migrate.NotFound") {
                postModifier { it.replace("<MIGRATOR>", migratorName) }
            }
            return
        }

        var migrationType = arguments[0].lowercase()
        when (migrationType) {
            "to" -> {
                migrator.migrateTo()
                migrationType = "To"
            }

            "from" -> {
                migrator.migrateFrom()
                migrationType = "From"
            }

            else -> {
                commandSender.generalMsg("InvalidArguments") {
                    label(label)
                    syntax(getSyntaxPath(null))
                }
                return
            }
        }

        commandSender.commandMsg("ServerSystem.Migrate.Success.${migrationType}") {
            postModifier { it.replace("<MIGRATOR>", migratorName) }
        }
    }
}
