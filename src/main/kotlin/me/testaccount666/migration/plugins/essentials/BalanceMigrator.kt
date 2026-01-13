package me.testaccount666.migration.plugins.essentials

import me.testaccount666.serversystem.ServerSystem.Companion.log
import net.ess3.api.MaxMoneyException
import net.ess3.api.events.UserBalanceUpdateEvent
import java.util.logging.Level

class BalanceMigrator : AbstractMigrator() {
    override fun migrateFrom(): Int {
        var count = 0

        val userManager = userManager
        val essentials = essentials
        val uuids = essentials.users.allUserUUIDs

        for (uuid in uuids) {
            val userOptional = userManager.getUserOrNull(uuid)
            if (userOptional == null) {
                log.warning("Couldn't find user '${uuid}', skipping balance migration!")
                continue
            }

            val essentialsUser = essentials.getUser(uuid)
            val user = userOptional.offlineUser
            val bankAccount = user.bankAccount

            bankAccount.balance = essentialsUser.money

            user.save()
            count += 1
        }

        return count
    }

    override fun migrateTo(): Int {
        var count = 0

        val userManager = userManager

        for (player in offlinePlayers()) {
            val uuid = player.uniqueId

            val userOptional = userManager.getUserOrNull(uuid)
            if (userOptional == null) {
                log.warning("Couldn't find user '${uuid}', skipping balance migration!")
                continue
            }

            val user = userOptional.offlineUser
            val bankAccount = user.bankAccount

            val essentials = essentials

            ensureUserDataExists(uuid)
            val essentialsUser = essentials.getUser(uuid)

            try {
                essentialsUser.setMoney(bankAccount.balance, UserBalanceUpdateEvent.Cause.SPECIAL)
                count += 1
            } catch (exception: MaxMoneyException) {
                val userName = user.getNameSafe()

                log.log(
                    Level.WARNING,
                    "Couldn't migrate balance for '${uuid}' (${userName}) with balance '${bankAccount.balance}'",
                    exception
                )
            }
        }

        return count
    }
}
