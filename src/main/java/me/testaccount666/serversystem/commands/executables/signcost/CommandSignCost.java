package me.testaccount666.serversystem.commands.executables.signcost;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import me.testaccount666.serversystem.clickablesigns.cost.CostType;
import me.testaccount666.serversystem.clickablesigns.util.SignUtils;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.logging.Level;

import static me.testaccount666.serversystem.utils.MessageBuilder.*;

@ServerSystemCommand(name = "signcost", tabCompleter = TabCompleterSignCost.class)
public class CommandSignCost extends AbstractServerSystemCommand {

    private static final Set<String> _COST_TYPES = Set.of("none", "exp", "economy");
    private static final int _MAX_DISTANCE = 5;

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "SignCost.Use")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length < 1) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var costTypeStr = arguments[0].toLowerCase();
        if (!_COST_TYPES.contains(costTypeStr)) {
            sign("Cost.InvalidType", commandSender)
                    .postModifier(message -> message.replace("<TYPES>", String.join(", ", _COST_TYPES))).build();
            return;
        }

        CostType costType;
        try {
            costType = CostType.valueOf(costTypeStr.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            sign("Cost.InvalidType", commandSender)
                    .postModifier(message -> message.replace("<TYPES>", String.join(", ", _COST_TYPES))).build();
            return;
        }

        var amount = 0.0D;
        if (costType != CostType.NONE) {
            if (arguments.length < 2) {
                general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
                return;
            }

            try {
                amount = Double.parseDouble(arguments[1]);
                if (amount <= 0) {
                    sign("Cost.InvalidAmount", commandSender).build();
                    return;
                }
            } catch (NumberFormatException ignored) {
                sign("Cost.InvalidAmount", commandSender).build();
                return;
            }
        }
        var player = commandSender.getPlayer();

        var targetBlock = player.getTargetBlock(null, _MAX_DISTANCE);
        if (!(targetBlock.getState() instanceof Sign sign)) {
            command("ClickableSigns.Cost.NotLookingAtSign", commandSender).build();
            return;
        }

        var signFile = SignUtils.getSignFile(sign.getLocation());
        if (!signFile.exists()) {
            sign("Cost.NotClickableSign", commandSender).build();
            return;
        }

        var config = YamlConfiguration.loadConfiguration(signFile);
        if (!config.contains("Key")) {
            sign("Cost.NotClickableSign", commandSender).build();
            return;
        }

        config.set("Cost.Type", costType.name());
        config.set("Cost.Amount", amount);

        try {
            config.save(signFile);
        } catch (IOException exception) {
            general("ErrorOccurred", commandSender).build();
            ServerSystem.getLog().log(Level.SEVERE, "Error occurred while saving sign cost config '${signFile.getAbsolutePath()}'", exception);
            return;
        }

        if (costType == CostType.NONE) {
            sign.setLine(3, "");
            sign.update();
            command("ClickableSigns.Cost.SetNone", commandSender).build();
            return;
        }

        String costLine;
        if (costType == CostType.EXP) costLine = "${(int)amount} EXP";
        else costLine = ServerSystem.Instance.getRegistry().getService(EconomyProvider.class).formatMoney(BigDecimal.valueOf(amount));

        sign.getSide(Side.FRONT).line(3, ComponentColor.translateToComponent("&6${costLine}"));
        sign.getSide(Side.BACK).line(3, ComponentColor.translateToComponent("&6${costLine}"));
        sign.update();

        var finalAmount = amount;
        sign("Cost.Set", commandSender)
                .postModifier(message -> message
                        .replace("<TYPE>", costType.name())
                        .replace("<AMOUNT>", String.valueOf(finalAmount))).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "SignCost";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "SignCost.Use", false);
    }
}