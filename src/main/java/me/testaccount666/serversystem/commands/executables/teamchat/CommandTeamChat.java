package me.testaccount666.serversystem.commands.executables.teamchat;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "teamchat")
public class CommandTeamChat extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "TeamChat.Use")) return;
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var message = String.join(" ", arguments).trim();
        var formatOptional = command("TeamChat.Format", commandSender).send(false)
                .prefix(false).postModifier(format -> format.replace("<MESSAGE>", message)).build();
        if (formatOptional.isEmpty()) return;
        var format = formatOptional.get();

        Bukkit.getOnlinePlayers().forEach(everyone -> {
            if (!PermissionManager.hasCommandPermission(everyone, "TeamChat.Use", false)) return;

            everyone.sendMessage(format);
        });
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "TeamChat";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "TeamChat.Use", false);
    }
}
