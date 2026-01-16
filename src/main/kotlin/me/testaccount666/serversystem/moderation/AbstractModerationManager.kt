package me.testaccount666.serversystem.moderation

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.managers.database.moderation.ModerationDatabaseManager
import java.util.*

abstract class AbstractModerationManager<T : AbstractModeration>(val ownerUuid: UUID) {
    protected val databaseManager = instance.registry.getService<ModerationDatabaseManager>()

    abstract fun addModeration(moderation: T)

    abstract fun removeModeration(moderation: T)

    abstract val moderations: List<T>

    open val activeModeration
        get() = moderations.firstOrNull { !it.isExpired }

    open val activeModerations
        get() = moderations.filter { !it.isExpired }

    fun hasActiveModeration() = moderations.any { !it.isExpired }
}
