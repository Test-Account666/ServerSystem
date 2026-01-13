package me.testaccount666.serversystem.commands.executables.balance

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("balance", ["baltop"])
class CommandBalance : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("baltop", true)) {
            executeBaltop(commandSender)
            return
        }

        executeBalance(commandSender, command, label, *arguments)
    }

    private fun executeBaltop(commandSender: User) {
        if (!checkBasePermission(commandSender, "Baltop.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val bankAccount = commandSender.bankAccount
        val topTen = bankAccount.topTen

        if (topTen.isEmpty()) {
            command("Baltop.NoData", commandSender).build()
            return
        }

        command("Baltop.Header", commandSender) { prefix(false) }.build()

        var position = 1
        for (entry in topTen.entries) {
            val playerUuid = entry.key
            val balance = entry.value
            val formattedBalance = instance.registry.getService<EconomyProvider>().formatMoney(balance)
            var playerName = Bukkit.getOfflinePlayer(playerUuid).name
            playerName = playerName ?: "Unknown"

            val currentPosition = position
            command("Baltop.Entry", commandSender) {
                prefix(false)
                target(playerName)
                postModifier {
                    it.replace("<POSITION>", currentPosition.toString())
                        .replace("<BALANCE>", formattedBalance)
                }
            }.build()
            position++
        }
    }

    private fun executeBalance(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Balance.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)

        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Balance.Other", targetPlayer.name)) return

        val balance = targetUser.bankAccount.balance
        val formattedBalance = instance.registry.getService<EconomyProvider>().formatMoney(balance)

        val messagePath = if (isSelf) "Balance.Success" else "Balance.SuccessOther"

        command(messagePath, commandSender) {
            target(targetPlayer.name)
            postModifier { message -> message.replace("<BALANCE>", formattedBalance) }
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String = "Balance"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Balance.Use", false)
    }
}
