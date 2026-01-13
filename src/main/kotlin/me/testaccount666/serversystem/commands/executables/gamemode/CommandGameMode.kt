package me.testaccount666.serversystem.commands.executables.gamemode

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.messages.MappingsData.Companion.gameMode
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

/**
 * Command executor for the gamemode command.
 * This command allows players to switch game modes for themselves or other players.
 */
@ServerSystemCommand("gamemode", ["gms", "gmc", "gma", "gmsp"], TabCompleterGameMode::class)
class CommandGameMode : AbstractServerSystemCommand() {
    /**
     * Executes the gamemode command and it's variants.
     * This method switches game modes for the target player if the sender has the required permissions.
     * If no target is specified, the sender is used as the target.
     * 
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command, where, depending on the command variant,
     * the first or second argument can be a target player name
     */
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("gms", true)) {
            handleGameModeCommand(commandSender, command, label, GameMode.SURVIVAL, *arguments)
            return
        }

        if (command.name.equals("gmc", true)) {
            handleGameModeCommand(commandSender, command, label, GameMode.CREATIVE, *arguments)
            return
        }

        if (command.name.equals("gma", true)) {
            handleGameModeCommand(commandSender, command, label, GameMode.ADVENTURE, *arguments)
            return
        }

        if (command.name.equals("gmsp", true)) {
            handleGameModeCommand(commandSender, command, label, GameMode.SPECTATOR, *arguments)
            return
        }

        // Handle /gamemode <Mode> <Target> command
        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val gameMode = parseGameMode(arguments[0])
        if (gameMode == null) {
            command("GameMode.InvalidGameMode", commandSender) {
                postModifier { replaceGameModePlaceholder(it, arguments[0]) }
            }.build()
            return
        }

        val newArguments = if (arguments.size > 1) arrayOf(arguments[1]) else arrayOf()

        handleGameModeCommand(commandSender, command, label, gameMode, *newArguments)
    }

    fun handleGameModeCommand(commandSender: User, command: Command?, label: String, gameMode: GameMode, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "GameMode.Use")) return

        val gameModePermission = when (gameMode) {
            GameMode.SURVIVAL -> "GameMode.Survival"
            GameMode.CREATIVE -> "GameMode.Creative"
            GameMode.ADVENTURE -> "GameMode.Adventure"
            GameMode.SPECTATOR -> "GameMode.Spectator"
        }

        if (!checkBasePermission(commandSender, gameModePermission)) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)

        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()
        val isSelf = targetPlayer === commandSender.getPlayer()

        targetPlayer!!.gameMode = gameMode

        val gameModeNameSender = gameMode(commandSender).getGameModeName(gameMode) ?: gameMode.name
        val messageKey = if (isSelf) "GameMode.Success" else "GameMode.SuccessOther"

        command(messageKey, commandSender) {
            target(targetPlayer.name)
            postModifier { replaceGameModePlaceholder(it, gameModeNameSender) }
        }.build()

        if (isSelf) return
        val gameModeNameTarget = gameMode(targetUser).getGameModeName(gameMode) ?: gameMode.name

        command("GameMode.Success", targetUser) {
            sender(commandSender.getNameSafe())
            target(targetPlayer.name)
            postModifier { replaceGameModePlaceholder(it, gameModeNameTarget) }
        }.build()
    }

    private fun replaceGameModePlaceholder(message: String, gameModeName: String): String {
        return message.replace("<GAMEMODE>", gameModeName)
    }

    private fun parseGameMode(input: String): GameMode? {
        try {
            val value = input.toInt()
            return when (value) {
                0 -> GameMode.SURVIVAL
                1 -> GameMode.CREATIVE
                2 -> GameMode.ADVENTURE
                3 -> GameMode.SPECTATOR
                else -> null
            }
        } catch (ignored: NumberFormatException) {
            // Not a number, try to match by name
        }

        return GameMode.entries
            .firstOrNull { gameMode -> gameMode.name.startsWith(input, true) }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "GameMode"
        val commandName = command.name.lowercase(getDefault())
        return when (commandName) {
            "gms" -> "GMS"
            "gmc" -> "GMC"
            "gma" -> "GMA"
            "gmsp" -> "GMSP"
            else -> "GameMode"
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "GameMode.Use", false)
    }
}
