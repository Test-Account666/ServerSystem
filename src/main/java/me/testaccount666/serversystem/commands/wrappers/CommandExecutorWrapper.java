package me.testaccount666.serversystem.commands.wrappers;

import lombok.Getter;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Getter
public class CommandExecutorWrapper extends AbstractCommandWrapper implements CommandExecutor {
    private final ServerSystemCommandExecutor _commandExecutor;

    public CommandExecutorWrapper(ServerSystemCommandExecutor commandExecutor) {
        _commandExecutor = commandExecutor;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        var commandUser = resolveCommandUser(commandSender);

        // This should technically never happen...
        if (commandUser.isEmpty()) {
            ServerSystem.getLog().severe("Error executing command '${command.getName()}'. CommandSender '${commandSender.getName()}' is not a valid user?!");
            return false;
        }

        _commandExecutor.execute(commandUser.get(), command, label, arguments);
        return true;
    }
}
