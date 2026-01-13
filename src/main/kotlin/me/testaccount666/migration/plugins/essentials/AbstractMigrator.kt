package me.testaccount666.migration.plugins.essentials

import com.earth2me.essentials.Essentials
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import java.nio.file.Path
import java.util.*

abstract class AbstractMigrator {
    val userManager by lazy { instance.registry.getService<UserManager>() }
    val essentials by lazy { Essentials.getPlugin(Essentials::class.java) }

    protected fun offlinePlayers() = Bukkit.getOfflinePlayers()

    protected fun ensureUserDataExists(uuid: UUID) {
        val userDataDirectory = Path.of("plugins", "Essentials", "userdata")
        if (!userDataDirectory.toFile().exists()) userDataDirectory.toFile().mkdirs()

        val userFile = userDataDirectory.resolve("${uuid}.yml").toFile()
        if (!userFile.exists()) userFile.createNewFile()
    }

    abstract fun migrateFrom(): Int

    abstract fun migrateTo(): Int
}
