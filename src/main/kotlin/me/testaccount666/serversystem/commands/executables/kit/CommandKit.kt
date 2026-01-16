package me.testaccount666.serversystem.commands.executables.kit

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.kit.manager.Kit
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.DurationParser.parseDuration
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

@ServerSystemCommand("kit", ["createkit", "deletekit"], TabCompleterKit::class)
class CommandKit : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val permissionPath = getPermission(command)!!
        if (!checkBasePermission(commandSender, permissionPath)) return

        when (command.name.lowercase(getDefault())) {
            "createkit" -> handleCreateKit(commandSender, *arguments)
            "deletekit" -> handleDeleteKit(commandSender, *arguments)
            "kit" -> handleKit(commandSender, *arguments)
        }
    }

    private fun handleCreateKit(commandSender: User, vararg arguments: String) {
        val kitName = arguments[0].lowercase(getDefault())
        val kitManager = instance.registry.getService<KitManager>()
        val kit = kitManager.getKit(kitName)
        if (kit != null) {
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
        val kitName = arguments[0].lowercase(getDefault())
        val kitManager = instance.registry.getService<KitManager>()
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
        val kitName = arguments[0].lowercase(getDefault())
        val kitManager = instance.registry.getService<KitManager>()
        val kit = kitManager.getKit(kitName)
        if (kit == null) {
            command("Kit.KitNotFound", commandSender) {
                postModifier { it.replace("<KIT>", arguments[0]) }
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, 1, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }
        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Kit.Other", targetPlayer.name)) return

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

    private fun getPermission(command: Command): String? {
        return when (command.name.lowercase(getDefault())) {
            "createkit" -> "Kit.Create"
            "deletekit" -> "Kit.Delete"
            "kit" -> "Kit.Use"
            else -> null
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = getPermission(command)!!
        return PermissionManager.hasCommandPermission(player, permissionPath, false)
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Kit"
        return when (val commandName = command.name.lowercase(getDefault())) {
            "createkit" -> "CreateKit"
            "deletekit" -> "DeleteKit"
            "kit" -> "Kit"
            else -> error("(CommandKit;SyntaxPath) Unexpected value: ${commandName}")
        }
    }
}
