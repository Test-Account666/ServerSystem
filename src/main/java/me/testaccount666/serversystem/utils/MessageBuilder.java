package me.testaccount666.serversystem.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.messages.MessageManager;
import me.testaccount666.serversystem.userdata.User;

import java.util.Optional;
import java.util.function.UnaryOperator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true, chain = true)
public class MessageBuilder {
    private final String _messagePath;
    private final Type _type;
    private final User _receiver;
    @Setter
    private boolean _send = true;
    @Setter
    private boolean _prefix = true;
    @Setter
    private boolean _format = true;
    @Setter
    private UnaryOperator<String> _preModifier = null;
    @Setter
    private UnaryOperator<String> _postModifier = null;
    @Setter
    private String _sender;
    @Setter
    private String _target;
    @Setter
    private String _label;
    @Setter
    private String _syntax;
    @Setter
    private String _language;

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

    public Optional<String> build() {
        var messagePath = switch (_type) {
            case GENERAL -> "General.${_messagePath}";
            case COMMAND -> "Commands.${_messagePath}";
            case SYNTAX -> "Syntax.${_messagePath}";
            case CLICKABLE_SIGN -> "ClickableSigns.${_messagePath}";
        };

        var messageOptional = _language != null? MessageManager.getMessage(_receiver, messagePath, _language)
                : MessageManager.getMessage(_receiver, messagePath);
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

        if (_preModifier != null) message = _preModifier.apply(message);
        if (_sender != null) message = message.replace("<SENDER>", _sender);
        if (_target == null) _target = _receiver.getName().orElse("Unknown");

        if (_syntax != null) {
            var syntaxOptional = syntax(_syntax, _receiver).sender(_sender).prefix(false)
                    .target(_target).label(_label).send(false).build();
            if (syntaxOptional.isPresent()) message = message.replace("<USAGE>", syntaxOptional.get());
            else message = message.replace("<USAGE>", "!!ERROR!!");
        }

        if (_postModifier != null) message = _postModifier.apply(message);

        var formattedMessage = _format? MessageManager.formatMessage(message, _receiver, _target, _label, _prefix) : message;
        if (_send) _receiver.sendMessage(formattedMessage);

        return Optional.of(formattedMessage);
    }

    public enum Type {
        GENERAL,
        COMMAND,
        CLICKABLE_SIGN,
        SYNTAX,
    }
}
