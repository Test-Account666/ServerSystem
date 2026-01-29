package me.testaccount666.serversystem.commands.executables.pay

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
import java.math.RoundingMode

@ServerSystemCommand("pay")
class CommandPay : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Pay.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, *arguments)) return

        if (arguments.size < 2) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (isSelf) {
            command("Pay.CannotPaySelf", commandSender).build()
            return
        }

        val amount = arguments[1].toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_UP) ?: run {
            command("Pay.InvalidAmount", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        if (amount <= BigDecimal.ZERO) {
            command("Pay.InvalidAmount", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        val bankAccount = commandSender.bankAccount

        if (bankAccount.balance < amount) {
            command("Pay.NotEnoughMoney", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        bankAccount.transfer(amount, targetUser.bankAccount)
        val formattedAmount = getService<EconomyProvider>().formatMoney(amount)

        command("Pay.Success", commandSender) {
            target(targetPlayer.name)
            postModifier { it.replace("<AMOUNT>", formattedAmount) }
        }.build()

        command("Pay.SuccessOther", targetUser) {
            target(targetPlayer.name)
            sender(commandSender.getNameSafe())
            postModifier { it.replace("<AMOUNT>", formattedAmount) }
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Pay"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Pay.Use", false)
    }
}
