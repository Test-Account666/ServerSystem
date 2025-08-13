package me.testaccount666.migration.essentials;

import com.earth2me.essentials.Essentials;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.UserManager;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;

import java.util.logging.Level;

import static me.testaccount666.migration.essentials.EssentialsMigrator.ensureUserDataExists;

public class BalanceMigrator {

    public int migrateFrom() {
        var count = 0;

        var userManager = ServerSystem.Instance.getRegistry().getService(UserManager.class);
        var essentials = Essentials.getPlugin(Essentials.class);
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
            count += 1;
        }

        return count;
    }

    public int migrateTo() {
        var count = 0;

        var userManager = ServerSystem.Instance.getRegistry().getService(UserManager.class);

        for (var player : Bukkit.getOfflinePlayers()) {
            var uuid = player.getUniqueId();

            var userOptional = userManager.getUser(uuid);
            if (userOptional.isEmpty()) {
                ServerSystem.getLog().warning("Couldn't find user '${uuid}', skipping balance migration!");
                continue;
            }

            var user = userOptional.get().getOfflineUser();
            var bankAccount = user.getBankAccount();

            var essentials = Essentials.getPlugin(Essentials.class);

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
