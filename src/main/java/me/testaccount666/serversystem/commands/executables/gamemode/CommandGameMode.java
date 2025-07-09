package me.testaccount666.serversystem.commands.executables.gamemode;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

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
            handleGameModeCommand(commandSender, GameMode.SURVIVAL, arguments, label);
            return;
        }

        if (command.getName().equalsIgnoreCase("gmc")) {
            handleGameModeCommand(commandSender, GameMode.CREATIVE, arguments, label);
            return;
        }

        if (command.getName().equalsIgnoreCase("gma")) {
            handleGameModeCommand(commandSender, GameMode.ADVENTURE, arguments, label);
            return;
        }

        if (command.getName().equalsIgnoreCase("gmsp")) {
            handleGameModeCommand(commandSender, GameMode.SPECTATOR, arguments, label);
            return;
        }

        // Handle /gamemode <Mode> <Target> command
        if (arguments.length < 1) {
            //TODO: Add syntax
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var gameModeOptional = parseGameMode(arguments[0]);
        if (gameModeOptional.isEmpty()) {
            sendCommandMessage(commandSender, "GameMode.InvalidGameMode", null, label,
                    message -> replaceGameModePlaceholder(message, arguments[0]));
            return;
        }

        var newArguments = arguments.length > 1? new String[]{arguments[1]} : new String[0];

        handleGameModeCommand(commandSender, gameModeOptional.get(), newArguments, label);
    }

    private void handleGameModeCommand(User commandSender, GameMode gameMode, String[] arguments, String label) {
        if (!checkBasePermission(commandSender, "GameMode.Use", label)) return;

        var gameModePermission = switch (gameMode) {
            case SURVIVAL -> "GameMode.Survival";
            case CREATIVE -> "GameMode.Creative";
            case ADVENTURE -> "GameMode.Adventure";
            case SPECTATOR -> "GameMode.Spectator";
        };

        if (!checkBasePermission(commandSender, gameModePermission, label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        targetPlayer.setGameMode(gameMode);

        var gameModeName = MappingsData.GameMode().getGameModeName(gameMode).orElse(gameMode.name());

        var isSelf = targetPlayer == commandSender.getPlayer();
        var messageKey = isSelf? "GameMode.Success" : "GameMode.SuccessOther";

        sendCommandMessage(commandSender, messageKey, targetPlayer.getName(), label, message -> replaceGameModePlaceholder(message, gameModeName));

        if (isSelf) return;

        sendCommandMessage(targetUser, "GameMode.Success", null, label, message -> replaceGameModePlaceholder(message, gameModeName));
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

        return Arrays.stream(GameMode.values()).filter(gameMode -> isGameModeMatch(gameMode, input)).findFirst();
    }

    private boolean isGameModeMatch(GameMode gameMode, String input) {
        if (gameMode.name().equalsIgnoreCase(input)) return true;

        return MappingsData.GameMode().getGameModeName(gameMode)
                .map(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .orElse(false);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "GameMode.Use", false);
    }
}
