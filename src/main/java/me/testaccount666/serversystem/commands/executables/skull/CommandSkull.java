package me.testaccount666.serversystem.commands.executables.skull;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "skull")
public class CommandSkull extends AbstractServerSystemCommand {
    private final SkullCreator _skullCreator;

    public CommandSkull() {
        _skullCreator = new SkullCreator();
    }

    private static UUID ParseUuid(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static URL ParseUrl(String input) {
        try {
            return URI.create(input).toURL();
        } catch (IllegalArgumentException | MalformedURLException exception) {
            return null;
        }
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(ServerSystem.Instance, () -> {
            if (!checkBasePermission(commandSender, "Skull.Use")) return;
            if (commandSender instanceof ConsoleUser) {
                general("NotPlayer", commandSender).build();
                return;
            }

            if (arguments.length == 0) {
                executeSelfSkull(commandSender);
                return;
            }

            if (!checkOtherPermission(commandSender, "Skull.Other", arguments[0])) return;
            executeOtherSkull(commandSender, arguments);
        });
    }

    private void executeSelfSkull(User commandSender) {
        var skull = _skullCreator.getSkull(commandSender.getName().get());
        commandSender.getPlayer().getInventory().addItem(skull);
    }

    private void executeOtherSkull(User commandSender, String... arguments) {
        var argument = arguments[0];

        var skull = createSkullFromInput(commandSender, argument);
        if (skull == null) {
            general("ErrorOccurred", commandSender).build();
            return;
        }

        commandSender.getPlayer().getInventory().addItem(skull);
        command("Skull.Success", commandSender).build();
    }

    private ItemStack createSkullFromInput(User commandSender, String input) {
        var uuid = ParseUuid(input);
        if (uuid != null) return _skullCreator.getSkull(uuid);

        var parsedUrl = ParseUrl(input);
        if (parsedUrl != null) {
            command("Skull.Fetching", commandSender).build();
            return _skullCreator.getSkullByTexture(parsedUrl);
        }

        return _skullCreator.getSkull(input);
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Skull";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Skull.Use", false);
    }
}
