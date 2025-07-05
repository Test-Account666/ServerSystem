package me.testaccount666.serversystem.commands.executables.clearchat;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.concurrent.atomic.AtomicBoolean;

@ServerSystemCommand(name = "clearchat")
public class CommandClearChat extends AbstractServerSystemCommand {
    private static final int _CLEAR_LINES = 300;
    private static final String _EMPTY_LINE = "§r ";

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ClearChat.Use", label)) return;

        var sentError = new AtomicBoolean(false);
        Bukkit.getOnlinePlayers().forEach(everyone -> {
            if (!PermissionManager.hasPermission(everyone, "Commands.ClearChat.Bypass", false)) {
                for (var index = 0; index < _CLEAR_LINES; index++) {
                    var randomChar = (char) (33 + (Math.random() * (126 - 33))); // Safe ASCII range
                    everyone.sendMessage(String.valueOf(randomChar));
                }
                for (var index = 0; index < _CLEAR_LINES; index++) everyone.sendMessage(_EMPTY_LINE);
            }

            var messageOptional = MessageManager.getCommandMessage(commandSender, "ClearChat.Success", commandSender.getName().get(), label);

            if (messageOptional.isEmpty() && !sentError.get()) {
                sentError.set(true);
                sendGeneralMessage(commandSender, "ErrorOccurred", "*", label, null);
                return;
            }

            var message = messageOptional.get();
            everyone.sendMessage(message);
        });
    }
}
