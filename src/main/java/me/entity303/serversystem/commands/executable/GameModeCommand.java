package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GameModeCommand implements ICommandExecutorOverload {
    protected final ServerSystem _plugin;

    public GameModeCommand(ServerSystem plugin) {
        this._plugin = plugin;

        var gameModeCreativeCommand = new GameModeCreativeCommmand(this._plugin, this);
        var gameModeSurvivalCommand = new GameModeSurvivalCommand(this._plugin, this);
        var gameModeAdventureCommand = new GameModeAdventureCommand(this._plugin, this);
        var gameModeSpectatorCommand = new GameModeSpectatorCommand(this._plugin, this);

        plugin.GetCommandManager().RegisterCommand("gmc", gameModeCreativeCommand, null);
        plugin.GetCommandManager().RegisterCommand("gms", gameModeSurvivalCommand, null);
        plugin.GetCommandManager().RegisterCommand("gma", gameModeAdventureCommand, null);
        plugin.GetCommandManager().RegisterCommand("gmsp", gameModeSpectatorCommand, null);
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
        var gameMode = arguments.length == 0? this.ParseGameMode("creative") : this.ParseGameMode(arguments[0]);

        if (gameMode == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null,
                                                                                                       "GameMode.NotGameMode")
                                                                                           .replace("<MODE>", arguments[0].toUpperCase()));
            return true;
        }

        if (!this.HasPermission(commandSender, gameMode, arguments)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("gamemode")));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "GameMode"));
            return true;
        }

        if (arguments.length == 1) {
            this.ChangeGameMode(commandSender, gameMode, command, commandLabel);
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[1]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
            return true;
        }

        this.ChangeGameMode(target, gameMode, commandSender, command, commandLabel);
        return true;
    }

    private GameMode ParseGameMode(String argument) {
        return switch (argument.toLowerCase()) {
            case "1", "c", "creative", "k", "kreativ" -> GameMode.CREATIVE;
            case "2", "a", "adventure", "abenteuer" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator", "z", "zuschauer" -> GameMode.SPECTATOR;
            case "0", "s", "survival", "ü", "überleben" -> GameMode.SURVIVAL;
            default -> null;
        };
    }

    private boolean HasPermission(CommandSender sender, GameMode parsedGameMode, String... arguments) {
        var permission = arguments.length == 1? "gamemode.self." : "gamemode.others.";

        assert parsedGameMode != null;
        permission += this.GetGameModeName(parsedGameMode).toLowerCase();
        return this._plugin.GetPermissions().HasPermission(sender, permission, true);
    }

    private void ChangeGameMode(CommandSender sender, GameMode gameMode, Command command, String commandLabel) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, sender, null, "GameMode"));
            return;
        }
        player.setGameMode(gameMode);
        player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                .GetMessage(commandLabel, command, sender, null, "GameMode.Changed.Self")
                                                                                .replace("<MODE>", this.GetGameModeName(gameMode)));
    }

    private void ChangeGameMode(Player target, GameMode gameMode, CommandSender sender, Command command, String commandLabel) {
        target.setGameMode(gameMode);
        target.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                .GetMessage(commandLabel, command.getName(), sender, target,
                                                                                            "GameMode.Changed.Others.Target")
                                                                                .replace("<MODE>", this.GetGameModeName(gameMode)));
        sender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                .GetMessage(commandLabel, command.getName(), sender, target,
                                                                                            "GameMode.Changed.Others.Sender")
                                                                                .replace("<MODE>", this.GetGameModeName(gameMode)));
    }

    private String GetGameModeName(GameMode gameMode) {
        return switch (gameMode) {
            case CREATIVE -> "Creative";
            case ADVENTURE -> "Adventure";
            case SPECTATOR -> "Spectator";
            case SURVIVAL -> "Survival";
        };
    }
}



