package me.testaccount666.serversystem.commands.executables.economy

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.SimpleCompletion
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.AbstractBankAccount
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import org.bukkit.command.Command
import java.math.BigDecimal

@ServerSystemCommand(
    "economy", simpleCompletions = [
        SimpleCompletion(0, ["set", "give", "take"]),
        SimpleCompletion(1, isNull = true)
    ]
)
class CommandEconomy : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 3
    override fun getUsagePermission(command: Command) = "Economy.Use"
    override fun getSyntaxPath(command: Command?) = "Economy"

    private val actions: Map<String, Triple<String, AbstractBankAccount.(BigDecimal) -> Unit, String>> = mapOf(
        "set" to Triple("Economy.Set", { balance = it }, "Set"),

        "give" to Triple("Economy.Give", { balance += it }, "Give"),
        "add" to Triple("Economy.Give", { balance += it }, "Give"),

        "take" to Triple("Economy.Take", { balance -= it }, "Take"),
        "remove" to Triple("Economy.Take", { balance -= it }, "Take")
    )

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = getTargetUser(commandSender, 1, false, *arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[1]) }
            return
        }

        val amount = arguments[2].toBigDecimalOrNull() ?: run {
            commandSender.commandMsg("Economy.InvalidAmount")
            return
        }

        val (perm, action, key) = actions[arguments[0].lowercase()] ?: run {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        if (!checkPermission(commandSender, perm)) return
        action(targetUser.bankAccount, amount)
        sendSuccess(commandSender, targetUser, amount, key)
    }

    fun sendSuccess(commandSender: User, targetUser: User, amount: BigDecimal, key: String) {
        val formattedAmount = getService<EconomyProvider>().formatMoney(amount)
        val modifier = { message: String -> message.replace("<AMOUNT>", formattedAmount) }

        if (commandSender !== targetUser) {
            commandSender.commandMsg("Economy.${key}.Success") {
                target(targetUser.getNameOrNull())
                postModifier(modifier)
            }
        }

        targetUser.commandMsg("Economy.${key}.SuccessOther") {
            sender(commandSender.nameSafe)
            target(targetUser.getNameOrNull())
            postModifier(modifier)
        }
    }
}
