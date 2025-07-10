package me.testaccount666.serversystem.commands.executables.ip;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "ip")
public class CommandIp extends AbstractServerSystemCommand {

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Ip.Use", false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Ip.Use")) return;
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        command("Ip.Success", commandSender).target(targetPlayer.getName())
                .modifier(message -> message.replace("<IP>", targetPlayer.getAddress().getAddress().getHostAddress())).build();
    }
}
