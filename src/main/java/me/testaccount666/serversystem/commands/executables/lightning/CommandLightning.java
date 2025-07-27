package me.testaccount666.serversystem.commands.executables.lightning;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "lightning", tabCompleter = TabCompleterLightning.class)
public class CommandLightning extends AbstractServerSystemCommand {
    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Lightning.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetOptional = getTargetUser(commandSender, arguments);
        if (targetOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        var block = isSelf? commandSender.getPlayer().getTargetBlockExact(100) : targetPlayer.getLocation().getBlock();
        if (block == null) {
            command("Lightning.NoTarget", commandSender).build();
            return;
        }

        var effectOnly = false;
        if (arguments.length > 1) effectOnly = "visual".startsWith(arguments[1].toLowerCase());

        if (effectOnly) block.getWorld().strikeLightningEffect(block.getLocation());
        else block.getWorld().strikeLightning(block.getLocation());

        if (isSelf) general("Lightning.Success", commandSender).build();
        else general("Lightning.TargetSuccess", commandSender).target(targetPlayer.getName()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Lightning";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Lightning.Use", false);
    }
}
