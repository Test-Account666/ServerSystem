package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractSqlBankAccount extends AbstractBankAccount {
    protected final AbstractEconomyDatabaseManager databaseManager;

    public AbstractSqlBankAccount(UUID owner, BigInteger accountId) {
        super(owner, accountId);
        databaseManager = ServerSystem.Instance.getRegistry().getService(AbstractEconomyDatabaseManager.class);
    }


    @Override
    public BigDecimal getBalance() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT Balance FROM Economy WHERE Owner = ? AND AccountId = ?")) {

            statement.setString(1, owner.toString());
            statement.setString(2, accountId.toString());

            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next())
                    return new BigDecimal(ServerSystem.Instance.getRegistry().getService(EconomyProvider.class).getDefaultBalance());

                return resultSet.getBigDecimal("Balance");
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error looking up balance for '${owner}' ('${accountId}')'", exception);
        }
    }

    @Override
    public void setBalance(BigDecimal balance) {
        balance = balance.max(BigDecimal.ZERO);

        var query = isInDatabase()?
                "UPDATE Economy SET Balance = ? WHERE Owner = ? AND AccountId = ?" :
                "INSERT INTO Economy (Balance, Owner, AccountId) VALUES (?, ?, ?)";

        try (var connection = databaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(query)) {
                statement.setBigDecimal(1, balance);
                statement.setString(2, owner.toString());
                statement.setString(3, accountId.toString());

                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error setting balance for '${owner}' ('${accountId}')'", exception);
        }
    }

    @Override
    public void delete() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("DELETE FROM Economy WHERE Owner = ? AND AccountId = ?")) {

            statement.setString(1, owner.toString());
            statement.setString(2, accountId.toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Error resetting balance for '${owner}' ('${accountId}')'", exception);
        }
    }

    @Override
    public void save() {
        // No-op for SQL implementations as changes are saved immediately
    }

    private boolean isInDatabase() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT 1 FROM Economy WHERE Owner = ? AND AccountId = ?")) {

            statement.setString(1, owner.toString());
            statement.setString(2, accountId.toString());

            try (var resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error checking if account '${owner}' ('${accountId}') is in database!", exception);
        }
    }

    @Override
    public Map<UUID, BigDecimal> getTopTen() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT Owner, Balance FROM Economy ORDER BY Balance DESC LIMIT 10")) {

            try (var resultSet = statement.executeQuery()) {
                var topTen = new LinkedHashMap<UUID, BigDecimal>();

                while (resultSet.next()) {
                    var ownerUuid = UUID.fromString(resultSet.getString("Owner"));
                    var balance = resultSet.getBigDecimal("Balance");
                    topTen.put(ownerUuid, balance);
                }

                return topTen;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error retrieving top ten balances", exception);
        }
    }
}
