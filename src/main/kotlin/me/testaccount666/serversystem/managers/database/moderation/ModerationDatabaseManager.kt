package me.testaccount666.serversystem.managers.database.moderation

import me.testaccount666.serversystem.managers.database.DatabaseManager
import me.testaccount666.serversystem.moderation.AbstractModerationManager
import me.testaccount666.serversystem.moderation.BanModeration
import me.testaccount666.serversystem.moderation.MuteModeration
import java.util.*

/**
 * Interface for moderation database managers.
 */
interface ModerationDatabaseManager : DatabaseManager {
    /**
     * Instantiates a ban manager for the specified user.
     * 
     * @param uuid The UUID of the user
     * @return The ban manager
     */
    fun instantiateBanManager(uuid: UUID): AbstractModerationManager<BanModeration>

    /**
     * Instantiates a mute manager for the specified user.
     * 
     * @param uuid The UUID of the user
     * @return The mute manager
     */
    fun instantiateMuteManager(uuid: UUID): AbstractModerationManager<MuteModeration>
}
