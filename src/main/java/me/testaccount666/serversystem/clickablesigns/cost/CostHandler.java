package me.testaccount666.serversystem.clickablesigns.cost;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;

import static java.util.logging.Level.SEVERE;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;
import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

/**
 * Handles cost validation and deduction for sign usage.
 */
public class CostHandler {
    /**
     * Checks if a user can afford the cost specified in the configuration.
     *
     * @param user   The user to check
     * @param config The configuration containing cost information
     * @return true if the user can afford the cost, false otherwise
     */
    public static boolean canAfford(User user, FileConfiguration config) {
        var costType = getCostType(config);
        if (costType == CostType.NONE) return true;

        var costAmount = config.getDouble("Cost.Amount", 0);
        if (costAmount <= 0) return true;

        if (costType == CostType.EXP) return user.getPlayer().getTotalExperience() >= costAmount;
        if (costType == CostType.ECONOMY) {
            var bankAccount = user.getBankAccount();
            if (bankAccount == null) return false;
            return bankAccount.hasEnoughMoney(BigDecimal.valueOf(costAmount));
        }

        return false;
    }

    public static void refundCost(User user, FileConfiguration config) {
        var costType = getCostType(config);
        if (costType == CostType.NONE) return;

        var costAmount = config.getDouble("Cost.Amount", 0);
        if (costAmount <= 0) return;

        if (costType == CostType.EXP) {
            var player = user.getPlayer();
            player.setTotalExperience(player.getTotalExperience() + (int) costAmount);
            return;
        }
        if (costType == CostType.ECONOMY) {
            var bankAccount = user.getBankAccount();
            if (bankAccount == null) return;

            try {
                bankAccount.deposit(BigDecimal.valueOf(costAmount));
                bankAccount.save();
            } catch (Exception exception) {
                ServerSystem.getLog().log(SEVERE, "Failed to refund cost for '${user.getName()}', failed to save bank account", exception);
                general("ErrorOccurred", user).build();
            }
        }
    }

    /**
     * Deducts the cost from the user.
     *
     * @param user   The user to deduct from
     * @param config The configuration containing cost information
     * @return true if the cost was successfully deducted, false otherwise
     */
    public static boolean deductCost(User user, FileConfiguration config) {
        var costType = getCostType(config);
        if (costType == CostType.NONE) return true;

        var costAmount = config.getDouble("Cost.Amount", 0);
        if (costAmount <= 0) return true;

        if (!canAfford(user, config)) {
            if (costType == CostType.EXP) sign("Cost.NotEnoughExp", user)
                    .postModifier(message -> message.replace("<COST>", String.valueOf((int) costAmount))).build();
            else if (costType == CostType.ECONOMY) sign("Cost.NotEnoughMoney", user)
                    .postModifier(message -> message.replace("<COST>", String.valueOf(costAmount))).build();
            return false;
        }

        if (costType == CostType.EXP) {
            var player = user.getPlayer();
            player.setTotalExperience(player.getTotalExperience() - (int) costAmount);
            sign("Cost.PaidExp", user)
                    .postModifier(message -> message.replace("<COST>", String.valueOf((int) costAmount))).build();
            return true;
        }
        if (costType == CostType.ECONOMY) {
            var bankAccount = user.getBankAccount();
            if (bankAccount == null) return false;

            try {
                bankAccount.withdraw(BigDecimal.valueOf(costAmount));
                bankAccount.save();
                sign("Cost.PaidMoney", user)
                        .postModifier(message -> message.replace("<COST>", String.valueOf(costAmount))).build();
                return true;
            } catch (Exception exception) {
                return false;
            }
        }

        return false;
    }

    /**
     * Gets the cost type from the configuration.
     *
     * @param config The configuration
     * @return The cost type
     */
    public static CostType getCostType(FileConfiguration config) {
        var costTypeStr = config.getString("Cost.Type", "NONE");
        try {
            return CostType.valueOf(costTypeStr.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return CostType.NONE;
        }
    }
}