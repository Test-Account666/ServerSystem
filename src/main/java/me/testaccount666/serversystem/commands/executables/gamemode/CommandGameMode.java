package me.testaccount666.serversystem.commands.executables.gamemode;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "gamemode", variants = {"gms", "gmc", "gma", "gmsp"})
public class CommandGameMode implements ServerSystemCommandExecutor {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        //TODO: Remove testing-implementation and replace with actual one
        commandSender.getCommandSender().sendMessage("Hello World!");
    }

    private void handleGameModeSurvival(User commandSender, Player targetPlayer, String label) {

    }
}
