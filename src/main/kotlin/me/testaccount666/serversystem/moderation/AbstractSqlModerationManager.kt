package me.testaccount666.serversystem.moderation

import me.testaccount666.serversystem.managers.database.AbstractSqlDatabaseManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

abstract class AbstractSqlModerationManager<T : AbstractModeration>(ownerUuid: UUID) : AbstractModerationManager<T>(ownerUuid) {

    private val sqlDatabaseManager: AbstractSqlDatabaseManager
        get() = databaseManager as AbstractSqlDatabaseManager

    protected abstract val moderationTypes: List<String>

    protected abstract fun mapResultSet(resultSet: ResultSet): T

    protected open fun getModerationType(moderation: T) = moderationTypes.first()

    override fun addModeration(moderation: T) {
        try {
            sqlDatabaseManager.connection.use { connection ->
                connection.prepareStatement("INSERT INTO Moderation (TargetUUID, SenderUUID, IssueTime, ExpireTime, Reason, Type) VALUES (?, ?, ?, ?, ?, ?)")
                    .use { statement ->
                        statement.setString(1, moderation.targetUuid.toString())
                        statement.setString(2, moderation.senderUuid.toString())
                        statement.setLong(3, moderation.issueTime)
                        statement.setLong(4, moderation.expireTime)
                        statement.setString(5, moderation.reason)
                        statement.setString(6, getModerationType(moderation))
                        statement.executeUpdate()
                    }
            }
        } catch (exception: SQLException) {
            throw RuntimeException("Error adding moderation for target '${moderation.targetUuid}'", exception)
        }
    }

    override fun removeModeration(moderation: T) {
        val typePlaceholders = moderationTypes.joinToString(" OR ") { "Type = ?" }
        try {
            sqlDatabaseManager.connection.use { connection ->
                connection.prepareStatement("DELETE FROM Moderation WHERE TargetUUID = ? AND SenderUUID = ? AND IssueTime = ? AND ($typePlaceholders)")
                    .use { statement ->
                        statement.setString(1, moderation.targetUuid.toString())
                        statement.setString(2, moderation.senderUuid.toString())
                        statement.setLong(3, moderation.issueTime)
                        for (i in moderationTypes.indices) {
                            statement.setString(4 + i, moderationTypes[i])
                        }
                        statement.executeUpdate()
                    }
            }
        } catch (exception: SQLException) {
            throw RuntimeException("Error removing moderation for target '${moderation.targetUuid}'", exception)
        }
    }

    override val moderations: List<T>
        get() {
            val typePlaceholders = moderationTypes.joinToString(" OR ") { "Type = ?" }
            try {
                sqlDatabaseManager.connection.use { connection ->
                    connection.prepareStatement("SELECT * FROM Moderation WHERE TargetUUID = ? AND ($typePlaceholders)")
                        .use { statement ->
                            statement.setString(1, ownerUuid.toString())
                            for (i in moderationTypes.indices) {
                                statement.setString(2 + i, moderationTypes[i])
                            }
                            statement.executeQuery().use { resultSet ->
                                val moderations = ArrayList<T>()
                                while (resultSet.next()) {
                                    moderations.add(mapResultSet(resultSet))
                                }
                                return moderations
                            }
                        }
                }
            } catch (exception: SQLException) {
                throw RuntimeException("Error getting moderations for '${ownerUuid}'", exception)
            }
        }
}
