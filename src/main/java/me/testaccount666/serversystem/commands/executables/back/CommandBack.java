package me.testaccount666.serversystem.commands.executables.back;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

@ServerSystemCommand(name = "back")
public class CommandBack extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Back.Use", label)) return;

        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        var backType = commandSender.getLastBackType();
        backType = backType == BackType.NONE? BackType.DEATH : backType;

        if (arguments.length > 0) backType = switch (arguments[0].toLowerCase()) {
            case "death" -> BackType.DEATH;
            case "teleport", "tp" -> BackType.TELEPORT;
            default -> {
                sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
                yield null;
            }
        };

        if (backType == null) return;

        var backLocation = switch (backType) {
            case DEATH -> commandSender.getLastDeathLocation();
            case TELEPORT -> commandSender.getLastTeleportLocation();
            default -> {
                sendGeneralMessage(commandSender, "ErrorOccurred", null, label, null);
                throw new IllegalStateException("Unexpected value: ${backType}");
            }
        };

        if (backLocation == null) {
            sendGeneralMessage(commandSender, "NoBackLocation", null, label, null);
            return;
        }

        commandSender.getPlayer().teleport(backLocation);

        var finalBackType = backType;
        var typeNameOptional = MappingsData.BackType().getBackTypeName(finalBackType);
        var typeName = typeNameOptional.orElse("ERROR");

        sendCommandMessage(commandSender, "Back.Success", null, label,
                message -> message.replace("<TYPE>", typeName));
    }

    public enum BackType {
        DEATH,
        TELEPORT,
        NONE
    }
}
