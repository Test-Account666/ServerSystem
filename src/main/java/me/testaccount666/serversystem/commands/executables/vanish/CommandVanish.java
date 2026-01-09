package me.testaccount666.serversystem.commands.executables.vanish;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "vanish", variants = {"drop", "pickup", "interact", "message"})
public class CommandVanish extends AbstractServerSystemCommand {
    protected final VanishPacket vanishPacket;

    public CommandVanish() {
        vanishPacket = new VanishPacket();
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Vanish.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();

        var isSelf = targetUser == commandSender;


        switch (command.getName().toLowerCase()) {
            case "vanish" -> handleVanishCommand(commandSender, targetUser, isSelf);
            case "drop" -> handleDropCommand(commandSender, targetUser, isSelf);
            case "pickup" -> handlePickupCommand(commandSender, targetUser, isSelf);
            case "interact" -> handleInteractCommand(commandSender, targetUser, isSelf);
            case "message" -> handleMessageCommand(commandSender, targetUser, isSelf);
        }
    }

    private void handleDropCommand(User commandSender, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, targetUser, isSelf,
                "Drop", targetUser.getVanishData()::canDrop,
                value -> targetUser.getVanishData().canDrop(value)
        );
    }


    private void handlePickupCommand(User commandSender, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, targetUser, isSelf,
                "Pickup", targetUser.getVanishData()::canPickup,
                value -> targetUser.getVanishData().canPickup(value)
        );
    }

    private void handleToggleCommand(User commandSender, User targetUser, boolean isSelf, String featureName,
                                     BooleanSupplier getCurrentState, Consumer<Boolean> setState) {
        var messagePath = isSelf? "${featureName}.Success" : "${featureName}.SuccessOther";
        var enableFeature = !getCurrentState.getAsBoolean();

        messagePath = enableFeature? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        setState.accept(enableFeature);
        targetUser.save();

        command(messagePath, commandSender).target(targetUser.getName().get()).build();

        if (isSelf) return;

        command("${featureName}.Success" + (enableFeature? "Enabled" : "Disabled"), targetUser)
                .sender(commandSender.getName().get()).build();
    }


    private void handleInteractCommand(User commandSender, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, targetUser, isSelf,
                "Interact", targetUser.getVanishData()::canInteract,
                value -> targetUser.getVanishData().canInteract(value)
        );
    }

    private void handleMessageCommand(User commandSender, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, targetUser, isSelf,
                "Message", targetUser.getVanishData()::canMessage,
                value -> targetUser.getVanishData().canMessage(value)
        );
    }

    private void handleVanishCommand(User commandSender, User targetUser, boolean isSelf) {
        var messagePath = isSelf? "Vanish.Success" : "Vanish.SuccessOther";
        var enableVanish = !targetUser.isVanish();

        messagePath = enableVanish? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        targetUser.getPlayer().setSleepingIgnored(enableVanish);
        targetUser.getPlayer().setMetadata("vanished", new FixedMetadataValue(ServerSystem.getInstance(), enableVanish));
        targetUser.setVanish(enableVanish);
        targetUser.save();

        vanishPacket.sendVanishPacket(targetUser);

        command(messagePath, commandSender).target(targetUser.getName().get()).build();

        if (isSelf) return;

        command("Vanish.Success" + (enableVanish? "Enabled" : "Disabled"), targetUser).sender(commandSender.getName().get()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Vanish";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Vanish.Use", false);
    }
}
