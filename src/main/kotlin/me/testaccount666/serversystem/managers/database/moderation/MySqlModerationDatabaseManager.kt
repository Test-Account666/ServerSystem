package me.testaccount666.serversystem.managers.database.moderation

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.database.HikariConfigUtil
import me.testaccount666.serversystem.moderation.AbstractModerationManager
import me.testaccount666.serversystem.moderation.BanModeration
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.moderation.ban.MySqlBanManager
import me.testaccount666.serversystem.moderation.mute.MySqlMuteManager
import java.util.*

/**
 * Manages MySQL database connections for moderation-related functionality.
 * Provides a connection pool using HikariCP.
 */
class MySqlModerationDatabaseManager
/**
 * Creates a new MySqlModerationDatabaseManager with the specified configuration.
 * 
 * @param configReader The configuration reader to get database settings from
 */
    (configReader: ConfigReader?) : AbstractSqlModerationDatabaseManager(configReader) {

    override val autoIncrementSql = "INT AUTO_INCREMENT PRIMARY KEY"
    override val supportsInlineIndex = true

    /**
     * Initializes the database connection pool.
     */
    override fun initializeConnection() {
        val isClosed = dataSource?.isClosed ?: true
        if (!isClosed) return

        val reader = configReader
        if (reader == null) {
            log.warning("Failed to initialize MySQL connection pool for $databaseType: No configuration found.")
            return
        }

        val config = HikariConfigUtil.configureMySql(reader, "Moderation.MySQL", "moderation-hikari-pool")
        initializeConnection(config)
    }

    override fun instantiateBanManager(uuid: UUID): AbstractModerationManager<BanModeration> = MySqlBanManager(uuid)

    override fun instantiateMuteManager(uuid: UUID): AbstractModerationManager<MuteModeration> = MySqlMuteManager(uuid)
}
