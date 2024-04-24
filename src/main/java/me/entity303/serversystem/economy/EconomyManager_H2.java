package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;

public class EconomyManager_H2 extends AbstractEconomyManager {
    private Connection _connection;

    public EconomyManager_H2(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator,
                             String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
        this.Open();
        try {
            this._connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100), Balance DECIMAL(30, 2))");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean Open() {
        if (this.Initialize())
            try {
                this._connection = DriverManager.getConnection("jdbc:h2:file:" + new File("plugins//ServerSystem", "economy.h2").getAbsolutePath());
                return true;
            } catch (SQLException var2) {
                this._plugin.Error("Could not establish an H2 connection, SQLException: " + var2.getMessage());
                return false;
            }
        else
            return false;
    }

    protected boolean Initialize() {
        try {
            Class.forName("org.h2.Driver");
            return true;
        } catch (ClassNotFoundException var2) {
            this._plugin.Error("H2 driver class missing: " + var2.getMessage() + ".");
            return false;
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
        this._moneyCache.remove(player);
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));

        this.DeleteAccountSync(player);
        try {
            var query = "INSERT INTO Economy (UUID, Balance) VALUES (?, ?)";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setBigDecimal(2, new BigDecimal(String.valueOf(doubles[0])));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void RemoveMoney(OfflinePlayer player, double amount) {
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));

        var balance = this.GetMoneyAsNumber(player) - doubles[0];
        this.DeleteAccountSync(player);
        try {
            var query = "INSERT INTO Economy (UUID, Balance) VALUES (?, ?)";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setBigDecimal(2, new BigDecimal(balance));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this._moneyCache.remove(player);

    }

    @Override
    public void AddMoney(OfflinePlayer player, double amount) {
        var doubles = new double[] { amount };

        if (player == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));

        var balance = this.GetMoneyAsNumber(player) + doubles[0];
        this.DeleteAccountSync(player);
        try {
            var query = "INSERT INTO Economy (UUID, Balance) VALUES (?, ?)";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setBigDecimal(2, new BigDecimal(balance));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this._moneyCache.remove(player);

    }

    @Override
    public void CreateAccount(OfflinePlayer player) {
        if (this.HasAccount(player))
            return;
        this._moneyCache.put(player, Double.valueOf(this._startingMoney));
        this._accountCache.put(player, true);

        if (player == null)
            return;
        try {
            var query = "INSERT INTO Economy (UUID, Balance) VALUES (?, ?)";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setBigDecimal(2, new BigDecimal(this._startingMoney));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void DeleteAccount(OfflinePlayer player) {
        if (!this.HasAccount(player))
            return;
        this._moneyCache.remove(player);
        this._accountCache.put(player, false);

        if (player == null)
            return;
        try {
            var query = "DELETE FROM Economy WHERE UUID=?";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void DeleteAccountSync(OfflinePlayer player) {
        if (player == null)
            return;
        try {
            var query = "DELETE FROM Economy WHERE UUID=?";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
        if (Bukkit.isPrimaryThread())
            if (this._moneyCache.containsKey(player))
                return this._moneyCache.get(player);
        if (player == null)
            return 0.0D;
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
            this._moneyCache.put(player, money);
            return money;
        } catch (SQLException exception) {
            if (!exception.getMessage().toLowerCase().contains("closed"))
                exception.printStackTrace();
        }
        return 0.0D;
    }

    @Override
    public String GetMoney(OfflinePlayer player) {
        return this.Format(this.GetMoneyAsNumber(player));
    }

    @Override
    public boolean HasAccount(OfflinePlayer player) {
        if (Bukkit.isPrimaryThread())
            if (this._accountCache.containsKey(player))
                return this._accountCache.get(player);
        if (player == null)
            return false;
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM Economy WHERE UUID = '" + player.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (resultSet == null)
                return false;
            try {
                if (resultSet.isClosed())
                    return false;
            } catch (AbstractMethodError ignored) {
            }
            if (!resultSet.next())
                return false;
            var has = resultSet.getString("UUID") != null;
            this._accountCache.put(player, has);
            return has;
        } catch (SQLException throwables) {
            if (!throwables.getMessage().toLowerCase().contains("closed"))
                throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> GetTopTen() {
        var topTen = new LinkedHashMap<OfflinePlayer, Double>();
        try {
            var resultSet = this._connection.createStatement().executeQuery("SELECT * " + "FROM Economy " + "ORDER BY Balance desc " + "LIMIT 10");
            while (resultSet.next()) {
                var uuid = UUID.fromString(resultSet.getString("UUID"));
                Double balance = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
                topTen.put(Bukkit.getOfflinePlayer(uuid), balance);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return topTen;
    }

    @Override
    public void FetchTopTen() {
    }

    @Override
    public void Close() {
        try {
            this._connection.close();
        } catch (Exception ignored) {
        }
    }
}
