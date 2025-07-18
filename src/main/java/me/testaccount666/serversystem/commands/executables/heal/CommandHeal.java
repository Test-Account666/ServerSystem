package me.testaccount666.serversystem.commands.executables.heal;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "heal", variants = "feed")
public class CommandHeal extends AbstractServerSystemCommand {
    private final Set<PotionEffectType> _badEffectsSet = Set.of(PotionEffectType.INSTANT_DAMAGE, PotionEffectType.INFESTED,
            PotionEffectType.SLOWNESS, PotionEffectType.BAD_OMEN, PotionEffectType.DARKNESS, PotionEffectType.GLOWING,
            PotionEffectType.UNLUCK, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.MINING_FATIGUE,
            PotionEffectType.NAUSEA, PotionEffectType.OOZING, PotionEffectType.POISON, PotionEffectType.WITHER,
            PotionEffectType.WEAKNESS, PotionEffectType.LEVITATION);

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("heal")) {
            handleHealCommand(commandSender, command, label, arguments);
            return;
        }

        handleFeedCommand(commandSender, command, label, arguments);
    }

    private void handleFeedCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Feed.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Feed.Other", targetPlayer.getName())) return;

        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(20);

        var messagePath = isSelf? "Feed.Success" : "Feed.SuccessOther";

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("Feed.Success", targetUser)
                .sender(commandSender.getName().get()).target(targetPlayer.getName()).build();
    }

    private void handleHealCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Heal.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Heal.Other", targetPlayer.getName())) return;

        targetPlayer.setHealth(targetPlayer.getAttribute(Attribute.MAX_HEALTH).getValue());
        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(20);
        targetPlayer.setFireTicks(0);
        _badEffectsSet.forEach(targetPlayer::removePotionEffect);

        var messagePath = isSelf? "Heal.Success" : "Heal.SuccessOther";

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("Heal.Success", targetUser)
                .sender(commandSender.getName().get()).target(targetPlayer.getName()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "heal" -> "Heal";
            case "feed" -> "Feed";
            default -> throw new IllegalStateException("(CommandHeal) Unexpected value: ${commandName}");
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Heal.Use", false);
    }
}
