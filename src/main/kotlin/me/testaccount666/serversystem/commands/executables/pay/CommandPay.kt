package me.testaccount666.serversystem.commands.executables.pay

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import org.bukkit.command.Command
import java.math.BigDecimal
import java.math.RoundingMode

@ServerSystemCommand("pay")
class CommandPay : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 2
    override fun getUsagePermission(command: Command) = "Pay.Use"
    override fun getSyntaxPath(command: Command?) = "Pay"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (isSelf) {
            commandSender.commandMsg("Pay.CannotPaySelf")
            return
        }

        val amount = arguments[1].toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_UP) ?: run {
            commandSender.commandMsg("Pay.InvalidAmount") { target(targetPlayer.name) }
            return
        }

        if (amount <= BigDecimal.ZERO) {
            commandSender.commandMsg("Pay.InvalidAmount") { target(targetPlayer.name) }
            return
        }

        val bankAccount = commandSender.bankAccount

        if (bankAccount.balance < amount) {
            commandSender.commandMsg("Pay.NotEnoughMoney") { target(targetPlayer.name) }
            return
        }

        bankAccount.transfer(amount, targetUser.bankAccount)
        val formattedAmount = getService<EconomyProvider>().formatMoney(amount)

        commandSender.commandMsg("Pay.Success") {
            target(targetPlayer.name)
            postModifier { it.replace("<AMOUNT>", formattedAmount) }
        }

        targetUser.commandMsg("Pay.SuccessOther") {
            target(targetPlayer.name)
            sender(commandSender.nameSafe)
            postModifier { it.replace("<AMOUNT>", formattedAmount) }
        }
    }
}
