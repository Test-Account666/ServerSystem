package me.testaccount666.serversystem.commands.executables.fly;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

@ServerSystemCommand(name = "fly")
public class CommandFly implements ServerSystemCommandExecutor {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Fly.Use")) {
            MessageManager.getNoPermissionMessage(commandSender, "Commands.Fly.Use", null, label).ifPresent(commandSender::sendMessage);
            return;
        }

        if (arguments.length == 0 && commandSender instanceof ConsoleUser) {
            MessageManager.getFormattedMessage(commandSender, "General.NotPlayer", null, label).ifPresent(commandSender::sendMessage);
            return;
        }

        var targetPlayerOptional = getTargetPlayer(commandSender, arguments);

        if (targetPlayerOptional.isEmpty()) {
            MessageManager.getFormattedMessage(commandSender, "General.PlayerNotFound", arguments[0], label).ifPresent(commandSender::sendMessage);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();
        var targetUserOptional = ServerSystem.Instance.getUserManager().getUser(targetPlayer);

        if (targetUserOptional.isEmpty()) {
            Bukkit.getLogger().warning("(CommandFly) User '${targetPlayer.getName()}' is not cached! This should not happen!");
            MessageManager.getFormattedMessage(commandSender, "General.ErrorOccurred", arguments[0], label).ifPresent(commandSender::sendMessage);
            return;
        }

        // Target should be online, so casting, without additional checks, should be safe
        var targetUser = (User) targetUserOptional.get().getOfflineUser();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !PermissionManager.hasCommandPermission(commandSender, "Fly.Other")) {
            MessageManager.getNoPermissionMessage(commandSender, "Commands.Fly.Other", targetPlayer.getName(), label).ifPresent(commandSender::sendMessage);
            return;
        }

        var messagePath = isSelf? "Fly.Success" : "Fly.SuccessOther";
        var isFlying = !targetPlayer.getAllowFlight();

        messagePath = isFlying? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        var isOnGround = targetPlayer.getLocation().add(0, -.3, 0).getBlock().getType().isSolid();

        targetPlayer.setAllowFlight(isFlying);
        if (!isOnGround) targetPlayer.setFlying(isFlying);

        MessageManager.getCommandMessage(commandSender, messagePath, targetPlayer.getName(), label).ifPresent(commandSender::sendMessage);

        if (isSelf) return;
        MessageManager.getCommandMessage(targetPlayer, "Fly.Success." + (isFlying? "Enabled" : "Disabled"), null, label).ifPresent(targetPlayer::sendMessage);
    }

    private Optional<Player> getTargetPlayer(User commandSender, String... arguments) {
        if (arguments.length > 0) return Optional.ofNullable(Bukkit.getPlayer(arguments[0]));

        return Optional.of(commandSender.getPlayer());
    }
}
