package me.testaccount666.serversystem.commands.executables.privatemessage;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "privatemessage", variants = {"reply", "messagetoggle"})
public class CommandPrivateMessage extends AbstractServerSystemCommand {
    private String _privateMessageCommand = null;

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("messagetoggle")) {
            handleMessageToggleCommand(commandSender, label, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("privatemessage")) {
            handlePrivateMessageCommand(commandSender, label, arguments);
            return;
        }

        handleReplyCommand(commandSender, label, arguments);
    }

    private void handleMessageToggleCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "MessageToggle.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "MessageToggle.Other", targetPlayer.getName())) return;

        var acceptsMessages = !targetUser.isAcceptsMessages();

        targetUser.setAcceptsMessages(acceptsMessages);
        targetUser.save();

        var messagePath = isSelf? "MessageToggle.Success" : "MessageToggle.SuccessOther";
        messagePath = acceptsMessages? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("MessageToggle.Success" + (acceptsMessages? "Enabled" : "Disabled"), targetUser)
                .sender(commandSender.getName().get()).build();
    }

    private void handleReplyCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Reply.Use")) return;

        var targerUser = commandSender.getReplyUser();

        if (!isValidReplyTarget(targerUser)) {
            command("Reply.NoReply", commandSender).build();
            return;
        }

        var newArguments = new String[arguments.length + 1];
        newArguments[0] = targerUser.getName().get();
        System.arraycopy(arguments, 0, newArguments, 1, arguments.length);

        sendPrivateMessage(commandSender, targerUser, label, newArguments);
    }

    private void handlePrivateMessageCommand(User commandSender, String label, String... arguments) {
        if (_privateMessageCommand == null) _privateMessageCommand = label;
        if (!checkBasePermission(commandSender, "PrivateMessage.Use")) return;

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        sendPrivateMessage(commandSender, targetUser, label, arguments);
    }

    private void sendPrivateMessage(User commandSender, User targetUser, String label, String... arguments) {
        var nameOptional = targetUser.getName();
        if (nameOptional.isEmpty()) {
            general("ErrorOccurred", commandSender).label(label).target(targetUser.getUuid().toString()).build();
            return;
        }

        var targetName = nameOptional.get();

        var isSelf = targetUser == commandSender;

        if (isSelf) {
            command("PrivateMessage.CannotSendToSelf", commandSender).build();
            return;
        }

        if (!targetUser.isAcceptsMessages()) {
            command("PrivateMessage.NoMessages", commandSender).target(targetName).build();
            return;
        }

        var message = IntStream.range(1, arguments.length).mapToObj(index -> "${arguments[index]} ").collect(Collectors.joining()).trim();

        var successOptional = command("PrivateMessage.Success", commandSender)
                .target(targetName).prefix(false).send(false)
                .modifier(msg -> msg.replace("<MESSAGE>", message)).build();

        var successOtherOptional = command("PrivateMessage.SuccessOther", targetUser)
                .sender(commandSender.getName().get()).prefix(false).send(false)
                .modifier(msg -> msg.replace("<MESSAGE>", message)).build();

        if (successOptional.isEmpty() || successOtherOptional.isEmpty()) {
            Bukkit.getLogger().warning("Couldn't find message for path Commands.PrivateMessage.Success or Commands.PrivateMessage.SuccessOther");
            general("ErrorOccurred", commandSender).label(label).target(targetName).build();
            return;
        }

        var success = successOptional.get();
        var successOther = successOtherOptional.get();

        var successComponent = Component.text(success)
                .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${targetName} "))
                .asComponent();

        var successOtherComponent = Component.text(successOther)
                .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${commandSender.getName().get()} "))
                .asComponent();

        commandSender.getCommandSender().sendMessage(successComponent);
        commandSender.setReplyUser(targetUser);

        if (targetUser.isIgnoredPlayer(commandSender.getUuid())) return;

        targetUser.getCommandSender().sendMessage(successOtherComponent);
        targetUser.setReplyUser(commandSender);
    }

    private boolean isValidReplyTarget(User targetUser) {
        if (targetUser == null || targetUser.getCommandSender() == null || targetUser.getName().isEmpty()) return false;

        if (targetUser instanceof ConsoleUser) return true;

        return targetUser.getPlayer() != null && targetUser.getPlayer().isOnline();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        if (command.getName().equalsIgnoreCase("privatemessage")) return PermissionManager.hasCommandPermission(player, "PrivateMessage.Use", false);

        if (command.getName().equalsIgnoreCase("reply")) return PermissionManager.hasCommandPermission(player, "Reply.Use", false);

        if (command.getName().equalsIgnoreCase("messagetoggle")) return PermissionManager.hasCommandPermission(player, "MessageToggle.Use", false);

        return false;

    }
}
