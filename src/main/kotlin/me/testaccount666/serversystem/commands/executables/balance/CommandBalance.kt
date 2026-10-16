package me.testaccount666.serversystem.commands.executables.balance

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("balance", ["baltop"])
class CommandBalance : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command): String {
        if (command.name.equals("balance", true)) return "Balance.Use"
        return "Baltop.Use"
    }

    override fun getSyntaxPath(command: Command?): String {
        command ?: return "Balance"
        return if (command.name.equals("baltop", true)) "Generic" else "Balance"
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("baltop", true)) {
            executeBaltop(commandSender)
            return
        }

        executeBalance(commandSender, command, label, *arguments)
    }

    private fun executeBaltop(commandSender: User) {
        if (!isPlayer(commandSender)) return

        val bankAccount = commandSender.bankAccount
        val topTen = bankAccount.topTen.takeIf { it.isNotEmpty() } ?: run {
            commandSender.commandMsg("Baltop.NoData")
            return
        }

        commandSender.commandMsg("Baltop.Header") { prefix(false) }

        var position = 1
        topTen.entries.forEach { (playerUuid, balance) ->
            val formattedBalance = getService<EconomyProvider>().formatMoney(balance)
            val playerName = Bukkit.getOfflinePlayer(playerUuid).name ?: "Unknown"

            commandSender.commandMsg("Baltop.Entry") {
                prefix(false)
                target(playerName)
                postModifier {
                    it.replace("<POSITION>", position.toString())
                        .replace("<BALANCE>", formattedBalance)
                }
            }
            position++
        }
    }

    private fun executeBalance(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Balance.Other", targetPlayer.name)) return

        val balance = targetUser.bankAccount.balance
        val formattedBalance = getService<EconomyProvider>().formatMoney(balance)

        val messagePath = if (isSelf) "Balance.Success" else "Balance.SuccessOther"

        commandSender.commandMsg(messagePath) {
            target(targetPlayer.name)
            postModifier { it.replace("<BALANCE>", formattedBalance) }
        }
    }
}
