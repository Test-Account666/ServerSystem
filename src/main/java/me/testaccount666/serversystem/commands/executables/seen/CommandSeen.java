package me.testaccount666.serversystem.commands.executables.seen;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ServerSystemCommand(name = "seen", tabCompleter = TabCompleterSeen.class)
public class CommandSeen extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Seen.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);

        if (cachedUserOptional.isEmpty()) {
            Bukkit.getLogger().warning("(CommandSeen) User '${arguments[0]}' is not cached! This should not happen!");
            sendGeneralMessage(commandSender, "ErrorOccurred", arguments[0], label, null);
            return;
        }

        var targetUser = cachedUserOptional.get().getOfflineUser();

        if (targetUser.getName().isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var lastSeen = targetUser.getLastSeen();

        if (targetUser instanceof User) lastSeen = System.currentTimeMillis();

        var formattedDate = parseDate(lastSeen);

        sendCommandMessage(commandSender, "Seen.Success", targetUser.getName().get(), label, message ->
                message.replace("<DATE>", formattedDate)
                        .replace("<IP>", targetUser.getLastKnownIp()));
    }


    private String parseDate(long dateMillis) {
        return Instant.ofEpochMilli(dateMillis)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
