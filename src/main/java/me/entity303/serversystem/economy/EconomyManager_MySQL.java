package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

public class EconomyManager_MySQL extends AbstractEconomyManager {
    private final String _server;

    public EconomyManager_MySQL(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                                String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
        this._server = this.GetPlugin().GetServerName();
    }

    @Override
    public boolean HasEnoughMoney(OfflinePlayer offlinePlayer, double amount) {
        if (offlinePlayer == null)
            return false;
        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        return this.GetMoneyAsNumber(offlinePlayer) >= amount;
    }

    @Override
    public Double GetMoneyAsNumber(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null)
            return 0.0D;
        if (Bukkit.isPrimaryThread())
            if (this._moneyCache.containsKey(offlinePlayer))
                return this._moneyCache.get(offlinePlayer);
        var resultSet = this.GetPlugin()
                            .GetMySQL()
                            .GetResult("SELECT * FROM Economy WHERE UUID = '" + offlinePlayer.getUniqueId() + "' AND Server = '" + this._server + "'");
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
            this._moneyCache.put(offlinePlayer, money);
            return money;
        } catch (SQLException exception) {
            if (!exception.getMessage().toLowerCase().contains("closed"))
                exception.printStackTrace();
        }
        return 0.0D;
    }

    @Override
    public void SetMoney(OfflinePlayer offlinePlayer, double amount) {
        var doubles = new double[] { amount };

        if (offlinePlayer == null)
            return;
        doubles[0] = Double.parseDouble(String.format("%.2f", doubles[0]).replace(",", "."));
        this.DeleteAccountSync(offlinePlayer);
        this.GetPlugin()
            .GetMySQL()
            .ExecuteUpdate(
                    "INSERT INTO `Economy` (UUID, Server, Balance)" + " VALUES ('" + offlinePlayer.getUniqueId() + "','" + this._server + "','" + doubles[0] +
                    "')");

        this._moneyCache.remove(offlinePlayer);

    }

    @Override
    public void CreateAccount(OfflinePlayer offlinePlayer) {
        if (this.HasAccount(offlinePlayer))
            return;
        this._accountCache.put(offlinePlayer, true);
        this._moneyCache.put(offlinePlayer, Double.valueOf(this._startingMoney));

        if (offlinePlayer == null)
            return;

        this.GetPlugin()
            .GetMySQL()
            .ExecuteUpdate(
                    "INSERT INTO `Economy` (UUID, Server, Balance) VALUES ('" + offlinePlayer.getUniqueId() + "','" + this._server + "','" + this._startingMoney +
                    "')");
    }

    @Override
    public void DeleteAccount(OfflinePlayer offlinePlayer) {
        if (!this.HasAccount(offlinePlayer))
            return;

        if (offlinePlayer == null)
            return;
        this.GetPlugin()
            .GetMySQL()
            .ExecuteUpdate("DELETE FROM Economy WHERE UUID='" + offlinePlayer.getUniqueId() + "'" + " AND Server = '" + this._server + "'");

        this._moneyCache.remove(offlinePlayer);
        this._accountCache.put(offlinePlayer, false);
    }

    @Override
    public boolean HasAccount(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null)
            return false;
        if (Bukkit.isPrimaryThread())
            if (this._accountCache.containsKey(offlinePlayer))
                return this._accountCache.get(offlinePlayer);
        ResultSet resultSet;
        resultSet = this._plugin.GetMySQL()
                                .GetResult("SELECT * FROM Economy WHERE UUID = '" + offlinePlayer.getUniqueId() + "'" + " AND Server = '" + this._server + "'");
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
            var uuid = resultSet.getString("UUID");
            resultSet.close();
            var has = uuid != null;
            this._accountCache.put(offlinePlayer, has);
            return has;
        } catch (SQLException throwables) {
            if (!throwables.getMessage().toLowerCase().contains("closed"))
                throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void FetchTopTen() {

    }

    @Override
    public void Close() {
        this.GetPlugin().GetMySQL().Close();
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> GetTopTen() {
        var topTen = new LinkedHashMap<OfflinePlayer, Double>();
        ResultSet resultSet = null;
        try {
            resultSet = this._plugin.GetMySQL()
                                    .GetResult("SELECT * " + "FROM Economy " + "WHERE Server='" + this._server + "' " + "ORDER BY Balance desc " +
                                               "LIMIT 10");
            while (resultSet.next()) {
                var uuid = UUID.fromString(resultSet.getString("UUID"));
                Double balance = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));
                topTen.put(Bukkit.getOfflinePlayer(uuid), balance);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    if (!resultSet.isClosed())
                        resultSet.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
        }
        return topTen;
    }

    private void DeleteAccountSync(OfflinePlayer player) {
        if (player == null)
            return;

        this.GetPlugin()
            .GetMySQL()
            .ExecuteUpdate("DELETE FROM Economy WHERE UUID='" + player.getUniqueId() + "'" + " AND Server = '" + this._server + "'");
    }
}

