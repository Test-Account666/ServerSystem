package me.testaccount666.serversystem.placeholderapi.executables;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.placeholderapi.Placeholder;
import me.testaccount666.serversystem.userdata.OfflineUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BalancePlaceholder implements Placeholder {
    @Override
    public String execute(@Nullable OfflineUser user, String identifier, String... arguments) {
        if (user == null && arguments.length == 0) return "No user specified!";
        if (user == null || arguments.length >= 1) {
            var player = getOfflinePlayer(arguments[0]);
            var userOptional = getOfflineUser(player);
            if (userOptional.isEmpty()) return "User ${arguments[0]}} not found!";

            user = userOptional.get();
        }

        var bankAccount = user.getBankAccount();
        var balance = bankAccount.getBalance();

        var formatBalance = identifier.equalsIgnoreCase("balance");
        if (!formatBalance) return String.format("%.2f", balance.doubleValue());

        return ServerSystem.Instance.getEconomyProvider().formatMoney(balance);
    }

    @Override
    public Set<String> getIdentifiers() {
        return Set.of("balance", "unformattedbalance");
    }

    private OfflinePlayer getOfflinePlayer(String nameOrUuid) {
        try {
            var uuid = UUID.fromString(nameOrUuid);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException ignored) {
        }
        return Bukkit.getOfflinePlayer(nameOrUuid);
    }

    private Optional<OfflineUser> getOfflineUser(OfflinePlayer player) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(player.getUniqueId());
        if (userOptional.isEmpty()) return Optional.empty();
        var cachedUser = userOptional.get();
        return Optional.of(cachedUser.getOfflineUser());
    }
}
