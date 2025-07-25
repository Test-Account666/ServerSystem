package me.testaccount666.serversystem.commands.executables.gamemode;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.messages.MappingsData;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

/**
 * Command executor for the gamemode command.
 * This command allows players to switch game modes for themselves or other players.
 */
@ServerSystemCommand(name = "gamemode", variants = {"gms", "gmc", "gma", "gmsp"}, tabCompleter = TabCompleterGameMode.class)
public class CommandGameMode extends AbstractServerSystemCommand {

    /**
     * Executes the gamemode command and it's variants.
     * This method switches game modes for the target player if the sender has the required permissions.
     * If no target is specified, the sender is used as the target.
     *
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command, where, depending on the command variant,
     *                      the first or second argument can be a target player name
     */
    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("gms")) {
            handleGameModeCommand(commandSender, command, label, GameMode.SURVIVAL, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("gmc")) {
            handleGameModeCommand(commandSender, command, label, GameMode.CREATIVE, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("gma")) {
            handleGameModeCommand(commandSender, command, label, GameMode.ADVENTURE, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("gmsp")) {
            handleGameModeCommand(commandSender, command, label, GameMode.SPECTATOR, arguments);
            return;
        }

        // Handle /gamemode <Mode> <Target> command
        if (arguments.length < 1) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var gameModeOptional = parseGameMode(arguments[0]);
        if (gameModeOptional.isEmpty()) {
            command("GameMode.InvalidGameMode", commandSender)
                    .postModifier(message -> replaceGameModePlaceholder(message, arguments[0])).build();
            return;
        }

        var newArguments = arguments.length > 1? new String[]{arguments[1]} : new String[0];

        handleGameModeCommand(commandSender, command, label, gameModeOptional.get(), newArguments);
    }

    public void handleGameModeCommand(User commandSender, Command command, String label, GameMode gameMode, String[] arguments) {
        if (!checkBasePermission(commandSender, "GameMode.Use")) return;

        var gameModePermission = switch (gameMode) {
            case SURVIVAL -> "GameMode.Survival";
            case CREATIVE -> "GameMode.Creative";
            case ADVENTURE -> "GameMode.Adventure";
            case SPECTATOR -> "GameMode.Spectator";
        };

        if (!checkBasePermission(commandSender, gameModePermission)) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetPlayer == commandSender.getPlayer();

        targetPlayer.setGameMode(gameMode);

        var gameModeNameSender = MappingsData.gameMode(commandSender).getGameModeName(gameMode).orElse(gameMode.name());
        var messageKey = isSelf? "GameMode.Success" : "GameMode.SuccessOther";

        command(messageKey, commandSender).target(targetPlayer.getName())
                .postModifier(message -> replaceGameModePlaceholder(message, gameModeNameSender)).build();

        if (isSelf) return;
        var gameModeNameTarget = MappingsData.gameMode(targetUser).getGameModeName(gameMode).orElse(gameMode.name());

        command("GameMode.Success", targetUser)
                .sender(commandSender.getName().get()).target(targetPlayer.getName())
                .postModifier(message -> replaceGameModePlaceholder(message, gameModeNameTarget)).build();
    }

    private String replaceGameModePlaceholder(String message, String gameModeName) {
        return message.replace("<GAMEMODE>", gameModeName);
    }

    private Optional<GameMode> parseGameMode(String input) {
        try {
            var value = Integer.parseInt(input);
            return switch (value) {
                case 0 -> Optional.of(GameMode.SURVIVAL);
                case 1 -> Optional.of(GameMode.CREATIVE);
                case 2 -> Optional.of(GameMode.ADVENTURE);
                case 3 -> Optional.of(GameMode.SPECTATOR);
                default -> Optional.empty();
            };
        } catch (NumberFormatException ignored) {
            // Not a number, try to match by name
        }

        return Arrays.stream(GameMode.values()).filter(gameMode -> gameMode.name().toLowerCase().startsWith(input.toLowerCase())).findFirst();
    }

    @Override
    public String getSyntaxPath(Command command) {
        if (command == null) return "GameMode";
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "gms" -> "GMS";
            case "gmc" -> "GMC";
            case "gma" -> "GMA";
            case "gmsp" -> "GMSP";
            default -> "GameMode";
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "GameMode.Use", false);
    }
}
