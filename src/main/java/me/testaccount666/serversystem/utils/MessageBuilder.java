package me.testaccount666.serversystem.utils;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.messages.MessageManager;
import me.testaccount666.serversystem.userdata.User;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class MessageBuilder {
    private final String _messagePath;
    private final Type _type;
    private final User _receiver;
    private boolean _sendMessage = true;
    private boolean _sendPrefix = true;
    private boolean _format = true;
    private UnaryOperator<String> _preMessageModifier = null;
    private UnaryOperator<String> _postMessageModifier = null;
    private String _senderName;
    private String _targetName;
    private String _label;
    private String _syntaxPath;

    private MessageBuilder(String messagePath, Type type, User receiver) {
        _messagePath = messagePath;
        _type = type;
        _receiver = receiver;
    }

    public static MessageBuilder general(String messagePath, User receiver) {
        return of(Type.GENERAL, messagePath, receiver);
    }

    public static MessageBuilder command(String messagePath, User receiver) {
        return of(Type.COMMAND, messagePath, receiver);
    }

    public static MessageBuilder syntax(String messagePath, User receiver) {
        return of(Type.SYNTAX, messagePath, receiver);
    }

    public static MessageBuilder sign(String messagePath, User receiver) {
        return of(Type.CLICKABLE_SIGN, messagePath, receiver);
    }

    public static MessageBuilder of(Type type, String messagePath, User receiver) {
        return new MessageBuilder(messagePath, type, receiver);
    }

    public MessageBuilder postModifier(UnaryOperator<String> messageModifier) {
        _postMessageModifier = messageModifier;
        return this;
    }

    public MessageBuilder preModifier(UnaryOperator<String> messageModifier) {
        _preMessageModifier = messageModifier;
        return this;
    }

    public MessageBuilder send(boolean sendMessage) {
        _sendMessage = sendMessage;
        return this;
    }

    public MessageBuilder prefix(boolean sendPrefix) {
        _sendPrefix = sendPrefix;
        return this;
    }

    public MessageBuilder target(String targetName) {
        _targetName = targetName;
        return this;
    }

    public MessageBuilder sender(String senderName) {
        _senderName = senderName;
        return this;
    }

    public MessageBuilder label(String label) {
        _label = label;
        return this;
    }

    public MessageBuilder format(boolean format) {
        _format = format;
        return this;
    }

    public MessageBuilder syntaxPath(String syntaxPath) {
        _syntaxPath = syntaxPath;
        return this;
    }

    public Optional<String> build() {
        var messagePath = switch (_type) {
            case GENERAL -> "General.${_messagePath}";
            case COMMAND -> "Commands.${_messagePath}";
            case SYNTAX -> "Syntax.${_messagePath}";
            case CLICKABLE_SIGN -> "ClickableSigns.${_messagePath}";
        };

        var messageOptional = MessageManager.getMessage(_receiver, messagePath);
        if (messageOptional.isEmpty()) {
            if (_messagePath.equalsIgnoreCase("ErrorOccurred")) {
                ServerSystem.getLog().severe("'ErrorOccurred' message could not be found, this is a critical error!");
                ServerSystem.getLog().severe("Please report this error to the server administrator!");
                _receiver.sendMessage("Something went seriously wrong! Please contact an administrator of this server!");
                return Optional.empty();
            }

            general("ErrorOccurred", _receiver).build();
            return Optional.empty();
        }

        var message = messageOptional.get();

        if (_preMessageModifier != null) message = _preMessageModifier.apply(message);
        if (_senderName != null) message = message.replace("<SENDER>", _senderName);
        if (_targetName == null) _targetName = _receiver.getName().orElse("Unknown");

        if (_syntaxPath != null) {
            var syntaxOptional = syntax(_syntaxPath, _receiver).sender(_senderName).prefix(false)
                    .target(_targetName).label(_label).send(false).build();
            if (syntaxOptional.isPresent()) message = message.replace("<USAGE>", syntaxOptional.get());
            else message = message.replace("<USAGE>", "!!ERROR!!");
        }

        if (_postMessageModifier != null) message = _postMessageModifier.apply(message);

        var formattedMessage = _format? MessageManager.formatMessage(message, _receiver, _targetName, _label, _sendPrefix) : message;
        if (_sendMessage) _receiver.sendMessage(formattedMessage);

        return Optional.of(formattedMessage);
    }

    public enum Type {
        GENERAL,
        COMMAND,
        CLICKABLE_SIGN,
        SYNTAX,
    }
}
