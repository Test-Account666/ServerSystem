package me.testaccount666.serversystem.commands.executables.vanish;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@ServerSystemCommand(name = "vanish", variants = {"drop", "pickup", "interact", "message"})
public class CommandVanish extends AbstractServerSystemCommand {
    protected VanishPacket vanishPacket;

    public CommandVanish() {
        vanishPacket = new VanishPacket();
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Vanish.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();

        var isSelf = targetUser == commandSender;


        switch (command.getName().toLowerCase()) {
            case "vanish" -> handleVanishCommand(commandSender, label, targetUser, isSelf);
            case "drop" -> handleDropCommand(commandSender, label, targetUser, isSelf);
            case "pickup" -> handlePickupCommand(commandSender, label, targetUser, isSelf);
            case "interact" -> handleInteractCommand(commandSender, label, targetUser, isSelf);
            case "message" -> handleMessageCommand(commandSender, label, targetUser, isSelf);
        }
    }

    private void handleDropCommand(User commandSender, String label, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, label, targetUser, isSelf,
                "Drop", targetUser.getVanishData()::canDrop,
                value -> targetUser.getVanishData().setCanDrop(value)
        );
    }


    private void handlePickupCommand(User commandSender, String label, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, label, targetUser, isSelf,
                "Pickup", targetUser.getVanishData()::canPickup,
                value -> targetUser.getVanishData().setCanPickup(value)
        );
    }

    private void handleToggleCommand(User commandSender, String label, User targetUser,
                                     boolean isSelf, String featureName,
                                     BooleanSupplier getCurrentState, Consumer<Boolean> setState) {
        var messagePath = isSelf? featureName + ".Success" : featureName + ".SuccessOther";
        var enableFeature = !getCurrentState.getAsBoolean();

        messagePath = enableFeature? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        setState.accept(enableFeature);
        targetUser.save();

        sendCommandMessage(commandSender, messagePath, targetUser.getName().get(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "${featureName}.Success." + (enableFeature? "Enabled" : "Disabled"), commandSender.getName().get(), label, null);
    }


    private void handleInteractCommand(User commandSender, String label, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, label, targetUser, isSelf,
                "Interact", targetUser.getVanishData()::canInteract,
                value -> targetUser.getVanishData().setCanInteract(value)
        );
    }

    private void handleMessageCommand(User commandSender, String label, User targetUser, boolean isSelf) {
        handleToggleCommand(commandSender, label, targetUser, isSelf,
                "Message", targetUser.getVanishData()::canMessage,
                value -> targetUser.getVanishData().setCanMessage(value)
        );
    }

    private void handleVanishCommand(User commandSender, String label, User targetUser, boolean isSelf) {
        var messagePath = isSelf? "Vanish.Success" : "Vanish.SuccessOther";
        var enableVanish = !targetUser.isVanish();

        messagePath = enableVanish? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        targetUser.getPlayer().setSleepingIgnored(enableVanish);
        targetUser.getPlayer().setMetadata("vanished", new FixedMetadataValue(ServerSystem.Instance, enableVanish));
        targetUser.setVanish(enableVanish);
        targetUser.save();

        vanishPacket.sendVanishPacket(targetUser);

        sendCommandMessage(commandSender, messagePath, targetUser.getName().get(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "Vanish.Success." + (enableVanish? "Enabled" : "Disabled"), commandSender.getName().get(), label, null);
    }
}
