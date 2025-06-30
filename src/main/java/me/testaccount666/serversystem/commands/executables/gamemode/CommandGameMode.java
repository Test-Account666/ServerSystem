package me.testaccount666.serversystem.commands.executables.gamemode;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

@ServerSystemCommand(name = "gamemode", variants = {"gms", "gmc", "gma", "gmsp"}, tabCompleter = TabCompleterGameMode.class)
public class CommandGameMode implements ServerSystemCommandExecutor {

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
            MessageManager.sendMessage(commandSender, "General.InvalidArguments", null, label);
            return;
        }

        var gameModeOptional = parseGameMode(arguments[0]);
        if (gameModeOptional.isEmpty()) {
            MessageManager.sendCommandMessage(commandSender, "GameMode.InvalidGameMode");
            return;
        }

        handleGameModeCommand(commandSender, gameModeOptional.get(), arguments.length > 1? new String[]{arguments[1]} : new String[0], label);
    }

    private void handleGameModeCommand(User commandSender, GameMode gameMode, String[] arguments, String label) {
        if (!PermissionManager.hasCommandPermission(commandSender.getCommandSender(), "GameMode.Use")) {
            MessageManager.sendCommandMessage(commandSender, "GameMode.NoPermission");
            return;
        }

        var gameModePermission = switch (gameMode) {
            case SURVIVAL -> "GameMode.Survival";
            case CREATIVE -> "GameMode.Creative";
            case ADVENTURE -> "GameMode.Adventure";
            case SPECTATOR -> "GameMode.Spectator";
        };

        if (!PermissionManager.hasCommandPermission(commandSender.getCommandSender(), gameModePermission)) {
            MessageManager.sendCommandMessage(commandSender, "GameMode.NoPermission");
            return;
        }

        Player targetPlayer;

        if (arguments.length == 0) {
            if (commandSender instanceof ConsoleUser) {
                MessageManager.sendCommandMessage(commandSender, "GameMode.NotPlayer");
                return;
            }

            targetPlayer = commandSender.getPlayer();
        } else {
            if (!PermissionManager.hasCommandPermission(commandSender.getCommandSender(), "GameMode.Other")) {
                MessageManager.sendCommandMessage(commandSender, "GameMode.NoPermissionOther");
                return;
            }

            targetPlayer = Bukkit.getPlayer(arguments[0]);
            if (targetPlayer == null) {
                MessageManager.sendMessage(commandSender, "General.PlayerNotFound", arguments[0], label);
                return;
            }
        }

        targetPlayer.setGameMode(gameMode);

        var gameModeName = MappingsData.GameMode().getGameModeName(gameMode).orElse(gameMode.name());

        var isSelf = targetPlayer == commandSender.getPlayer();
        var messageKey = isSelf? "Commands.GameMode.Success" : "Commands.GameMode.SuccessOther";

        MessageManager.getMessage(messageKey).ifPresent(message -> {
            var formatted = message.replace("<GAMEMODE>", gameModeName);
            MessageManager.sendMessageString(commandSender, formatted, targetPlayer.getName(), label);
        });
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
}
