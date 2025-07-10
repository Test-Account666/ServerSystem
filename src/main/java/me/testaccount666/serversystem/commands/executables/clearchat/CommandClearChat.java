package me.testaccount666.serversystem.commands.executables.clearchat;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;

@ServerSystemCommand(name = "clearchat")
public class CommandClearChat extends AbstractServerSystemCommand {
    private static final int _CLEAR_LINES = 300;
    private static final String _EMPTY_LINE = "§r ";

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ClearChat.Use")) return;

        Bukkit.getOnlinePlayers().forEach(everyone -> {
            if (!PermissionManager.hasPermission(everyone, "Commands.ClearChat.Bypass", false)) {
                for (var index = 0; index < _CLEAR_LINES; index++) {
                    var randomChar = (char) (33 + (Math.random() * (126 - 33))); // Safe ASCII range
                    everyone.sendMessage(String.valueOf(randomChar));
                }
                for (var index = 0; index < _CLEAR_LINES; index++) everyone.sendMessage(_EMPTY_LINE);
            }

            var messageOptional = command("ClearChat.Success", commandSender).send(false).build();

            if (messageOptional.isEmpty()) return;

            var message = messageOptional.get();
            everyone.sendMessage(message);
        });
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "ClearChat.Use", false);
    }
}
