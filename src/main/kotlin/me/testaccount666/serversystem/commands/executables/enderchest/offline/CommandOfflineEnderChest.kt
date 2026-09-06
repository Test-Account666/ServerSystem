package me.testaccount666.serversystem.commands.executables.enderchest.offline

import de.tr7zw.nbtapi.NBT
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.Bukkit
import org.bukkit.command.Command

class CommandOfflineEnderChest : AbstractServerSystemCommand {
    val enderChestLoader: EnderChestLoader?
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "OfflineEnderChest.Use"
    override fun getSyntaxPath(command: Command?) = "OfflineEnderChest"

    constructor() {
        if (!Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            log.warning("NBTAPI is not installed, Offline-EnderChest will not work!")
            enderChestLoader = null
            return
        }

        if (!NBT.preloadApi()) {
            log.severe("Failed to load NBT-API!")
            enderChestLoader = null
            return
        }

        enderChestLoader = EnderChestLoader()
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        executeEnderChestCommand(commandSender, *arguments)
    }

    fun executeEnderChestCommand(commandSender: User, vararg arguments: String) {
        val cachedUser = getService<UserManager>().getUserOrNull(arguments[0]) ?: run {
            commandSender.generalMsg("Offline.NeverPlayed") { target(arguments[0]) }
            return
        }

        if (cachedUser.isOnlineUser) {
            commandSender.generalMsg("Offline.NotOffline") { target(arguments[0]) }
            return
        }

        val targetUser = cachedUser.offlineUser
        val targetPlayer = targetUser.player ?: return

        if (!targetPlayer.hasPlayedBefore()) {
            commandSender.generalMsg("Offline.NeverPlayed") { target(arguments[0]) }
            return
        }

        val inventory = enderChestLoader?.loadOfflineInventory(targetPlayer) ?: run {
            log.warning("(OfflineEnderChest) Failed to load inventory of '${arguments[0]}'!")
            commandSender.generalMsg("ErrorOccurred") { target(arguments[0]) }
            return
        }

        commandSender.getPlayer()!!.openInventory(inventory)
    }
}
