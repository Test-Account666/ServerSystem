package me.testaccount666.serversystem.placeholderapi.executables

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.placeholderapi.Placeholder
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class BalancePlaceholder : Placeholder {
    override fun execute(user: OfflineUser?, identifier: String, vararg arguments: String): String {
        var user = user
        if (user == null && arguments.isEmpty()) return "No user specified!"
        if (user == null || arguments.isNotEmpty()) {
            val player = getOfflinePlayer(arguments[0])
            val targetUser = getOfflineUser(player) ?: return "User ${arguments[0]} not found!"

            user = targetUser
        }

        val balance = user.bankAccount.balance

        val formatBalance = identifier.equals("balance", ignoreCase = true)
        if (!formatBalance) return String.format("%.2f", balance.toDouble())

        return instance.registry.getService<EconomyProvider>().formatMoney(balance)
    }

    override val identifiers = setOf("balance", "unformattedbalance")

    private fun getOfflinePlayer(nameOrUuid: String): OfflinePlayer {
        try {
            val uuid = UUID.fromString(nameOrUuid)
            return Bukkit.getOfflinePlayer(uuid)
        } catch (_: IllegalArgumentException) {
        }
        return Bukkit.getOfflinePlayer(nameOrUuid)
    }

    private fun getOfflineUser(player: OfflinePlayer): OfflineUser? {
        val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(player.uniqueId)
        return cachedUser?.offlineUser
    }
}
