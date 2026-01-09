package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.userdata.money.ConsoleBankAccount
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Represents the Console CommandSender as a User.
 *
 *
 * Has benefits like infinite money
 */
open class ConsoleUser internal constructor() : User(UserManager.USER_DATA_PATH.resolve("${CONSOLE_UUID}.yml.gz").toFile()) {
    override val commandSender: CommandSender = Bukkit.getConsoleSender()
    override var player: OfflinePlayer? = null
    override var onlinePlayer: Player? = null

    override fun loadBasicData() {
        playerLanguage = MessageManager.defaultLanguage!!

        val consoleName = MappingsData.console(this).getName("name")
        name = consoleName.orElse("Server")
        uuid = CONSOLE_UUID
        bankAccount = ConsoleBankAccount()
        isAcceptsMessages = true
    }

    override fun getName(): Optional<String> = Optional.ofNullable(name)

    companion object {
        // 00000000-0000-0000-0000-000000000000 is never a player, so let's just use that for the console
        @JvmField
        val CONSOLE_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
}