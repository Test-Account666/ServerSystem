package me.testaccount666.serversystem.commands.executables.gamemode

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.managers.messages.MappingsData.Companion.gameMode
import me.testaccount666.serversystem.userdata.User
import org.bukkit.GameMode
import org.bukkit.command.Command

/**
 * Command executor for the gamemode command.
 * This command allows players to switch game modes for themselves or other players.
 */
@ServerSystemCommand("gamemode", ["gms", "gmc", "gma", "gmsp"], TabCompleterGameMode::class)
class CommandGameMode : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command): Int {
        if (command.name.equals("gamemode", true)) return 1
        return 0
    }

    override fun getUsagePermission(command: Command) = "GameMode.Use"
    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "GameMode"
        val commandName = command.name.lowercase()
        return when (commandName) {
            "gms" -> "GMS"
            "gmc" -> "GMC"
            "gma" -> "GMA"
            "gmsp" -> "GMSP"
            else -> "GameMode"
        }
    }

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
        val gameMode = parseGameMode(arguments[0]) ?: run {
            commandSender.commandMsg("GameMode.InvalidGameMode") {
                postModifier { replaceGameModePlaceholder(it, arguments[0]) }
            }
            return
        }

        val newArguments = if (arguments.size > 1) arrayOf(arguments[1]) else arrayOf()

        handleGameModeCommand(commandSender, command, label, gameMode, *newArguments)
    }

    fun handleGameModeCommand(commandSender: User, command: Command?, label: String, gameMode: GameMode, vararg arguments: String) {
        val gameModePermission = when (gameMode) {
            GameMode.SURVIVAL -> "GameMode.Survival"
            GameMode.CREATIVE -> "GameMode.Creative"
            GameMode.ADVENTURE -> "GameMode.Adventure"
            GameMode.SPECTATOR -> "GameMode.Spectator"
        }

        if (!checkPermission(commandSender, gameModePermission)) return
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()
        val isSelf = targetPlayer === commandSender.getPlayer()

        targetPlayer!!.gameMode = gameMode

        val gameModeNameSender = gameMode(commandSender).getGameModeName(gameMode) ?: gameMode.name
        val messageKey = if (isSelf) "GameMode.Success" else "GameMode.SuccessOther"

        commandSender.commandMsg(messageKey) {
            target(targetPlayer.name)
            postModifier { replaceGameModePlaceholder(it, gameModeNameSender) }
        }

        if (isSelf) return
        val gameModeNameTarget = gameMode(targetUser).getGameModeName(gameMode) ?: gameMode.name

        targetUser.commandMsg("GameMode.Success") {
            sender(commandSender.nameSafe)
            target(targetPlayer.name)
            postModifier { replaceGameModePlaceholder(it, gameModeNameTarget) }
        }
    }

    private fun replaceGameModePlaceholder(message: String, gameModeName: String) = message.replace("<GAMEMODE>", gameModeName)

    private fun parseGameMode(input: String): GameMode? {
        input.toIntOrNull()?.let {
            return when (it) {
                0 -> GameMode.SURVIVAL
                1 -> GameMode.CREATIVE
                2 -> GameMode.ADVENTURE
                3 -> GameMode.SPECTATOR
                else -> null
            }
        }

        return GameMode.entries.firstOrNull { it.name.startsWith(input, true) }
    }
}
