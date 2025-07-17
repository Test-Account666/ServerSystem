package me.testaccount666.serversystem.commands.executables.kit;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.kit.manager.Kit;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.DurationParser.parseDuration;
import static me.testaccount666.serversystem.utils.DurationParser.parseUnbanDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "kit", variants = {"createkit", "deletekit"}, tabCompleter = TabCompleterKit.class)
public class CommandKit extends AbstractServerSystemCommand {

    public CommandKit() {
        ServerSystem.Instance.setKitManager(new KitManager());
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = getPermission(command);
        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var permissionPath = getPermission(command);
        if (!checkBasePermission(commandSender, permissionPath)) return;

        switch (command.getName().toLowerCase()) {
            case "createkit" -> handleCreateKit(commandSender, label, arguments);
            case "deletekit" -> handleDeleteKit(commandSender, label, arguments);
            case "kit" -> handleKit(commandSender, label, arguments);
        }
    }

    private void handleCreateKit(User commandSender, String label, String... arguments) {
        var kitName = arguments[0].toLowerCase();
        var kitManager = ServerSystem.Instance.getKitManager();
        var kitOptional = kitManager.getKit(kitName);
        if (kitOptional.isPresent()) {
            command("Kit.Create.KitAlreadyExists", commandSender)
                    .postModifier(message -> message.replace("<KIT>", kitOptional.get().getDisplayName())).build();
            return;
        }

        var cooldown = -1L;

        if (arguments.length > 1) cooldown = parseDuration(arguments[1]);
        if (cooldown == -2) {
            command("Kit.Create.InvalidCooldown", commandSender).build();
            return;
        }

        var player = commandSender.getPlayer();
        var inventory = player.getInventory();

        var offHandItem = inventory.getItemInOffHand();
        var armorContents = inventory.getArmorContents();
        var contents = inventory.getContents();

        var kit = new Kit(kitName, cooldown, offHandItem, armorContents, contents);
        kitManager.addKit(kit);
        kitManager.saveAllKits();
        command("Kit.Create.Success", commandSender).postModifier(message -> message.replace("<KIT>", kit.getDisplayName())).build();
    }

    private void handleDeleteKit(User commandSender, String label, String... arguments) {
        var kitName = arguments[0].toLowerCase();
        var kitManager = ServerSystem.Instance.getKitManager();
        if (!kitManager.kitExists(kitName)) {
            command("Kit.KitNotFound", commandSender)
                    .postModifier(message -> message.replace("<KIT>", arguments[0])).build();
            return;
        }

        kitManager.removeKit(kitName);
        command("Kit.Delete.Success", commandSender).postModifier(message -> message.replace("<KIT>", arguments[0])).build();
    }

    private void handleKit(User commandSender, String label, String... arguments) {
        var kitName = arguments[0].toLowerCase();
        var kitManager = ServerSystem.Instance.getKitManager();
        var kitOptional = kitManager.getKit(kitName);
        if (kitOptional.isEmpty()) {
            command("Kit.KitNotFound", commandSender)
                    .postModifier(message -> message.replace("<KIT>", arguments[0])).build();
            return;
        }
        var kit = kitOptional.get();

        var userOptional = getTargetUser(commandSender, 1, arguments);
        if (userOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[1]).build();
            return;
        }
        var targetUser = userOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Kit.Other", targetPlayer.getName())) return;

        if (isSelf && commandSender.isOnKitCooldown(kitName)) {
            var cooldown = commandSender.getKitCooldown(kitName);

            command("Kit.OnCooldown", commandSender)
                    .postModifier(message -> message.replace("<KIT>", kit.getDisplayName())
                            .replace("<DATE>", parseUnbanDate(cooldown))).build();
            return;
        }

        if (isSelf) commandSender.setKitCooldown(kitName, kit.getCoolDown());
        kit.giveKit(targetPlayer);

        var messagePath = "Kit.Success." + (isSelf? "Self" : "Other");
        command(messagePath, commandSender).target(targetPlayer.getName())
                .postModifier(message -> message.replace("<KIT>", kit.getDisplayName())).build();
    }

    private String getPermission(Command command) {
        return switch (command.getName().toLowerCase()) {
            case "createkit" -> "Kit.Create";
            case "deletekit" -> "Kit.Delete";
            case "kit" -> "Kit.Use";
            default -> null;
        };
    }
}
