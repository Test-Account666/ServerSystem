package me.testaccount666.serversystem.placeholderapi.executables;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.placeholderapi.Placeholder;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BaltopPlaceholder implements Placeholder {

    @Override
    public String execute(@Nullable OfflineUser user, String identifier, String... arguments) {
        if (user == null) user = UserManager.getConsoleUser();
        if (arguments.length == 0) return "Name or Balance not specified!";

        var type = arguments[0];

        var newArguments = new String[arguments.length - 1];
        System.arraycopy(arguments, 1, newArguments, 0, newArguments.length);

        if (type.equalsIgnoreCase("name")) return executeName(user, newArguments);
        if (type.equalsIgnoreCase("balance")) return executeBalance(user, true, newArguments);
        if (type.equalsIgnoreCase("unformattedbalance")) return executeBalance(user, false, newArguments);

        return "Invalid type '${type}'";
    }

    private String executeName(OfflineUser user, String... arguments) {
        if (arguments.length == 0) return "No place specified!";
        var placeString = arguments[0];
        var place = -1;

        try {
            place = Integer.parseInt(placeString);
            if (place < 1) return "Invalid place '${placeString}', must be greater than 0!";
            if (place > 10) return "Invalid place '${placeString}', must be less than 11!";
        } catch (NumberFormatException ignored) {
            return "Invalid place '${placeString}'";
        }

        var bankAccount = user.getBankAccount();
        var topTen = bankAccount.getTopTen();
        UUID uuid = null;
        var count = 0;
        for (var top : topTen.keySet()) {
            if (count != place - 1) {
                count++;
                continue;
            }

            uuid = top;
            break;
        }

        if (uuid == null) uuid = topTen.keySet().stream().toList().getLast();
        var optionalUser = getOfflineUser(Bukkit.getOfflinePlayer(uuid));
        if (optionalUser.isEmpty()) return "User ${uuid} not found!";
        var offlineUser = optionalUser.get();
        var nameOptional = offlineUser.getName();

        return nameOptional.orElse("User ${uuid} has no name!");
    }

    private String executeBalance(OfflineUser user, boolean format, String... arguments) {
        if (arguments.length == 0) return "No place specified!";
        var placeString = arguments[0];
        var place = -1;

        try {
            place = Integer.parseInt(placeString);
            if (place < 1) return "Invalid place '${placeString}', must be greater than 0!";
            if (place > 10) return "Invalid place '${placeString}', must be less than 11!";
        } catch (NumberFormatException ignored) {
            return "Invalid place '${placeString}'";
        }

        var bankAccount = user.getBankAccount();
        var topTen = bankAccount.getTopTen();
        BigDecimal balance = null;
        var count = 0;
        for (var top : topTen.values()) {
            if (count != place - 1) {
                count++;
                continue;
            }

            balance = top;
            break;
        }

        if (balance == null) balance = topTen.values().stream().toList().getLast();

        if (!format) return String.format("%.2f", balance.doubleValue());

        return ServerSystem.Instance.getRegistry().getService(EconomyProvider.class).formatMoney(balance);
    }

    @Override
    public Set<String> getIdentifiers() {
        return Set.of("baltop");
    }

    private Optional<OfflineUser> getOfflineUser(OfflinePlayer player) {
        var userOptional = ServerSystem.Instance.getRegistry().getService(UserManager.class).getUser(player.getUniqueId());
        if (userOptional.isEmpty()) return Optional.empty();
        var cachedUser = userOptional.get();
        return Optional.of(cachedUser.getOfflineUser());
    }
}
