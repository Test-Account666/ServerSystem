package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class AbstractEconomyManager_SQL extends AbstractEconomyManager {
    protected Connection _connection;

    public AbstractEconomyManager_SQL(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                                      String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
        this.Initialize();
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

    protected abstract boolean Initialize();

    public abstract void Open();

    @Override
    public Double GetMoneyAsNumber(OfflinePlayer offlinePlayer) {
        this.Open();
        if (offlinePlayer == null)
            return 0.0D;

        if (Bukkit.isPrimaryThread() && this._moneyCache.containsKey(offlinePlayer))
            return this._moneyCache.get(offlinePlayer);

        try {
            var statement = this._connection.createStatement();
            var resultSet = statement.executeQuery("SELECT * FROM Economy WHERE UUID = '" + offlinePlayer.getUniqueId() + "'");
            try {
                if (resultSet.isClosed()) {
                    if (!statement.isClosed())
                        statement.close();

                    return 0.0D;
                }

                if (!resultSet.next()) {
                    resultSet.close();

                    if (!statement.isClosed())
                        statement.close();

                    return 0.0D;
                }

                var money = Double.parseDouble(String.format("%.2f", resultSet.getDouble("Balance")).replace(",", "."));

                resultSet.close();

                if (!statement.isClosed())
                    statement.close();

                this._moneyCache.remove(offlinePlayer);
                this._moneyCache.put(offlinePlayer, money);
                return money;
            } catch (SQLException exception) {
                if (!exception.getMessage().toLowerCase().contains("closed"))
                    exception.printStackTrace();
                try {
                    if (!statement.isClosed())
                        statement.close();
                } catch (SQLException exception1) {
                    exception1.printStackTrace();
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0.0D;
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    @Override
    public void SetMoney(OfflinePlayer offlinePlayer, double amount) {
        this.Open();

        if (offlinePlayer == null)
            return;

        amount = Double.parseDouble(String.format("%.2f", amount).replace(",", "."));
        this.DeleteAccountSync(offlinePlayer);

        this._moneyCache.remove(offlinePlayer);
        this._moneyCache.put(offlinePlayer, amount);

        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("INSERT INTO `Economy` (UUID, Balance)" + " VALUES ('" + offlinePlayer.getUniqueId() + "','" + amount + "')");

            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (statement == null)
                return;

            try {
                if (statement.isClosed())
                    return;

                statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    @Override
    public void CreateAccount(OfflinePlayer offlinePlayer) {
        if (this.HasAccount(offlinePlayer))
            return;

        this.Open();

        if (offlinePlayer == null)
            return;

        this._moneyCache.remove(offlinePlayer);
        this._moneyCache.put(offlinePlayer, Double.valueOf(this._startingMoney));

        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("INSERT INTO `Economy` (UUID, Balance) VALUES ('" + offlinePlayer.getUniqueId() + "','" + this._startingMoney + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (statement == null)
                return;

            try {
                if (statement.isClosed())
                    return;

                statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void DeleteAccount(OfflinePlayer offlinePlayer) {
        if (!this.HasAccount(offlinePlayer))
            return;

        Bukkit.getScheduler().runTaskAsynchronously(this.GetPlugin(), () -> this.DeleteAccountSync(offlinePlayer));
    }

    @Override
    public boolean HasAccount(OfflinePlayer offlinePlayer) {
        this.Open();
        if (offlinePlayer == null)
            return false;

        try {
            var statement = this._connection.createStatement();
            var resultSet = statement.executeQuery("SELECT * FROM Economy WHERE UUID = '" + offlinePlayer.getUniqueId() + "'");
            if (resultSet == null) {
                if (statement.isClosed())
                    return false;

                statement.close();

                return false;
            }

            if (resultSet.isClosed() || !resultSet.next()) {
                if (statement.isClosed())
                    return false;

                statement.close();
                return false;
            }

            var uuid = resultSet.getString("UUID");
            resultSet.close();
            try {
                if (statement.isClosed())
                    return uuid != null;

                statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            return uuid != null;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public abstract void FetchTopTen();

    @Override
    public void Close() {
        try {
            if (this._connection.isClosed())
                return;

            this._connection.close();
        } catch (Exception ignored) {
        }
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
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (!statement.isClosed())
                    statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return topTen;
    }

    public void DeleteAccountSync(OfflinePlayer offlinePlayer) {
        this.Open();
        if (offlinePlayer == null)
            return;

        this._moneyCache.remove(offlinePlayer);

        Statement statement = null;
        try {
            this.Open();
            statement = this._connection.createStatement();
            statement.executeUpdate("DELETE FROM Economy WHERE UUID='" + offlinePlayer.getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (statement == null) {
                return;
            }

            try {
                if (!statement.isClosed())
                    statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
