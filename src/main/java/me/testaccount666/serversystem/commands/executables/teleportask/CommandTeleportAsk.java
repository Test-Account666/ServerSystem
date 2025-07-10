package me.testaccount666.serversystem.commands.executables.teleportask;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "teleportask", variants = {"teleporthereask", "teleportaccept", "teleportdeny", "teleporttoggle"})
public class CommandTeleportAsk extends AbstractServerSystemCommand {
    protected final Set<TeleportRequest> activeTeleportRequests = new HashSet<>();

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        switch (command.getName()) {
            case "teleportask" -> handleTeleportAsk(commandSender, label, arguments);
            case "teleportaccept" -> handleTeleportAccept(commandSender, label);
            case "teleportdeny" -> handleTeleportDeny(commandSender, label);
            case "teleporthereask" -> handleTeleportHereAsk(commandSender, label, arguments);
            case "teleporttoggle" -> handleTeleportToggle(commandSender, label, arguments);
        }
    }

    /**
     * Validates a target player for teleport commands
     *
     * @param commandSender The user sending the command
     * @param label         The command label used
     * @param arguments     Command arguments containing target player name
     * @return The target User if valid, null if validation failed
     */
    private User validateTargetPlayer(User commandSender, String label, String... arguments) {
        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return null;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (isSelf) {
            command("TeleportAsk.CannotTeleportSelf", commandSender).build();
            return null;
        }

        if (!targetUser.isAcceptsTeleports()) {
            command("TeleportAsk.NoTeleport", commandSender).target(targetPlayer.getName()).build();
            return null;
        }

        return targetUser;
    }

    /**
     * Validates common command requirements for teleport commands
     *
     * @param commandSender    The user sending the command
     * @param permissionSuffix The permission suffix to check
     * @param label            The command label used
     * @param arguments        Command arguments
     * @return true if validation failed and command should exit, false to continue processing
     */
    private boolean validateTeleportCommand(User commandSender, String permissionSuffix, String label, String... arguments) {
        if (!checkBasePermission(commandSender, permissionSuffix)) return true;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return true;
        }

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return true;
        }

        return false;
    }

    private void handleTeleportAsk(User commandSender, String label, String... arguments) {
        if (validateTeleportCommand(commandSender, "TeleportAsk.Use", label, arguments)) return;

        var targetUser = validateTargetPlayer(commandSender, label, arguments);
        if (targetUser == null) return;

        var targetPlayer = targetUser.getPlayer();

        var timeOut = System.currentTimeMillis() + (1000 * 60 * 2); // Two minutes


        command("TeleportAsk.Success", commandSender).target(targetPlayer.getName()).build();

        if (targetUser.isIgnoredPlayer(commandSender.getUuid())) return;

        command("TeleportAsk.SuccessOther", targetUser).sender(commandSender.getName().get()).build();

        var teleportRequest = new TeleportRequest(commandSender, targetUser, timeOut, false);
        targetUser.setTeleportRequest(teleportRequest);
        sendAcceptDenyButtons(commandSender, targetUser, label);
    }

    private void handleTeleportHereAsk(User commandSender, String label, String... arguments) {
        if (validateTeleportCommand(commandSender, "TeleportHereAsk.Use", label, arguments)) return;

        var targetUser = validateTargetPlayer(commandSender, label, arguments);
        if (targetUser == null) return;

        var targetPlayer = targetUser.getPlayer();

        var timeOut = System.currentTimeMillis() + (1000 * 60 * 2); // Two minutes

        command("TeleportHereAsk.Success", commandSender).target(targetPlayer.getName()).build();

        if (targetUser.isIgnoredPlayer(commandSender.getUuid())) return;

        command("TeleportHereAsk.SuccessOther", targetUser).sender(commandSender.getName().get()).build();

        var teleportRequest = new TeleportRequest(commandSender, targetUser, timeOut, true);
        targetUser.setTeleportRequest(teleportRequest);
        sendAcceptDenyButtons(commandSender, targetUser, label);
    }

    private void sendAcceptDenyButtons(User commandSender, User targetUser, String label) {
        var targetPlayer = targetUser.getPlayer();


        var acceptButton = command("TeleportAsk.Buttons.Accept.Name", targetUser)
                .send(false).prefix(false).build();

        var denyButton = command("TeleportAsk.Buttons.Deny.Name", targetUser)
                .send(false).prefix(false).build();

        if (acceptButton.isEmpty() || denyButton.isEmpty()) {
            Bukkit.getLogger().warning("Couldn't find accept or deny button for ${targetUser.getName().get()} in the language file. Please check the language file for errors.");
            general("ErrorOccurred", targetUser).label(label).build();
            return;
        }

        var acceptButtonTooltip = command("TeleportAsk.Buttons.Accept.Tooltip", targetUser)
                .prefix(false).send(false).build();

        var denyButtonTooltip = command("TeleportAsk.Buttons.Deny.Tooltip", targetUser)
                .prefix(false).send(false).build();

        if (acceptButtonTooltip.isEmpty() || denyButtonTooltip.isEmpty()) {
            Bukkit.getLogger().warning("Couldn't find accept or deny button tooltip for ${targetUser.getName().get()} in the language file. Please check the language file for errors.");
            general("ErrorOccurred", targetUser).label(label).build();
            return;
        }

        var acceptComponent = createMessageComponent(
                acceptButton.get(),
                acceptButtonTooltip.get(),
                ClickEvent.callback(audience -> handleTeleportAccept(targetUser, label))
        );

        var denyComponent = createMessageComponent(
                denyButton.get(),
                denyButtonTooltip.get(),
                ClickEvent.callback(audience -> handleTeleportDeny(targetUser, label))
        );

        targetPlayer.sendMessage(acceptComponent);
        targetPlayer.sendMessage(denyComponent);
    }


    /**
     * Validates a teleport request for accept/deny commands
     *
     * @param commandSender    The user who is accepting/denying
     * @param permissionSuffix The permission suffix to check
     * @return The teleport request if valid, null otherwise
     */
    private TeleportRequest validateTeleportRequest(User commandSender, String permissionSuffix) {
        if (!checkBasePermission(commandSender, permissionSuffix)) return null;

        var teleportRequest = commandSender.getTeleportRequest();

        if (teleportRequest == null || teleportRequest.isExpired()) {
            command("TeleportAccept.NoRequest", commandSender).build();
            return null;
        }

        var requester = teleportRequest.getSender();
        if (requester.getPlayer() == null || !requester.getPlayer().isOnline()) {
            command("TeleportAccept.NoRequest", commandSender).build();
            return null;
        }

        return teleportRequest;
    }

    private void handleTeleportAccept(User commandSender, String label) {
        var teleportRequest = validateTeleportRequest(commandSender, "TeleportAccept.Use");
        if (teleportRequest == null) return;

        var requester = teleportRequest.getSender();

        commandSender.setTeleportRequest(null);

        command("TeleportAccept.SuccessOther", requester).target(commandSender.getName().get()).build();

        var teleporter = teleportRequest.isTeleportHere()? commandSender : requester;
        var target = teleportRequest.isTeleportHere()? requester : commandSender;

        var canInstantTeleport = PermissionManager.hasPermission(teleporter.getCommandSender(), "Commands.TeleportAsk.InstantTeleport", false);

        if (canInstantTeleport) {
            executeTeleport(teleporter, target, label);
            return;
        }

        command("TeleportAsk.StartingTeleporting", teleporter).target(target.getName().get()).build();
        startTeleportTimer(teleporter, target, teleportRequest);
    }

    private void handleTeleportDeny(User commandSender, String label) {
        var teleportRequest = validateTeleportRequest(commandSender, "TeleportDeny.Use");
        if (teleportRequest == null) return;

        var requester = teleportRequest.getSender();

        commandSender.setTeleportRequest(null);

        command("TeleportDeny.Success", commandSender).target(requester.getName().get()).build();
        command("TeleportDeny.SuccessOther", requester).target(commandSender.getName().get()).build();
    }

    private void startTeleportTimer(User teleporter, User target, TeleportRequest teleportRequest) {
        var teleporterPlayer = teleporter.getPlayer();
        var targetPlayer = target.getPlayer();

        activeTeleportRequests.add(teleportRequest);
        teleportRequest.setTimerId(Bukkit.getScheduler().scheduleSyncDelayedTask(ServerSystem.Instance, () -> {
            if (teleporterPlayer == null || !teleporterPlayer.isOnline()) return;
            if (targetPlayer == null || !targetPlayer.isOnline()) return;

            executeTeleport(teleporter, target, null);
            activeTeleportRequests.remove(teleportRequest);
        }, 20L * 5));
    }

    /**
     * Executes the teleport with animation and notification
     *
     * @param teleporter The user who is teleporting
     * @param target     The target user to teleport to
     * @param label      The command label
     */
    private void executeTeleport(User teleporter, User target, String label) {
        var targetLocation = target.getPlayer().getLocation();

        playAnimation(targetLocation);
        teleporter.getPlayer().teleport(targetLocation);
        playAnimation(targetLocation);

        command("TeleportAsk.TeleportFinished", teleporter).target(target.getName().get()).build();
    }

    /**
     * Creates an interactive message component with hover text and click action
     *
     * @param text        The button text
     * @param hoverText   The text to show when hovering over the button
     * @param clickAction The action to perform when clicked
     * @return The formatted component
     */
    private Component createMessageComponent(String text, String hoverText, ClickEvent clickAction) {
        return Component.text(text)
                .hoverEvent(HoverEvent.showText(Component.text(hoverText)))
                .clickEvent(clickAction)
                .asComponent();
    }


    /**
     * Plays a teleportation animation effect at the given location
     *
     * @param location The location to play the animation at
     */
    private void playAnimation(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        location.getWorld().spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05);
    }

    private void handleTeleportToggle(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "TeleportToggle.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "TeleportToggle.Other", targetPlayer.getName())) return;

        var acceptsTeleports = !targetUser.isAcceptsTeleports();

        var messagePath = isSelf? "TeleportToggle.Success" : "TeleportToggle.SuccessOther";
        messagePath = acceptsTeleports? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        targetUser.setAcceptsTeleports(acceptsTeleports);
        targetUser.save();

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;

        command("TeleportToggle.Success" + (acceptsTeleports? "Enabled" : "Disabled"), targetUser)
                .sender(commandSender.getName().get()).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName()) {
            case "teleportask" -> "TeleportAsk.Use";
            case "teleportaccept" -> "TeleportAccept.Use";
            case "teleportdeny" -> "TeleportDeny.Use";
            case "teleporthereask" -> "TeleportHereAsk.Use";
            case "teleporttoggle" -> "TeleportToggle.Use";
            default -> null;
        };
        if (permissionPath == null) return false;

        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }
}
