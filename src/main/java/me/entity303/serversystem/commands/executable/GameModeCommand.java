package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GameModeCommand extends CommandUtils implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public GameModeCommand(ServerSystem plugin) {
        super(plugin);
        this.plugin = plugin;

        var gameModeCreativeCommand = new GameModeCreativeCommmand(this.plugin, this);
        var gameModeSurvivalCommand = new GameModeSurvivalCommand(this.plugin, this);
        var gameModeAdventureCommand = new GameModeAdventureCommand(this.plugin, this);
        var gameModeSpectatorCommand = new GameModeSpectatorCommand(this.plugin, this);

        plugin.getCommandManager().registerCommand("gmc", gameModeCreativeCommand, null);
        plugin.getCommandManager().registerCommand("gms", gameModeSurvivalCommand, null);
        plugin.getCommandManager().registerCommand("gma", gameModeAdventureCommand, null);
        plugin.getCommandManager().registerCommand("gmsp", gameModeSpectatorCommand, null);
    }

    @SuppressWarnings("DuplicatedCode")
    public static boolean ExecuteGameMode(CommandSender commandSender, Command command, String commandLabel, String[] arguments, String gameMode,
                                          GameModeCommand gameModeCommand) {
        if (arguments.length == 0)
            arguments = new String[] { gameMode };
        else if (arguments.length == 1)
            arguments = new String[] { gameMode, arguments[0] };
        else {
            List<String> argumentList = new LinkedList<>();

            Collections.addAll(argumentList, arguments);

            argumentList.add(0, gameMode);

            arguments = argumentList.toArray(new String[0]);
        }

        return gameModeCommand.onCommand(commandSender, command, commandLabel, arguments);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.hasPermission(commandSender, arguments)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission("gamemode"));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "GameMode"));
            return true;
        }

        var gameMode = this.parseGameMode(arguments[0]);
        if (gameMode == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null,
                                                                                                     "GameMode.NotGameMode")
                                                                                         .replace("<MODE>", arguments[0].toUpperCase()));
            return true;
        }

        if (arguments.length == 1) {
            this.changeGameMode(commandSender, gameMode, command, commandLabel);
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[1]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[1]));
            return true;
        }

        this.changeGameMode(target, gameMode, commandSender, command, commandLabel);
        return true;
    }

    private boolean hasPermission(CommandSender sender, String... arguments) {
        var permission = arguments.length == 1? "gamemode.self." : "gamemode.others.";

        var parsedGameMode = this.parseGameMode(arguments[0]);

        assert parsedGameMode != null;
        permission += this.getGameModeName(parsedGameMode).toLowerCase();
        return this.plugin.getPermissions().hasPermission(sender, permission, true);
    }

    private GameMode parseGameMode(String argument) {
        return switch (argument.toLowerCase()) {
            case "1", "c", "creative", "k", "kreativ" -> GameMode.CREATIVE;
            case "2", "a", "adventure", "abenteuer" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator", "z", "zuschauer" -> GameMode.SPECTATOR;
            case "0", "s", "survival", "ü", "überleben" -> GameMode.SURVIVAL;
            default -> null;
        };
    }

    private void changeGameMode(CommandSender sender, GameMode gameMode, Command command, String commandLabel) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, sender, null, "GameMode"));
            return;
        }
        player.setGameMode(gameMode);
        player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command, sender, null, "GameMode.Changed.Self")
                                                                              .replace("<MODE>", this.getGameModeName(gameMode)));
    }

    private void changeGameMode(Player target, GameMode gameMode, CommandSender sender, Command command, String commandLabel) {
        target.setGameMode(gameMode);
        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command.getName(), sender, target,
                                                                                          "GameMode.Changed.Others.Target")
                                                                              .replace("<MODE>", this.getGameModeName(gameMode)));
        sender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command.getName(), sender, target,
                                                                                          "GameMode.Changed.Others.Sender")
                                                                              .replace("<MODE>", this.getGameModeName(gameMode)));
    }

    private String getGameModeName(GameMode gameMode) {
        return switch (gameMode) {
            case CREATIVE -> "Creative";
            case ADVENTURE -> "Adventure";
            case SPECTATOR -> "Spectator";
            case SURVIVAL -> "Survival";
        };
    }
}



