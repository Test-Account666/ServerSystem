package me.testaccount666.serversystem.commands.executables.inventorysee.offline

import de.tr7zw.nbtapi.NBT
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player

class CommandOfflineInventorySee : AbstractServerSystemCommand {
    constructor(commandInventorySee: CommandInventorySee) : super() {
        if (!Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            log.warning("NBTAPI is not installed, Offline-InventorySee will not work!")
            inventoryLoader = null
            return
        }
        if (!NBT.preloadApi()) {
            log.severe("Failed to load NBT-API!")
            inventoryLoader = null
            return
        }
        inventoryLoader = InventoryLoader()
    }

    val inventoryLoader: InventoryLoader?

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (inventoryLoader == null) {
            general("CommandDisabled", commandSender) {
                label(label)
                postModifier { it.replace("<REASON>", "NBTAPI is not installed, Offline-InventorySee will not work!") }
            }.build()
            return
        }

        if (!command.name.equals("offlineinventorysee", true)) return

        processOfflineInventorySee(commandSender, label, *arguments)
    }

    fun processOfflineInventorySee(commandSender: User, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "OfflineInventorySee.Use")) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(null))
                label(label)
            }.build()
            return
        }

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

        val inventory = inventoryLoader!!.loadOfflineInventory(targetPlayer)
        if (inventory == null) {
            log.warning("(OfflineInventorySee) Failed to load inventory of '${arguments[0]}'!")
            general("ErrorOccurred", commandSender) {
                target(arguments[0])
                label(label)
            }.build()
            return
        }

        commandSender.getPlayer()!!.openInventory(inventory)
    }

    override fun getSyntaxPath(command: Command?): String = "OfflineInventorySee"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "OfflineInventorySee.Use", false)
    }
}
