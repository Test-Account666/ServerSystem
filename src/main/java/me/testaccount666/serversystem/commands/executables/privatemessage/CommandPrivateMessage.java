package me.testaccount666.serversystem.commands.executables.privatemessage;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.events.UserPrivateMessageEvent;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.messages.MessageManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ComponentColor;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "privatemessage", variants = {"reply", "messagetoggle", "socialspy"})
public class CommandPrivateMessage extends AbstractServerSystemCommand {
    private String _privateMessageCommand = null;

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName().toLowerCase();

        switch (commandName) {
            case "socialspy" -> handleSocialSpyCommand(commandSender, command, label, arguments);
            case "messagetoggle" -> handleMessageToggleCommand(commandSender, command, label, arguments);
            case "privatemessage" -> handlePrivateMessageCommand(commandSender, command, label, arguments);
            default -> handleReplyCommand(commandSender, command, label, arguments);
        }
    }

    private void handleSocialSpyCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "SocialSpy.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "SocialSpy.Other", targetPlayer.getName())) return;

        var isEnabled = !targetUser.isSocialSpyEnabled();

        var messagePath = isSelf? "SocialSpy.Success" : "SocialSpy.SuccessOther";

        messagePath += isEnabled? ".Enabled" : ".Disabled";

        targetUser.setSocialSpyEnabled(isEnabled);
        targetUser.save();

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("SocialSpy.Success" + (isEnabled? "Enabled" : "Disabled"), targetUser).build();
    }

    private void handleMessageToggleCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "PrivateMessage.Toggle.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "PrivateMessage.Toggle.Other", targetPlayer.getName())) return;

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

    private void handleReplyCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "PrivateMessage.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

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

    private void handlePrivateMessageCommand(User commandSender, Command command, String label, String... arguments) {
        if (_privateMessageCommand == null) _privateMessageCommand = label;
        if (!checkBasePermission(commandSender, "PrivateMessage.Use")) return;

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
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
                .format(false).target(targetName).prefix(false).send(false)
                .postModifier(msg -> MessageManager.applyPlaceholders(msg, commandSender, targetName, label)
                        .replace("<MESSAGE>", message)).build();

        var successOtherOptional = command("PrivateMessage.SuccessOther", targetUser)
                .sender(commandSender.getName().get()).prefix(false).send(false)
                .postModifier(msg -> MessageManager.applyPlaceholders(msg, targetUser, targetName, label)
                        .replace("<MESSAGE>", message)).build();

        if (successOptional.isEmpty() || successOtherOptional.isEmpty()) {
            ServerSystem.getLog().warning("Couldn't find message for path Commands.PrivateMessage.Success or Commands.PrivateMessage.SuccessOther");
            general("ErrorOccurred", commandSender).label(label).target(targetName).build();
            return;
        }

        var messageEvent = new UserPrivateMessageEvent(commandSender, message, targetUser);
        Bukkit.getPluginManager().callEvent(messageEvent);
        if (messageEvent.isCancelled()) return;

        var success = successOptional.get();
        var successOther = successOtherOptional.get();

        var successComponent = ComponentColor.translateToComponent(success)
                .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${targetName} "))
                .asComponent();

        var successOtherComponent = ComponentColor.translateToComponent(successOther)
                .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${commandSender.getName().get()} "))
                .asComponent();


        messageEvent.getRecipients().forEach(recipient -> {
            if (recipient == commandSender) {
                commandSender.sendMessage(successComponent);
                commandSender.setReplyUser(targetUser);
                return;
            }

            if (recipient == targetUser && targetUser.isIgnoredPlayer(commandSender.getUuid())) return;

            recipient.sendMessage(successOtherComponent);
            recipient.setReplyUser(commandSender);
        });
    }

    private boolean isValidReplyTarget(User targetUser) {
        if (targetUser == null || targetUser.getCommandSender() == null || targetUser.getName().isEmpty()) return false;

        if (targetUser instanceof ConsoleUser) return true;

        return targetUser.getPlayer() != null && targetUser.getPlayer().isOnline();
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "privatemessage" -> "PrivateMessage";
            case "reply" -> "Reply";
            case "messagetoggle" -> "MessageToggle";
            case "socialspy" -> "SocialSpy";
            default -> throw new IllegalStateException("(CommandPrivateMessage) Unexpected value: ${commandName}");
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "privatemessage" -> "PrivateMessage.Use";
            case "reply" -> "PrivateMessage.Use";
            case "messagetoggle" -> "PrivateMessage.Toggle.Use";
            case "socialspy" -> "SocialSpy.Use";
            default -> throw new IllegalStateException("(CommandPrivateMessage) Unexpected value: ${command.getName().toLowerCase()}");
        };

        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }
}
