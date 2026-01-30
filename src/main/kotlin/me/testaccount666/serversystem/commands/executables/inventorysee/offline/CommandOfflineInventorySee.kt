package me.testaccount666.serversystem.commands.executables.inventorysee.offline

import de.tr7zw.nbtapi.NBT
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command

class CommandOfflineInventorySee : AbstractServerSystemCommand {
    val inventoryLoader: InventoryLoader?
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "OfflineInventorySee.Use"

    override fun getSyntaxPath(command: Command?) = "OfflineInventorySee"

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
        if (!isPlayer(commandSender)) return

        val cachedUser = getService<UserManager>().getUserOrNull(arguments[0]) ?: run {
            general("Offline.NeverPlayed", commandSender) { target(arguments[0]) }.build()
            return
        }

        if (cachedUser.isOnlineUser) {
            general("Offline.NotOffline", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = cachedUser.offlineUser.player ?: return
        if (!targetPlayer.hasPlayedBefore()) {
            general("Offline.NeverPlayed", commandSender) { target(arguments[0]) }.build()
            return
        }

        val inventory = inventoryLoader!!.loadOfflineInventory(targetPlayer) ?: run {
            log.warning("(OfflineInventorySee) Failed to load inventory of '${arguments[0]}'!")
            general("ErrorOccurred", commandSender) {
                target(arguments[0])
                label(label)
            }.build()
            return
        }

        commandSender.getPlayer()!!.openInventory(inventory)
    }
}
