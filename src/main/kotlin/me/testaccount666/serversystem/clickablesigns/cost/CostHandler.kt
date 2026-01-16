package me.testaccount666.serversystem.clickablesigns.cost

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.configuration.file.FileConfiguration
import java.math.BigDecimal
import java.util.Locale.getDefault
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
    fun canAfford(user: User, config: FileConfiguration): Boolean {
        val costType = getCostType(config)
        if (costType == CostType.NONE) return true

        val costAmount = config.getDouble("Cost.Amount")
        if (costAmount <= 0) return true

        if (costType == CostType.EXP) return user.getPlayer()!!.calculateTotalExperiencePoints() >= costAmount
        if (costType == CostType.ECONOMY) {
            val bankAccount = user.bankAccount
            return bankAccount.hasEnoughMoney(BigDecimal.valueOf(costAmount))
        }

        return false
    }

    fun refundCost(user: User, config: FileConfiguration) {
        val costType = getCostType(config)
        if (costType == CostType.NONE) return

        val costAmount = config.getDouble("Cost.Amount")
        if (costAmount <= 0) return

        if (costType == CostType.EXP) {
            val player = user.getPlayer()
            player!!.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() + costAmount.toInt())
            return
        }
        if (costType == CostType.ECONOMY) {
            val bankAccount = user.bankAccount

            try {
                bankAccount.deposit(BigDecimal.valueOf(costAmount))
                bankAccount.save()
            } catch (exception: Exception) {
                log.log(Level.SEVERE, "Failed to refund cost for '${user.getNameOrNull()}', failed to save bank account", exception)
                general("ErrorOccurred", user).build()
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

        if (!canAfford(user, config)) {
            if (costType == CostType.EXP) sign("Cost.NotEnoughExp", user) {
                postModifier { it.replace("<AMOUNT>", costAmount.toInt().toString()) }
            }.build()
            else if (costType == CostType.ECONOMY) sign("Cost.NotEnoughMoney", user) {
                postModifier { it.replace("<AMOUNT>", costAmount.toString()) }
            }.build()
            return false
        }

        if (costType == CostType.EXP) {
            val player = user.getPlayer()
            player!!.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() - costAmount.toInt())
            sign("Cost.PaidExp", user) {
                postModifier { it.replace("<AMOUNT>", costAmount.toInt().toString()) }
            }.build()
            return true
        }
        if (costType == CostType.ECONOMY) {
            val bankAccount = user.bankAccount

            try {
                bankAccount.withdraw(BigDecimal.valueOf(costAmount))
                bankAccount.save()
                sign("Cost.PaidMoney", user) {
                    postModifier { it.replace("<AMOUNT>", costAmount.toString()) }
                }.build()
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
        val costTypeStr: String = config.getString("Cost.Type", "NONE")!!
        return try {
            CostType.valueOf(costTypeStr.uppercase(getDefault()))
        } catch (_: IllegalArgumentException) {
            CostType.NONE
        }
    }
}