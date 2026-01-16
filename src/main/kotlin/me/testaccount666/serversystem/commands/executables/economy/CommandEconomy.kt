package me.testaccount666.serversystem.commands.executables.economy

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.util.Locale.getDefault

@ServerSystemCommand("economy", [], TabCompleterEconomy::class)
class CommandEconomy : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Economy.Use")) return

        if (arguments.size <= 2) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, 1, false, *arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[1]) }.build()
            return
        }

        val amount: BigDecimal
        try {
            amount = BigDecimal(arguments[2])
        } catch (_: NumberFormatException) {
            command("Economy.InvalidAmount", commandSender).build()
            return
        }

        val economyOperation = arguments[0].lowercase(getDefault())

        when (economyOperation) {
            "set" -> handleSetEconomy(commandSender, targetUser, amount)
            "give", "add" -> handleGiveEconomy(commandSender, targetUser, amount)
            "take", "remove" -> handleTakeEconomy(commandSender, targetUser, amount)
            else -> general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
        }
    }

    private fun handleSetEconomy(commandSender: User, targetUser: User, amount: BigDecimal) {
        if (!checkBasePermission(commandSender, "Economy.Set")) return

        targetUser.bankAccount.balance = amount
        sendSuccess(commandSender, targetUser, amount, "Set")
    }

    private fun handleGiveEconomy(commandSender: User, targetUser: User, amount: BigDecimal) {
        if (!checkBasePermission(commandSender, "Economy.Give")) return

        targetUser.bankAccount.deposit(amount)
        sendSuccess(commandSender, targetUser, amount, "Give")
    }

    private fun handleTakeEconomy(commandSender: User, targetUser: User, amount: BigDecimal) {
        if (!checkBasePermission(commandSender, "Economy.Take")) return

        targetUser.bankAccount.withdraw(amount)
        sendSuccess(commandSender, targetUser, amount, "Take")
    }

    fun sendSuccess(commandSender: User, targetUser: User, amount: BigDecimal, key: String) {
        val formattedAmount = instance.registry.getService<EconomyProvider>().formatMoney(amount)
        val modifier = { message: String -> message.replace("<AMOUNT>", formattedAmount) }

        command("Economy.${key}.Success", commandSender) {
            target(targetUser.getNameOrNull())
            postModifier(modifier)
        }.build()

        command("Economy.${key}.SuccessOther", targetUser) {
            sender(commandSender.getNameSafe())
            target(targetUser.getNameOrNull())
            postModifier(modifier)
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Economy"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Economy.Use", false)
    }
}
