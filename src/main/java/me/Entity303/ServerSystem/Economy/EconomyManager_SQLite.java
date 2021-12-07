package me.Entity303.ServerSystem.Economy;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;

public class EconomyManager_SQLite extends ManagerEconomy {
    private final String currencySingular;
    private final String currencyPlural;
    private final String startingMoney;
    private final String displayFormat;
    private final String moneyFormat;
    private final String separator;
    private Connection connection;

    public EconomyManager_SQLite(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator, String thousands, ss plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
        this.currencySingular = currencySingular;
        this.currencyPlural = currencyPlural;
        this.separator = separator;
        this.displayFormat = displayFormat;
        this.startingMoney = startingMoney;
        this.moneyFormat = moneyFormat;
        this.open();
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100), Balance DECIMAL(30, 2))");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected boolean initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (ClassNotFoundException var2) {
            this.plugin.error("Class not found in initialize(): " + var2);
            return false;
        }
    }

    public boolean open() {
        if (this.initialize()) try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File("plugins//ServerSystem", "economy.sqlite").getAbsolutePath());
            return true;
        } catch (SQLException var2) {
            this.plugin.error("Could not establish an SQLite connection, SQLException: " + var2.getMessage());
            return false;
        }
        else return false;
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
    public String format(double money) {
        String moneyStr = String.format(Locale.US, "%1$,.2f", money);

        moneyStr = moneyStr.replace(",", "<THOUSAND>");

        String moneyString = moneyStr.split("\\.")[0] + "." + moneyStr.split("\\.")[1];
        String formattedMoney;
        String first = "0";
        String last = "00";
        try {
            first = moneyString.split("\\.")[0];
            last = moneyString.split("\\.")[1];
        } catch (Exception ignored) {

        }

        if (last.length() == 1) last = last + "0";
        formattedMoney = this.moneyFormat.
                replace("<FIRST>", first).
                replace("<LAST>", last).
                replace("<SEPARATOR>", this.separator).
                replace("<THOUSAND>", this.getThousands());
        return this.displayFormat.replace("<MONEY>", formattedMoney).replace("<CURRENCY>", money >= 2 ? this.currencyPlural : this.currencySingular);
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
        if (player == null) return false;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        return this.getMoneyAsNumber(player) >= amount;
    }

    @Override
    public void makeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {
        if (sender == null) return;
        if (target == null) return;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        this.removeMoney(sender, amount);
        this.addMoney(target, amount);
    }

    @Override
    public void setMoney(OfflinePlayer player, double amount) {
        double[] doubles = new double[]{amount};

        if (player == null) return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        this.deleteAccountSync(player);
        try {
            this.connection.createStatement().executeUpdate("INSERT INTO `Economy` (UUID, Balance)"
                    + " VALUES ('" + player.getUniqueId() +
                    "','" + doubles[0] + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void removeMoney(OfflinePlayer player, double amount) {
        double[] doubles = new double[]{amount};

        if (player == null) return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        double balance = this.getMoneyAsNumber(player) - doubles[0];
        this.deleteAccountSync(player);
        try {
            this.connection.createStatement().executeUpdate("INSERT INTO `Economy` (UUID, Balance)"
                    + " VALUES ('" + player.getUniqueId() +
                    "','" + balance + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void addMoney(OfflinePlayer player, double amount) {
        double[] doubles = new double[]{amount};

        if (player == null) return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        double balance = this.getMoneyAsNumber(player) + doubles[0];
        this.deleteAccountSync(player);
        try {
            this.connection.createStatement().executeUpdate("INSERT INTO `Economy` (UUID, Balance)"
                    + " VALUES ('" + player.getUniqueId() + "','" + balance + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void createAccount(OfflinePlayer player) {
        if (this.hasAccount(player)) return;

        {
            if (player == null) return;
            try {
                this.connection.createStatement().executeUpdate("INSERT INTO `Economy` (UUID, Balance) VALUES ('" + player.getUniqueId() + "','" + this.startingMoney + "')");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public void deleteAccount(OfflinePlayer player) {
        if (!this.hasAccount(player)) return;

        {
            if (player == null) return;
            try {
                this.connection.createStatement().executeUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void deleteAccountSync(OfflinePlayer player) {
        if (player == null) return;
        try {
            this.connection.createStatement().executeUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
        if (player == null) return 0.0D;
        ResultSet resultSet = null;
        try {
            resultSet = this.connection.createStatement().executeQuery("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0.0D;
        }
        try {
            try {
                if (resultSet.isClosed()) return 0.0D;
            } catch (AbstractMethodError ignored) {

            }
            if (!resultSet.next()) return 0.0D;
            Double money = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
            resultSet.close();
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
        if (player == null) return false;
        ResultSet rs = null;
        try {
            rs = this.connection.createStatement().executeQuery("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        try {
            if (rs == null) return false;
            try {
                if (rs.isClosed()) return false;
            } catch (AbstractMethodError ignored) {
            }
            if (!rs.next()) return false;
            String uuid = rs.getString("UUID");
            rs.close();
            return uuid != null;
        } catch (SQLException throwables) {
            if (!throwables.getMessage().toLowerCase().contains("closed"))
                throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> getTopTen() {
        LinkedHashMap<OfflinePlayer, Double> topTen = new LinkedHashMap<>();
        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery(
                    "SELECT * " +
                    "FROM Economy " +
                    "ORDER BY Balance desc " +
                    "LIMIT 10");
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("UUID"));
                Double balance = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
                topTen.put(Bukkit.getOfflinePlayer(uuid), balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topTen;
    }

    @Override
    public void fetchTopTen() {

    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (Exception ignored) {
        }
    }
}
