package me.testaccount666.serversystem.commands.executables.speed;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "speed", variants = {"flyspeed", "walkspeed"})
public class CommandSpeed extends AbstractServerSystemCommand {

    private static SpeedResult calculateSpeeds(float speed) {
        if (speed <= 0) return new SpeedResult(0F, 0F);

        var flySpeed = speed / 10F;
        flySpeed = Math.clamp(flySpeed, 0F, 1F);
        var walkSpeed = 0.2F + (speed - 1) * 0.0888889F;
        walkSpeed = Math.clamp(walkSpeed, 0F, 1F);

        return new SpeedResult(walkSpeed, flySpeed);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Speed.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, arguments)) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, 1, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[1]).build();
            return;
        }
        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Speed.Other", targetPlayer.getName())) return;

        var speedType = switch (command.getName().toLowerCase()) {
            case "flyspeed" -> "Fly";
            case "walkspeed" -> "Walk";
            default -> targetPlayer.isFlying()? "Fly" : "Walk";
        };

        Float speed;
        try {
            speed = Float.parseFloat(arguments[0]);
            speed = Math.max(0, speed);
            speed = Math.min(10, speed);
        } catch (NumberFormatException ignored) {
            command("Speed.InvalidSpeed", commandSender).target(targetPlayer.getName()).build();
            return;
        }

        var speedTuple = calculateSpeeds(speed);

        speed = switch (speedType) {
            case "Fly" -> speedTuple.flySpeed();
            case "Walk" -> speedTuple.walkSpeed();
            default -> throw new IllegalStateException("Unexpected value: ${speedType}");
        };

        if (speedType.equalsIgnoreCase("Fly")) targetPlayer.setFlySpeed(speed);
        else targetPlayer.setWalkSpeed(speed);

        var messagePath = isSelf? "Speed.Success" : "Speed.SuccessOther";
        command(messagePath, commandSender).target(targetPlayer.getName())
                .postModifier(message -> message.replace("<SPEED>", arguments[0])).build();

        if (isSelf) return;
        command("Speed.Success", targetUser).target(targetPlayer.getName()).sender(commandSender.getName().get())
                .postModifier(message -> message.replace("<SPEED>", arguments[0])).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Speed";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Speed.Use", false);
    }

    private record SpeedResult(float walkSpeed, float flySpeed) {

    }
}
