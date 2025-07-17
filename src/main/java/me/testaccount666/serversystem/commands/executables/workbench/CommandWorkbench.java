package me.testaccount666.serversystem.commands.executables.workbench;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "workbench", variants = {"anvil", "smithing", "loom", "grindstone", "cartography", "stonecutter"})
public class CommandWorkbench extends AbstractServerSystemCommand {
    private final Map<String, Consumer<Player>> _menuOpeners = Map.of(
            "Workbench", MenuUtils::openWorkbench,
            "Anvil", MenuUtils::openAnvil,
            "Smithing", MenuUtils::openSmithing,
            "Loom", MenuUtils::openLoom,
            "Grindstone", MenuUtils::openGrindstone,
            "Cartography", MenuUtils::openCartography,
            "Stonecutter", MenuUtils::openStonecutter
    );

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var player = commandSender.getPlayer();
        var commandName = capitalizeFirstLetter(command.getName());

        var permissionPath = "${commandName}.Use";
        if (!checkBasePermission(commandSender, permissionPath)) return;

        var opener = _menuOpeners.get(commandName);
        if (opener == null) throw new IllegalStateException("Unexpected command name: ${commandName}");

        opener.accept(player);
    }

    private String capitalizeFirstLetter(String input) {
        if (input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @Override
    public String getSyntaxPath(Command command) {
        throw new UnsupportedOperationException("Workbench command doesn't have an available syntax!");
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionMap = Map.of(
                "workbench", "Workbench.Use",
                "anvil", "Anvil.Use",
                "smithing", "Smithing.Use",
                "loom", "Loom.Use",
                "grindstone", "Grindstone.Use",
                "cartography", "Cartography.Use",
                "stonecutter", "Stonecutter.Use"
        );

        var permissionPath = permissionMap.get(command.getName().toLowerCase());
        if (permissionPath == null) return false;

        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }
}
