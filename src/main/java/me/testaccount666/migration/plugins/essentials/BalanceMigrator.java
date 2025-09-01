package me.testaccount666.migration.plugins.essentials;

import me.testaccount666.serversystem.ServerSystem;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.UserBalanceUpdateEvent;

import java.util.logging.Level;

public class BalanceMigrator extends AbstractMigrator {

    @Override
    public int migrateFrom() {
        var count = 0;

        var userManager = userManager();
        var essentials = essentials();
        var uuids = essentials.getUsers().getAllUserUUIDs();

        for (var uuid : uuids) {
            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping balance migration!");
                continue;
            }

            var essentialsUser = essentials.getUser(uuid);
            var user = userOptional.get().getOfflineUser();
            var bankAccount = user.getBankAccount();

            bankAccount.setBalance(essentialsUser.getMoney());

            user.save();
            count += 1;
        }

        return count;
    }

    @Override
    public int migrateTo() {
        var count = 0;

        var userManager = userManager();

        for (var player : offlinePlayers()) {
            var uuid = player.getUniqueId();

            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping balance migration!");
                continue;
            }

            var user = userOptional.get().getOfflineUser();
            var bankAccount = user.getBankAccount();

            var essentials = essentials();

            ensureUserDataExists(uuid);
            var essentialsUser = essentials.getUser(uuid);

            try {
                essentialsUser.setMoney(bankAccount.getBalance(), UserBalanceUpdateEvent.Cause.SPECIAL);
                count += 1;
            } catch (MaxMoneyException exception) {
                var userName = user.getName().orElse("Unknown");

                ServerSystem.getLog().log(Level.WARNING, "Couldn't migrate balance for '${uuid}' (${userName}) with balance '${bankAccount.getBalance()}'", exception);
            }
        }

        return count;
    }
}
