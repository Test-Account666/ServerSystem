package me.testaccount666.serversystem.managers.database.moderation

import me.testaccount666.serversystem.managers.database.HikariConfigUtil
import me.testaccount666.serversystem.moderation.AbstractModerationManager
import me.testaccount666.serversystem.moderation.BanModeration
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.moderation.ban.SqliteBanManager
import me.testaccount666.serversystem.moderation.mute.SqliteMuteManager
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * Manages SQLite database connections for moderation-related functionality.
 * Provides a connection pool using HikariCP.
 */
class SqliteModerationDatabaseManager(dataFolder: File) : AbstractSqlModerationDatabaseManager(null) {
    private val _databaseFile: File = Path.of(dataFolder.path, "data", "moderation.db").toFile()

    override val autoIncrementSql = "INTEGER PRIMARY KEY AUTOINCREMENT"

    /**
     * Initializes the database connection pool.
     */
    override fun initializeConnection() {
        val isClosed = dataSource?.isClosed ?: true
        if (!isClosed) return

        if (!_databaseFile.parentFile.exists()) _databaseFile.parentFile.mkdirs()

        val config = HikariConfigUtil.configureSqlite(_databaseFile, "moderation-sqlite-pool")
        initializeConnection(config)
    }

    override fun instantiateBanManager(uuid: UUID): AbstractModerationManager<BanModeration> = SqliteBanManager(uuid)

    override fun instantiateMuteManager(uuid: UUID): AbstractModerationManager<MuteModeration> = SqliteMuteManager(uuid)
}
