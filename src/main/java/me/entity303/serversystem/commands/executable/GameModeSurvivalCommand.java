package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static me.entity303.serversystem.commands.executable.GameModeCommand.ExecuteGameMode;

public class GameModeSurvivalCommand extends CommandUtils implements CommandExecutorOverload {
    private static final String GAME_MODE = "survival";
    private final GameModeCommand gameModeCommand;

    public GameModeSurvivalCommand(ServerSystem plugin, GameModeCommand gameModeCommand) {
        super(plugin);

        this.gameModeCommand = gameModeCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        return ExecuteGameMode(commandSender, command, commandLabel, arguments, GAME_MODE, this.gameModeCommand);
    }
}
