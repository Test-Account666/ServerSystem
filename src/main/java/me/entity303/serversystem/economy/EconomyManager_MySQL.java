package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;

public class EconomyManager_MySQL extends ManagerEconomy {
    private final String currencySingular;
    private final String currencyPlural;
    private final String startingMoney;
    private final String displayFormat;
    private final String moneyFormat;
    private final String separator;
    private final String server;

    public EconomyManager_MySQL(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator,
                                String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
        this.currencySingular = currencySingular;
        this.currencyPlural = currencyPlural;
        this.startingMoney = startingMoney;
        this.displayFormat = displayFormat;
        this.moneyFormat = moneyFormat;
        this.separator = separator;
        this.server = this.getPlugin().getServerName();
    }

    @Override
    public String format(double money) {
        var moneyStr = String.format(Locale.US, "%1$,.2f", money);

        moneyStr = moneyStr.replace(",", "<THOUSAND>");

        var moneyString = moneyStr.split("\\.")[0] + "." + moneyStr.split("\\.")[1];
        String formattedMoney;
        var first = "0";
        var last = "00";
        try {
            first = moneyString.split("\\.")[0];
            last = moneyString.split("\\.")[1];
        } catch (Exception ignored) {

        }

        if (last.length() == 1)
            last = last + "0";
        formattedMoney = this.moneyFormat.replace("<FIRST>", first)
                                         .replace("<LAST>", last)
                                         .replace("<SEPARATOR>", this.separator)
                                         .replace("<THOUSAND>", this.getThousands());


        var plural = false;

        var moneyWorth = money;

        if (money < 0)
            moneyWorth = money * -1;

        if (moneyWorth < 1)
            plural = true;

        if (money > 1)
            plural = true;
        return this.displayFormat.replace("<MONEY>", formattedMoney).replace("<CURRENCY>", plural? this.currencyPlural : this.currencySingular);
    }

    @Override
    public boolean hasEnoughMoney(Player player, double amount) {
        return this.hasEnoughMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void makeTransaction(Player sender, Player target, double amount) {
        this.makeTransaction((OfflinePlayer) sender, target, amount);
    }

    @Override
    public void setMoney(Player player, double amount) {
        this.setMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void removeMoney(Player player, double amount) {
        this.removeMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void addMoney(Player player, double amount) {
        this.addMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void createAccount(Player player) {
        this.createAccount((OfflinePlayer) player);
    }

    @Override
    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        if (player == null)
            return false;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        return this.getMoneyAsNumber(player) >= amount;
    }

    @Override
    public void makeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {
        if (sender == null)
            return;
        if (target == null)
            return;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        this.removeMoney(sender, amount);
        this.addMoney(target, amount);
    }

    @Override
    public void setMoney(OfflinePlayer player, double amount) {
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        this.deleteAccountSync(player);
        this.getPlugin()
            .getMySQL()
            .executeUpdate(
                    "INSERT INTO `Economy` (UUID, Server, Balance)" + " VALUES ('" + player.getUniqueId() + "','" + this.server + "','" + doubles[0] + "')");

        this.moneyCache.remove(player);

    }

    @Override
    public void removeMoney(OfflinePlayer player, double amount) {
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        var balance = this.getMoneyAsNumber(player) - doubles[0];
        this.deleteAccountSync(player);
        this.getPlugin()
            .getMySQL()
            .executeUpdate("INSERT INTO `Economy` (UUID, Server, Balance)" + " VALUES ('" + player.getUniqueId() + "','" + this.server + "','" + balance + "')");
        this.moneyCache.remove(player);

    }

    @Override
    public void addMoney(OfflinePlayer player, double amount) {
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        var balance = this.getMoneyAsNumber(player) + doubles[0];
        this.deleteAccountSync(player);
        this.getPlugin()
            .getMySQL()
            .executeUpdate("INSERT INTO `Economy` (UUID, Server, Balance)" + " VALUES ('" + player.getUniqueId() + "','" + this.server + "','" + balance + "')");
        this.moneyCache.remove(player);

    }

    @Override
    public void createAccount(OfflinePlayer player) {
        if (this.hasAccount(player))
            return;
        this.accountCache.put(player, true);
        this.moneyCache.put(player, Double.valueOf(this.startingMoney));

        if (player == null)
            return;

        this.getPlugin()
            .getMySQL()
            .executeUpdate(
                    "INSERT INTO `Economy` (UUID, Server, Balance) VALUES ('" + player.getUniqueId() + "','" + this.server + "','" + this.startingMoney + "')");
    }

    @Override
    public void deleteAccount(OfflinePlayer player) {
        if (!this.hasAccount(player))
            return;

        if (player == null)
            return;
        this.getPlugin().getMySQL().executeUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'" + " AND Server = '" + this.server + "'");

        this.moneyCache.remove(player);
        this.accountCache.put(player, false);
    }

    private void deleteAccountSync(OfflinePlayer player) {
        if (player == null)
            return;
        this.getPlugin().getMySQL().executeUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'" + " AND Server = '" + this.server + "'");
    }

    @Override
    public Double getMoneyAsNumber(Player player) {
        return this.getMoneyAsNumber((OfflinePlayer) player);
    }

    @Override
    public String getMoney(Player player) {
        return this.getMoney((OfflinePlayer) player);
    }

    @Override
    public Double getMoneyAsNumber(OfflinePlayer player) {
        if (player == null)
            return 0.0D;
        if (Bukkit.isPrimaryThread())
            if (this.moneyCache.containsKey(player))
                return this.moneyCache.get(player);
        var resultSet =
                this.getPlugin().getMySQL().getResult("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "' AND Server = '" + this.server + "'");
        try {
            try {
                if (resultSet.isClosed())
                    return 0.0D;
            } catch (AbstractMethodError ignored) {
            }
            if (!resultSet.next())
                return 0.0D;
            Double money = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
            resultSet.close();
            this.moneyCache.put(player, money);
            return money;
        } catch (SQLException e) {
            if (!e.getMessage().toLowerCase().contains("closed"))
                e.printStackTrace();
        }
        return 0.0D;
    }

    @Override
    public String getMoney(OfflinePlayer player) {
        return this.format(this.getMoneyAsNumber(player));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        if (player == null)
            return false;
        if (Bukkit.isPrimaryThread())
            if (this.accountCache.containsKey(player))
                return this.accountCache.get(player);
        ResultSet rs;
        rs = this.plugin.getMySQL().getResult("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'" + " AND Server = '" + this.server + "'");
        try {
            if (rs == null)
                return false;
            try {
                if (rs.isClosed())
                    return false;
            } catch (AbstractMethodError ignored) {
            }
            if (!rs.next())
                return false;
            var uuid = rs.getString("UUID");
            rs.close();
            var has = uuid != null;
            this.accountCache.put(player, has);
            return has;
        } catch (SQLException throwables) {
            if (!throwables.getMessage().toLowerCase().contains("closed"))
                throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void fetchTopTen() {

    }

    @Override
    public void close() {
        this.getPlugin().getMySQL().close();
    }

    @Override
    public String getMoneyFormat() {
        return this.moneyFormat;
    }

    @Override
    public String getSeparator() {
        return this.separator;
    }

    @Override
    public String getStartingMoney() {
        return this.startingMoney;
    }

    @Override
    public String getDisplayFormat() {
        return this.displayFormat;
    }

    @Override
    public String getCurrencySingular() {
        return this.currencySingular;
    }

    @Override
    public String getCurrencyPlural() {
        return this.currencyPlural;
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> getTopTen() {
        var topTen = new LinkedHashMap<OfflinePlayer, Double>();
        ResultSet resultSet = null;
        try {
            resultSet = this.plugin.getMySQL()
                                   .getResult("SELECT * " + "FROM Economy " + "WHERE Server='" + this.server + "' " + "ORDER BY Balance desc " + "LIMIT 10");
            while (resultSet.next()) {
                var uuid = UUID.fromString(resultSet.getString("UUID"));
                Double balance = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
                topTen.put(Bukkit.getOfflinePlayer(uuid), balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    if (!resultSet.isClosed())
                        resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return topTen;
    }
}

