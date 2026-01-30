package me.testaccount666.serversystem.commands.executables.kit

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.kit.manager.Kit
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.DurationParser.parseDuration
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

@ServerSystemCommand("kit", ["createkit", "deletekit"], TabCompleterKit::class)
class CommandKit : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command): String {
        return when (command.name.lowercase()) {
            "createkit" -> "Kit.Create"
            "deletekit" -> "Kit.Delete"
            "kit" -> "Kit.Use"
            else -> error("(CommandKit;getUsagePermission) Unexpected value: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Kit"
        return when (val commandName = command.name.lowercase()) {
            "createkit" -> "CreateKit"
            "deletekit" -> "DeleteKit"
            "kit" -> "Kit"
            else -> error("(CommandKit;SyntaxPath) Unexpected value: ${commandName}")
        }
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        when (command.name.lowercase()) {
            "createkit" -> handleCreateKit(commandSender, *arguments)
            "deletekit" -> handleDeleteKit(commandSender, *arguments)
            "kit" -> handleKit(commandSender, *arguments)
        }
    }

    private fun handleCreateKit(commandSender: User, vararg arguments: String) {
        val kitName = arguments[0].lowercase()
        val kitManager = getService<KitManager>()
        kitManager.getKit(kitName)?.also { kit ->
            command("Kit.Create.KitAlreadyExists", commandSender) {
                postModifier { it.replace("<KIT>", kit.displayName) }
            }.build()
            return
        }

        var cooldown = -1L

        if (arguments.size > 1) cooldown = parseDuration(arguments[1])
        if (cooldown == -2L) {
            command("Kit.Create.InvalidCooldown", commandSender).build()
            return
        }

        val player = commandSender.getPlayer()
        val inventory = player!!.inventory
        val contents = inventory.contents

        val newKit = Kit(kitName, cooldown, contents)
        kitManager.addKit(newKit)
        kitManager.saveAllKits()
        command("Kit.Create.Success", commandSender) {
            postModifier { it.replace("<KIT>", newKit.displayName) }
        }.build()
    }

    private fun handleDeleteKit(commandSender: User, vararg arguments: String) {
        val kitName = arguments[0].lowercase()
        val kitManager = getService<KitManager>()
        if (!kitManager.kitExists(kitName)) {
            command("Kit.KitNotFound", commandSender) {
                postModifier { it.replace("<KIT>", arguments[0]) }
            }.build()
            return
        }

        kitManager.removeKit(kitName)
        command("Kit.Delete.Success", commandSender) {
            postModifier { it.replace("<KIT>", arguments[0]) }
        }.build()
    }

    private fun handleKit(commandSender: User, vararg arguments: String) {
        val kitName = arguments[0].lowercase()
        val kitManager = getService<KitManager>()
        val kit = kitManager.getKit(kitName) ?: run {
            command("Kit.KitNotFound", commandSender) {
                postModifier { it.replace("<KIT>", arguments[0]) }
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, 1, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }
        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Kit.Other", targetPlayer.name)) return

        if (isSelf && commandSender.isOnKitCooldown(kitName)) {
            val cooldown = commandSender.getKitCooldown(kitName)

            command("Kit.OnCooldown", commandSender) {
                postModifier {
                    it.replace("<KIT>", kit.displayName)
                        .replace("<DATE>", parseDate(cooldown, commandSender))
                }
            }.build()
            return
        }

        if (isSelf) commandSender.setKitCooldown(kitName, kit.coolDown)
        kit.giveKit(targetPlayer)

        val messagePath = "Kit.Success." + (if (isSelf) "Self" else "Other")
        command(messagePath, commandSender) {
            target(targetPlayer.name)
            postModifier { it.replace("<KIT>", kit.displayName) }
        }.build()
    }
}
