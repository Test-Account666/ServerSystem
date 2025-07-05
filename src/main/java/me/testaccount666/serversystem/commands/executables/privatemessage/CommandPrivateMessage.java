package me.testaccount666.serversystem.commands.executables.privatemessage;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ServerSystemCommand(name = "privatemessage", variants = "reply")
public class CommandPrivateMessage extends AbstractServerSystemCommand {
    private String _privateMessageCommand = null;

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("privatemessage")) {
            handlePrivateMessageCommand(commandSender, label, arguments);
            return;
        }

        handleReplyCommand(commandSender, label, arguments);
    }

    private void handleReplyCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Reply.Use", label)) return;

        var targerUser = commandSender.getReplyUser();

        if (!isValidReplyTarget(targerUser)) {
            sendCommandMessage(commandSender, "Reply.NoReply", null, label, null);
            return;
        }

        var newArguments = new String[arguments.length + 1];
        newArguments[0] = targerUser.getName().get();
        System.arraycopy(arguments, 0, newArguments, 1, arguments.length);

        handlePrivateMessageCommand(commandSender, label, newArguments);
    }

    private void handlePrivateMessageCommand(User commandSender, String label, String... arguments) {
        if (_privateMessageCommand == null) _privateMessageCommand = label;

        if (!checkBasePermission(commandSender, "PrivateMessage.Use", label)) return;

        if (arguments.length <= 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (isSelf) {
            sendCommandMessage(commandSender, "PrivateMessage.CannotSendToSelf", targetPlayer.getName(), label, null);
            return;
        }

        if (!targetUser.isAcceptsMessages()) {
            sendCommandMessage(commandSender, "PrivateMessage.NoMessages", targetPlayer.getName(), label, null);
            return;
        }

        var message = IntStream.range(1, arguments.length).mapToObj(index -> "${arguments[index]} ").collect(Collectors.joining()).trim();
        var successOptional = MessageManager.getFormattedMessage(commandSender, "Commands.PrivateMessage.Success", targetPlayer.getName(), label, false);
        var successOtherOptional = MessageManager.getFormattedMessage(commandSender, "Commands.PrivateMessage.SuccessOther", targetPlayer.getName(), label, false);

        if (successOptional.isEmpty() || successOtherOptional.isEmpty()) {
            Bukkit.getLogger().warning("Couldn't find message for path Commands.PrivateMessage.Success or Commands.PrivateMessage.SuccessOther");
            sendGeneralMessage(commandSender, "ErrorOccurred", targetPlayer.getName(), label, null);
            return;
        }

        var success = successOptional.get();
        var successOther = successOtherOptional.get();

        var successComponent = Component.text(success.replace("<MESSAGE>", message))
                .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${targetPlayer.getName()} "))
                .asComponent();

        var successOtherComponent = Component.text(successOther.replace("<MESSAGE>", message))
                .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${commandSender.getName().get()} "))
                .asComponent();

        commandSender.getCommandSender().sendMessage(successComponent);
        targetPlayer.sendMessage(successOtherComponent);

        targetUser.setReplyUser(commandSender);
        commandSender.setReplyUser(targetUser);
    }

    private boolean isValidReplyTarget(User targetUser) {
        if (targetUser == null || targetUser.getCommandSender() == null || targetUser.getName().isEmpty()) return false;

        if (targetUser instanceof ConsoleUser) return true;

        return targetUser.getPlayer() != null && targetUser.getPlayer().isOnline();
    }

}
