package me.testaccount666.serversystem.clickablesigns.cost

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import org.bukkit.configuration.file.FileConfiguration
import java.util.logging.Level

/**
 * Handles cost validation and deduction for sign usage.
 */
object CostHandler {
    /**
     * Checks if a user can afford the cost specified in the configuration.
     *
     * @param user   The user to check
     * @param config The configuration containing cost information
     * @return true if the user can afford the cost, false otherwise
     */
    fun canAfford(user: User, config: FileConfiguration, sendMessage: Boolean = true): Boolean {
        val costType = getCostType(config)
        if (costType == CostType.NONE) return true

        val costAmount = config.getDouble("Cost.Amount")
        if (costAmount <= 0) return true

        val canAfford = when (costType) {
            CostType.EXP -> user.getPlayer()!!.calculateTotalExperiencePoints() >= costAmount
            CostType.ECONOMY -> user.bankAccount.balance >= costAmount.toBigDecimal()
        }

        if (!canAfford && sendMessage) {
            if (costType == CostType.EXP) user.signMsg("Cost.NotEnoughExp") {
                postModifier { it.replace("<AMOUNT>", costAmount.toInt().toString()) }
            }
            else if (costType == CostType.ECONOMY) user.signMsg("Cost.NotEnoughMoney") {
                postModifier { it.replace("<AMOUNT>", costAmount.toString()) }
            }
        }

        return canAfford
    }

    fun refundCost(user: User, config: FileConfiguration) {
        val costType = getCostType(config)
        if (costType == CostType.NONE) return

        val costAmount = config.getDouble("Cost.Amount")
        if (costAmount <= 0) return

        if (costType == CostType.EXP) {
            val player = user.getPlayer()!!
            player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() + costAmount.toInt())
            return
        }
        if (costType == CostType.ECONOMY) {
            val bankAccount = user.bankAccount

            try {
                bankAccount.balance += costAmount.toBigDecimal()
                bankAccount.save()
            } catch (exception: Exception) {
                log.log(Level.SEVERE, "Failed to refund cost for '${user.nameSafe}', failed to save bank account", exception)
                user.generalMsg("ErrorOccurred")
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
    fun deductCost(user: User, config: FileConfiguration): Boolean {
        val costType = getCostType(config)
        if (costType == CostType.NONE) return true

        val costAmount = config.getDouble("Cost.Amount")
        if (costAmount <= 0) return true

        if (!canAfford(user, config, false)) return false

        if (costType == CostType.EXP) {
            val player = user.getPlayer()!!
            player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() - costAmount.toInt())
            user.signMsg("Cost.PaidExp") {
                postModifier { it.replace("<AMOUNT>", costAmount.toInt().toString()) }
            }
            return true
        }
        if (costType == CostType.ECONOMY) {
            val bankAccount = user.bankAccount

            try {
                bankAccount.balance -= costAmount.toBigDecimal()
                bankAccount.save()
                user.signMsg("Cost.PaidMoney") {
                    postModifier { it.replace("<AMOUNT>", costAmount.toString()) }
                }
                return true
            } catch (_: Exception) {
                return false
            }
        }

        return false
    }

    /**
     * Gets the cost type from the configuration.
     *
     * @param config The configuration
     * @return The cost type
     */
    fun getCostType(config: FileConfiguration): CostType {
        return config.getEnum("Cost.Type", CostType.NONE)
    }
}