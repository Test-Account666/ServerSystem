package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import net.ess3.api.events.UserBalanceUpdateEvent
import java.util.logging.Level

class BalanceMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        val count = essentials.users.allUserUUIDs.count { uuid ->
            runCatching {
                val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                    log.warning("Couldn't find user '${uuid}', skipping balance migration!")
                    return@count false
                }

                val essentialsUser = essentials.getUser(uuid)
                val user = cachedUser.offlineUser
                val bankAccount = user.bankAccount

                bankAccount.balance = essentialsUser.money

                user.save()
                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate balance for '${uuid}'", it) }.getOrDefault(false)
        }

        return count
    }

    override fun migrateTo(): Int {
        val count = offlinePlayers().count { player ->
            val uuid = player.uniqueId
            runCatching {
                val cachedUser = userManager.getUserOrNull(uuid) ?: run {
                    log.warning("Couldn't find user '${uuid}', skipping balance migration!")
                    return@count false
                }

                val user = cachedUser.offlineUser
                val bankAccount = user.bankAccount

                ensureUserDataExists(uuid)
                val essentialsUser = essentials.getUser(uuid)

                essentialsUser.setMoney(bankAccount.balance, UserBalanceUpdateEvent.Cause.SPECIAL)
                return@runCatching true
            }.onFailure { log.log(Level.WARNING, "Couldn't migrate balance for '${uuid}'", it) }.getOrDefault(false)
        }

        return count
    }
}
