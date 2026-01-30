package me.testaccount666.serversystem.commands.executables.serversystem

import me.testaccount666.migration.plugins.MigratorRegistry
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.serverVersion
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.updates.UpdateManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
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
            else -> general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
        }
    }

    fun version(commandSender: User, label: String) {
        if (!checkPermission(commandSender, "ServerSystem.Version")) return

        command("ServerSystem.Version.Checking", commandSender).build()

        val updateManager = getService<UpdateManager>()
        updateManager.updateChecker.getLatestVersion().thenAccept { latestVersion ->
            command("ServerSystem.Version.Success", commandSender) {
                prefix(false)
                postModifier { applyVersion(it, latestVersion.version) }
            }.build()
        }.exceptionally { throwable ->
            throwable.printStackTrace()
            general("ErrorOccurred", commandSender) { label(label) }.build()

            command("ServerSystem.Version.Success", commandSender) {
                prefix(false)
                postModifier { applyVersion(it, null) }
            }.build()
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

        val serverSystem = instance
        Bukkit.getScheduler().cancelTasks(serverSystem)
        serverSystem.onDisable()
        serverSystem.onEnable()

        if (!instance.isEnabled) {
            general("ErrorOccurred", commandSender).build()
            return
        }

        command("ServerSystem.Reload.Success", commandSender).build()
    }

    fun migrate(commandSender: User, label: String, vararg arguments: String) {
        if (!checkPermission(commandSender, "ServerSystem.Migrate")) return

        if (arguments.size <= 1) {
            general("InvalidArguments", commandSender) {
                label(label)
                syntax(getSyntaxPath(null))
            }.build()
            return
        }

        val migratorRegistry = getService<MigratorRegistry>()
        val migratorName = arguments[1]

        val migrator = migratorRegistry.getMigrator(migratorName) ?: run {
            command("ServerSystem.Migrate.NotFound", commandSender) {
                postModifier { it.replace("<MIGRATOR>", migratorName) }
            }.build()
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
                general("InvalidArguments", commandSender) {
                    label(label)
                    syntax(getSyntaxPath(null))
                }.build()
                return
            }
        }

        command("ServerSystem.Migrate.Success.${migrationType}", commandSender) {
            postModifier { it.replace("<MIGRATOR>", migratorName) }
        }.build()
    }
}
