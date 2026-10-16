package me.testaccount666.serversystem.commands.executables.kit

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.kit.manager.Kit
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.DurationParser.parseDuration
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
        val kitName = arguments[0]
        val kitManager = getService<KitManager>()
        kitManager.getKit(kitName)?.also { kit ->
            commandSender.commandMsg("Kit.Create.KitAlreadyExists") {
                postModifier { it.replace("<KIT>", kit.displayName) }
            }
            return
        }

        val cooldown = if (arguments.size > 1) parseDuration(arguments[1]) else -1L
        if (cooldown == -2L) {
            commandSender.commandMsg("Kit.Create.InvalidCooldown")
            return
        }

        val inventory = commandSender.getPlayer()!!.inventory

        val newKit = Kit(kitName, cooldown, inventory.contents)
        kitManager.addKit(newKit)
        kitManager.saveAllKits()
        commandSender.commandMsg("Kit.Create.Success") {
            postModifier { it.replace("<KIT>", newKit.displayName) }
        }
    }

    private fun handleDeleteKit(commandSender: User, vararg arguments: String) {
        val kitName = arguments[0]
        val kitManager = getService<KitManager>()
        if (!kitManager.kitExists(kitName)) {
            commandSender.commandMsg("Kit.KitNotFound") {
                postModifier { it.replace("<KIT>", arguments[0]) }
            }
            return
        }

        kitManager.removeKit(kitName)
        commandSender.commandMsg("Kit.Delete.Success") {
            postModifier { it.replace("<KIT>", arguments[0]) }
        }
    }

    private fun handleKit(commandSender: User, vararg arguments: String) {
        val kitName = arguments[0]
        val kitManager = getService<KitManager>()
        val kit = kitManager.getKit(kitName) ?: run {
            commandSender.commandMsg("Kit.KitNotFound") {
                postModifier { it.replace("<KIT>", arguments[0]) }
            }
            return
        }

        val targetUser = getTargetUser(commandSender, 1, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[1]) }
            return
        }
        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Kit.Other", targetPlayer.name)) return

        if (isSelf && commandSender.isOnKitCooldown(kitName)) {
            val cooldown = commandSender.getKitCooldown(kitName)

            commandSender.commandMsg("Kit.OnCooldown") {
                postModifier {
                    it.replace("<KIT>", kit.displayName)
                        .replace("<DATE>", parseDate(cooldown, commandSender))
                }
            }
            return
        }

        if (isSelf) targetUser.setKitCooldown(kitName, kit.coolDown).also { targetUser.save() }
        kit.giveKit(targetPlayer)

        val messagePath = "Kit.Success." + (if (isSelf) "Self" else "Other")
        commandSender.commandMsg(messagePath) {
            target(targetPlayer.name)
            postModifier { it.replace("<KIT>", kit.displayName) }
        }
    }
}
