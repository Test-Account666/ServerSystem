package me.testaccount666.serversystem.commands.executables.back;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "back")
public class CommandBack extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Back.Use")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var backType = commandSender.getLastBackType();
        backType = backType == BackType.NONE? BackType.DEATH : backType;

        if (arguments.length > 0) backType = switch (arguments[0].toLowerCase()) {
            case "death" -> BackType.DEATH;
            case "teleport", "tp" -> BackType.TELEPORT;
            default -> {
                general("InvalidArguments", commandSender).label(label).build();
                yield null;
            }
        };

        if (backType == null) return;

        var backLocation = switch (backType) {
            case DEATH -> commandSender.getLastDeathLocation();
            case TELEPORT -> commandSender.getLastTeleportLocation();
            default -> {
                general("ErrorOccurred", commandSender).label(label).build();
                throw new IllegalStateException("Unexpected value: ${backType}");
            }
        };

        if (backLocation == null) {
            command("Back.NoBackLocation", commandSender).build();
            return;
        }

        commandSender.getPlayer().teleport(backLocation);

        var finalBackType = backType;
        var typeNameOptional = MappingsData.backType().getBackTypeName(finalBackType);
        var typeName = typeNameOptional.orElse("ERROR");

        command("Back.Success", commandSender)
                .postModifier(message -> message.replace("<TYPE>", typeName)).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Back.Use", false);
    }

    public enum BackType {
        DEATH,
        TELEPORT,
        NONE
    }
}
