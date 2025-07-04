package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.ServerSystem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public abstract class AbstractSqlBankAccount extends AbstractBankAccount {
    protected Connection connection;

    public AbstractSqlBankAccount(UUID owner, BigInteger accountId, Connection connection) {
        super(owner, accountId);
        this.connection = connection;
    }


    @Override
    public BigDecimal getBalance() {
        try {
            var query = "SELECT Balance FROM Economy WHERE Owner = ? AND AccountId = ?";
            var statement = connection.prepareStatement(query);
            statement.setString(1, owner.toString());
            statement.setString(2, accountId.toString());
            var resultSet = statement.executeQuery();

            var hasNext = resultSet.next();

            if (!hasNext) {
                resultSet.close();
                statement.close();
                return new BigDecimal(ServerSystem.Instance.getEconomyManager().getDefaultBalance());
            }

            var balance = resultSet.getBigDecimal("Balance");

            resultSet.close();
            statement.close();

            return balance;
        } catch (SQLException exception) {
            throw new RuntimeException("Error looking up balance for '${owner}' ('${accountId}')'", exception);
        }
    }

    @Override
    public void setBalance(BigDecimal balance) {
        balance = balance.max(BigDecimal.ZERO);

        try {
            var query = isInDatabase()? "UPDATE Economy SET Balance = ? WHERE Owner = ? AND AccountId = ?"
                    : "INSERT INTO Economy (Balance, Owner, AccountId) VALUES (?, ?, ?)";
            var statement = connection.prepareStatement(query);
            statement.setBigDecimal(1, balance);
            statement.setString(2, owner.toString());
            statement.setString(3, accountId.toString());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException exception) {
            throw new RuntimeException("Error setting balance for '${owner}' ('${accountId}')'", exception);
        }
    }

    @Override
    public void delete() {
        try {
            var query = "DELETE FROM Economy WHERE Owner = ? AND AccountId = ?";
            var statement = connection.prepareStatement(query);
            statement.setString(1, owner.toString());
            statement.setString(2, accountId.toString());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException exception) {
            throw new RuntimeException("Error resetting balance for '${owner}' ('${accountId}')'", exception);
        }
    }

    @Override
    public void save() {

    }

    private boolean isInDatabase() {
        try {
            var query = "SELECT * FROM Economy WHERE Owner = ? AND AccountId = ?";
            var statement = connection.prepareStatement(query);
            statement.setString(1, owner.toString());
            statement.setString(2, accountId.toString());

            var resultSet = statement.executeQuery();

            var hasNext = resultSet.next();

            resultSet.close();
            statement.close();

            return hasNext;
        } catch (SQLException exception) {
            throw new RuntimeException("Error checking if account '${owner}' ('${accountId}') is in database!", exception);
        }
    }
}
