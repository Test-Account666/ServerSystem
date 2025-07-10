package me.testaccount666.serversystem.commands.executables.seen;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "seen", tabCompleter = TabCompleterSeen.class)
public class CommandSeen extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Seen.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);

        if (cachedUserOptional.isEmpty()) {
            Bukkit.getLogger().warning("(CommandSeen) User '${arguments[0]}' is not cached! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        var targetUser = cachedUserOptional.get().getOfflineUser();

        if (targetUser.getName().isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var lastSeen = targetUser.getLastSeen();

        if (targetUser instanceof User) lastSeen = System.currentTimeMillis();

        var formattedDate = parseDate(lastSeen);

        command("Seen.SuccessOther", commandSender).target(targetUser.getName().get())
                .modifier(message -> message.replace("<DATE>", formattedDate)
                        .replace("<IP>", targetUser.getLastKnownIp())).build();
    }


    private String parseDate(long dateMillis) {
        return Instant.ofEpochMilli(dateMillis)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Seen.Use", false);
    }
}
