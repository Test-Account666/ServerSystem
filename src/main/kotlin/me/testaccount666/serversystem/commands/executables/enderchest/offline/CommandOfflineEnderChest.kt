package me.testaccount666.serversystem.commands.executables.enderchest.offline

import de.tr7zw.nbtapi.NBT
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player

class CommandOfflineEnderChest : AbstractServerSystemCommand {
    val enderChestLoader: EnderChestLoader?

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
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        executeEnderChestCommand(commandSender, *arguments)
    }

    fun executeEnderChestCommand(commandSender: User, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "OfflineEnderChest.Use")) return

        val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(arguments[0])
        if (cachedUser == null) {
            general("Offline.NeverPlayed", commandSender) { target(arguments[0]) }.build()
            return
        }

        if (cachedUser.isOnlineUser) {
            general("Offline.NotOffline", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetUser = cachedUser.offlineUser
        val targetPlayer = targetUser.player ?: return

        if (!targetPlayer.hasPlayedBefore()) {
            general("Offline.NeverPlayed", commandSender) { target(arguments[0]) }.build()
            return
        }

        val inventory = enderChestLoader?.loadOfflineInventory(targetPlayer)
        if (inventory == null) {
            log.warning("(OfflineEnderChest) Failed to load inventory of '${arguments[0]}'!")
            general("ErrorOccurred", commandSender) { target(arguments[0]) }.build()
            return
        }

        commandSender.getPlayer()!!.openInventory(inventory)
    }

    override fun getSyntaxPath(command: Command?) = "OfflineEnderChest"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "OfflineEnderChest.Use", false)
    }
}
