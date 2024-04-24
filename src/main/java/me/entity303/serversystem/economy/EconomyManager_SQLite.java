package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;

public class EconomyManager_SQLite extends AbstractEconomyManager {
    private Connection _connection;

    public EconomyManager_SQLite(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator,
                                 String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
        this.Open();
        try {
            var statement = this._connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100), Balance DECIMAL(30, 2))");
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            this.Close();
        }
    }

    public boolean Open() {
        if (this._connection != null)
            try {
                if (!this._connection.isClosed())
                    return true;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        if (this.Initialize())
            try {
                this._connection = DriverManager.getConnection("jdbc:sqlite:" + new File("plugins//ServerSystem", "economy.sqlite").getAbsolutePath());
                return true;
            } catch (SQLException var2) {
                this._plugin.Error("Could not establish an SQLite connection, SQLException: " + var2.getMessage());
                return false;
            }
        else
            return false;
    }

    protected boolean Initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (ClassNotFoundException var2) {
            this._plugin.Error("Class not found in initialize(): " + var2);
            return false;
        }
    }

    @Override
    public String Format(double money) {
        var moneyStr = String.format(Locale.US, "%1$,.2f", money);

        moneyStr = moneyStr.replace(",", "<THOUSAND>");

        var formattedMoney = GetFormattedMoney(moneyStr);


        var plural = false;

        var moneyWorth = money;

        if (money < 0)
            moneyWorth = money * -1;

        if (moneyWorth < 1)
            plural = true;

        if (money > 1)
            plural = true;
        return this._displayFormat.replace("<MONEY>", formattedMoney).replace("<CURRENCY>", plural? this._currencyPlural : this._currencySingular);
    }

    private String GetFormattedMoney(String moneyStr) {
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
        formattedMoney = this._moneyFormat.replace("<FIRST>", first)
                                          .replace("<LAST>", last)
                                          .replace("<SEPARATOR>", this._separator)
                                          .replace("<THOUSAND>", this.GetThousands());
        return formattedMoney;
    }

    @Override
    public boolean HasEnoughMoney(Player player, double amount) {
        return this.HasEnoughMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void MakeTransaction(Player sender, Player target, double amount) {
        this.MakeTransaction((OfflinePlayer) sender, target, amount);
    }

    @Override
    public void SetMoney(Player player, double amount) {
        this.SetMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void RemoveMoney(Player player, double amount) {
        this.RemoveMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void AddMoney(Player player, double amount) {
        this.AddMoney((OfflinePlayer) player, amount);
    }

    @Override
    public void CreateAccount(Player player) {
        this.CreateAccount((OfflinePlayer) player);
    }

    @Override
    public boolean HasEnoughMoney(OfflinePlayer player, double amount) {
        if (player == null)
            return false;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        return this.GetMoneyAsNumber(player) >= amount;
    }

    @Override
    public void MakeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {
        if (sender == null)
            return;
        if (target == null)
            return;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        this.RemoveMoney(sender, amount);
        this.AddMoney(target, amount);
    }

    @Override
    public void SetMoney(OfflinePlayer player, double amount) {
        this.Open();
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        this.DeleteAccountSync(player);
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("INSERT INTO `Economy` (UUID, Balance)" + " VALUES ('" + player.getUniqueId() + "','" + doubles[0] + "')");

            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }

    }

    @Override
    public void RemoveMoney(OfflinePlayer player, double amount) {
        this.Open();
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        var balance = this.GetMoneyAsNumber(player) - doubles[0];
        this.DeleteAccountSync(player);
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("INSERT INTO `Economy` (UUID, Balance)" + " VALUES ('" + player.getUniqueId() + "','" + balance + "')");
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }
    }

    @Override
    public void AddMoney(OfflinePlayer player, double amount) {
        this.Open();
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        var balance = this.GetMoneyAsNumber(player) + doubles[0];
        this.DeleteAccountSync(player);
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("INSERT INTO `Economy` (UUID, Balance)" + " VALUES ('" + player.getUniqueId() + "','" + balance + "')");

            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }

    }

    @Override
    public void CreateAccount(OfflinePlayer player) {
        if (this.HasAccount(player))
            return;

        this.Open();

        if (player == null)
            return;
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("INSERT INTO `Economy` (UUID, Balance) VALUES ('" + player.getUniqueId() + "','" + this._startingMoney + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }
    }

    @Override
    public void DeleteAccount(OfflinePlayer player) {
        if (!this.HasAccount(player))
            return;

        this.Open();


        if (player == null)
            return;
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }

    }

    public void DeleteAccountSync(OfflinePlayer player) {
        this.Open();
        if (player == null)
            return;
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }
    }

    @Override
    public Double GetMoneyAsNumber(Player player) {
        return this.GetMoneyAsNumber((OfflinePlayer) player);
    }

    @Override
    public String GetMoney(Player player) {
        return this.GetMoney((OfflinePlayer) player);
    }

    @Override
    public Double GetMoneyAsNumber(OfflinePlayer player) {
        this.Open();
        if (player == null)
            return 0.0D;
        ResultSet resultSet;
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            return 0.0D;
        }
        try {
            try {
                if (resultSet.isClosed()) {
                    if (statement != null)
                        try {
                            if (!statement.isClosed())
                                statement.close();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    return 0.0D;
                }
            } catch (AbstractMethodError ignored) {

            }
            if (!resultSet.next()) {
                resultSet.close();
                if (statement != null)
                    try {
                        if (!statement.isClosed())
                            statement.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                return 0.0D;
            }
            Double money = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
            resultSet.close();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            return money;
        } catch (SQLException exception) {
            if (!exception.getMessage().toLowerCase().contains("closed"))
                exception.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception1) {
                    exception1.printStackTrace();
                }
        }
        return 0.0D;
    }

    @Override
    public String GetMoney(OfflinePlayer player) {
        return this.Format(this.GetMoneyAsNumber(player));
    }

    @Override
    public boolean HasAccount(OfflinePlayer player) {
        this.Open();
        if (player == null)
            return false;
        ResultSet resultSet;
        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            return false;
        }
        try {
            if (resultSet == null) {
                if (statement != null)
                    try {
                        if (!statement.isClosed())
                            statement.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                return false;
            }
            try {
                if (resultSet.isClosed()) {
                    if (statement != null)
                        try {
                            if (!statement.isClosed())
                                statement.close();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    return false;
                }
            } catch (AbstractMethodError ignored) {
            }
            if (!resultSet.next()) {
                resultSet.close();
                if (statement != null)
                    try {
                        if (!statement.isClosed())
                            statement.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                return false;
            }
            var uuid = resultSet.getString("UUID");
            resultSet.close();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            return uuid != null;
        } catch (SQLException throwables) {
            if (!throwables.getMessage().toLowerCase().contains("closed"))
                throwables.printStackTrace();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }
        return false;
    }

    @Override
    public void FetchTopTen() {
    }

    @Override
    public void Close() {
        try {
            if (!this._connection.isClosed())
                this._connection.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public String GetMoneyFormat() {
        return this._moneyFormat;
    }

    @Override
    public String GetSeparator() {
        return this._separator;
    }

    @Override
    public String GetStartingMoney() {
        return this._startingMoney;
    }

    @Override
    public String GetDisplayFormat() {
        return this._displayFormat;
    }

    @Override
    public String GetCurrencySingular() {
        return this._currencySingular;
    }

    @Override
    public String GetCurrencyPlural() {
        return this._currencyPlural;
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> GetTopTen() {
        this.Open();
        var topTen = new LinkedHashMap<OfflinePlayer, Double>();
        Statement statement = null;
        try {
            statement = this._connection.createStatement();
            var resultSet = statement.executeQuery("SELECT * " + "FROM Economy " + "ORDER BY Balance desc " + "LIMIT 10");
            while (resultSet.next()) {
                var uuid = UUID.fromString(resultSet.getString("UUID"));
                Double balance = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
                topTen.put(Bukkit.getOfflinePlayer(uuid), balance);
            }

            resultSet.close();
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (statement != null)
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }
        return topTen;
    }
}
