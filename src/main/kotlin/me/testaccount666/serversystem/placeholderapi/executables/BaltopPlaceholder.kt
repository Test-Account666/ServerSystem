package me.testaccount666.serversystem.placeholderapi.executables

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.placeholderapi.Placeholder
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.math.BigDecimal
import java.util.*

class BaltopPlaceholder : Placeholder {
    override fun execute(user: OfflineUser?, identifier: String, vararg arguments: String): String {
        var user = user
        if (user == null) user = consoleUser
        if (arguments.isEmpty()) return "Name or Balance not specified!"

        val type: String = arguments[0]

        val newArguments = arguments.copyOfRange(1, arguments.size)

        if (type.equals("name", ignoreCase = true)) return executeName(user, *newArguments)
        if (type.equals("balance", ignoreCase = true)) return executeBalance(user, true, *newArguments)
        if (type.equals("unformattedbalance", ignoreCase = true)) return executeBalance(user, false, *newArguments)

        return "Invalid type '${type}'"
    }

    private fun executeName(user: OfflineUser, vararg arguments: String): String {
        if (arguments.isEmpty()) return "No place specified!"
        val placeString: String = arguments[0]
        var place: Int

        try {
            place = placeString.toInt()
            if (place < 1) return "Invalid place '${placeString}', must be greater than 0!"
            if (place > 10) return "Invalid place '${placeString}', must be less than 11!"
        } catch (_: NumberFormatException) {
            return "Invalid place '${placeString}'"
        }

        val bankAccount = user.bankAccount

        val topTen = bankAccount.topTen
        var uuid: UUID? = null
        var count = 0
        for (top in topTen.keys) {
            if (count != place - 1) {
                count++
                continue
            }

            uuid = top
            break
        }

        if (uuid == null) uuid = topTen.keys.stream().toList().last()
        val targetUser = getOfflineUser(Bukkit.getOfflinePlayer(uuid)) ?: return "User ${uuid} not found!"
        val name = targetUser.getNameOrNull()

        return name ?: "User ${uuid} has no name!"
    }

    private fun executeBalance(user: OfflineUser, format: Boolean, vararg arguments: String): String {
        if (arguments.isEmpty()) return "No place specified!"
        val placeString = arguments[0]
        var place: Int

        try {
            place = placeString.toInt()
            if (place < 1) return "Invalid place '${placeString}', must be greater than 0!"
            if (place > 10) return "Invalid place '${placeString}', must be less than 11!"
        } catch (_: NumberFormatException) {
            return "Invalid place '${placeString}'"
        }

        val bankAccount = user.bankAccount

        val topTen = bankAccount.topTen
        var balance: BigDecimal? = null
        var count = 0
        for (top in topTen.values) {
            if (count != place - 1) {
                count++
                continue
            }

            balance = top
            break
        }

        if (balance == null) balance = topTen.values.stream().toList().last()

        if (!format) return String.format("%.2f", balance!!.toDouble())

        return instance.registry.getService<EconomyProvider>().formatMoney(balance!!)
    }

    override val identifiers = setOf("baltop")

    private fun getOfflineUser(player: OfflinePlayer): OfflineUser? {
        val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(player.uniqueId)
        return cachedUser?.offlineUser
    }
}
