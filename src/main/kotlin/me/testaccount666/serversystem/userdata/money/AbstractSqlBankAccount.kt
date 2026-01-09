package me.testaccount666.serversystem.userdata.money

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

private const val SELECT_BALANCE = "SELECT Balance FROM Economy WHERE Owner = ? AND AccountId = ?"
private const val INSERT_BALANCE = "INSERT INTO Economy (Balance, Owner, AccountId) VALUES (?, ?, ?)"
private const val UPDATE_BALANCE = "UPDATE Economy SET Balance = ? WHERE Owner = ? AND AccountId = ?"
private const val DELETE_ACCOUNT = "DELETE FROM Economy WHERE Owner = ? AND AccountId = ?"
private const val CHECK_EXISTS = "SELECT 1 FROM Economy WHERE Owner = ? AND AccountId = ?"
private const val SELECT_TOP_TEN = "SELECT Owner, Balance FROM Economy ORDER BY Balance DESC LIMIT 10"

abstract class AbstractSqlBankAccount(
    owner: UUID,
    accountId: BigInteger
) : AbstractBankAccount(owner, accountId) {

    protected val databaseManager: AbstractEconomyDatabaseManager =
        ServerSystem.instance.registry.getService()

    override var balance: BigDecimal
        get() = fetchBalance()
        set(newBalance) = upsertBalance(newBalance.min(BigDecimal.ZERO))

    override fun delete() = executeUpdate(DELETE_ACCOUNT)

    override fun save() {
        // Intentional no-op for SQL
    }

    override val topTen: MutableMap<UUID, BigDecimal>
        get() = fetchTopTen()

    private fun fetchBalance(): BigDecimal =
        tryQuery(SELECT_BALANCE) { rs ->
            if (rs.next()) rs.getBigDecimal("Balance")
            else BigDecimal(
                ServerSystem.instance.registry
                    .getService<EconomyProvider>()
                    .defaultBalance
            )
        } ?: BigDecimal.ZERO // should never happen


    private fun upsertBalance(value: BigDecimal) {
        val query = if (existsInDatabase()) UPDATE_BALANCE else INSERT_BALANCE
        executeUpdate(query, value)
    }

    private fun existsInDatabase(): Boolean =
        tryQuery(CHECK_EXISTS) { it.next() } ?: false

    private fun fetchTopTen(): MutableMap<UUID, BigDecimal> =
        tryQuery(SELECT_TOP_TEN) { rs ->
            linkedMapOf<UUID, BigDecimal>().apply {
                while (rs.next()) {
                    val ownerUuid = UUID.fromString(rs.getString("Owner"))
                    put(ownerUuid, rs.getBigDecimal("Balance"))
                }
            }
        } ?: linkedMapOf()

    private fun <T> tryQuery(
        sql: String,
        handler: (resultSet: ResultSet) -> T
    ): T? {
        try {
            databaseManager.getConnection().use { connection ->
                connection.prepareStatement(sql).use { stmt ->
                    bindParams(stmt, sql)
                    stmt.executeQuery().use { rs ->
                        return handler(rs)
                    }
                }
            }
        } catch (ex: SQLException) {
            throw RuntimeException("SQL Query error: $sql", ex)
        }
    }

    private fun executeUpdate(sql: String, vararg data: Any) {
        try {
            databaseManager.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    bindParams(stmt, sql, *data)
                    stmt.executeUpdate()
                }
            }
        } catch (ex: SQLException) {
            throw RuntimeException("SQL Update error: $sql", ex)
        }
    }

    private fun bindParams(
        stmt: PreparedStatement,
        sql: String,
        vararg manual: Any
    ) {
        val params = when (sql) {
            SELECT_BALANCE,
            CHECK_EXISTS,
            DELETE_ACCOUNT -> arrayOf(owner.toString(), accountId.toString())

            UPDATE_BALANCE -> arrayOf(manual[0], owner.toString(), accountId.toString())
            INSERT_BALANCE -> arrayOf(manual[0], owner.toString(), accountId.toString())
            else -> manual
        }

        params.forEachIndexed { index, value -> stmt.setObject(index + 1, value) }
    }
}
