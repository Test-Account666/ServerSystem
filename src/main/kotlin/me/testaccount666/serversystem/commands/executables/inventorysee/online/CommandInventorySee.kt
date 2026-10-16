package me.testaccount666.serversystem.commands.executables.inventorysee.online

import me.testaccount666.paperktx.extensions.*
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.inventorysee.offline.CommandOfflineInventorySee
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

@ServerSystemCommand("inventorysee", ["offlineinventorysee"], TabCompleterInventorySee::class)
class CommandInventorySee : AbstractServerSystemCommand() {
    val inventoryCache = HashMap<Player, Inventory>()
    val offlineInventorySee = CommandOfflineInventorySee(this)
    override fun minRequiredArguments(command: Command): Int {
        if (isOffline(command)) return offlineInventorySee.minRequiredArguments(command)
        return 1
    }

    override fun getUsagePermission(command: Command): String {
        if (isOffline(command)) return offlineInventorySee.getUsagePermission(command)
        return "InventorySee.Use"
    }

    override fun getSyntaxPath(command: Command?): String {
        command ?: return "InventorySee"
        if (isOffline(command)) return offlineInventorySee.getSyntaxPath(command)
        return "InventorySee"
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.startsWith("offline", true)) {
            offlineInventorySee.execute(commandSender, command, label, *arguments)
            return
        }

        if (!command.name.equals("inventorysee", true)) return
        processInventorySee(commandSender, label, *arguments)
    }

    fun processInventorySee(sender: User, label: String, vararg arguments: String) {
        if (!isPlayer(sender)) return

        val targetUser = getTargetUser(sender, arguments = arrayOf(arguments[0])) ?: run {
            sender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        if (targetUser === sender) {
            sender.commandMsg("InventorySee.CannotSeeSelf")
            return
        }

        val ownerPlayer = targetUser.getPlayer()!!
        val viewerPlayer = sender.getPlayer()!!

        inventoryCache.computeIfAbsent(ownerPlayer, ::createAndInitializeInventory).also(viewerPlayer::openInventory)
    }

    private fun createAndInitializeInventory(ownerPlayer: Player): Inventory {
        return Bukkit.createInventory(ownerPlayer, 54, "${ownerPlayer.name}'s Inventory".asComponent()).apply {
            refreshInventoryContents(ownerPlayer, this)
        }
    }

    fun refreshInventoryContents(owner: Player, displayInventory: Inventory) {
        copyPlayerInventory(owner, displayInventory)
        addSectionDecorators(displayInventory)
    }

    private fun copyPlayerInventory(owner: Player, displayInventory: Inventory) {
        // main inventory and hotbar (slots 0-39)
        for (slot in 0..39) displayInventory[slot] = owner.inventory[slot]

        displayInventory[40] = owner.inventory.itemInOffHand
        displayInventory[41] = owner.itemOnCursor
    }

    fun addSectionDecorators(displayInventory: Inventory) {
        InventorySeeUtils.addSectionDecorators(displayInventory, false)
    }

    fun placeFilledMarkers(inventory: Inventory, material: Material, displayName: String, startSlot: Int, endSlot: Int) {
        InventorySeeUtils.placeFilledMarkers(inventory, material, displayName, startSlot, endSlot)
    }

    fun applyChangesToOwner(owner: Player, displayInventory: Inventory) {
        for (slot in 0..39) owner.inventory[slot] = displayInventory[slot]

        owner.inventory.setItemInOffHand(displayInventory[40])
        owner.setItemOnCursor(displayInventory[41])
        owner.updateInventory()
        dropModifiedItems(owner, displayInventory, 42, 45)
    }

    private fun dropModifiedItems(owner: Player, displayInventory: Inventory, fromSlot: Int, toSlot: Int) {
        displayInventory[fromSlot..<toSlot].filterNot { it.isAir() }.forEach {
            owner.world.dropItem(owner.eyeLocation.add(0.0, -0.33, 0.0), it.clone()).velocity = owner.location.direction.multiply(0.35)
            it.amount = 0
        }
    }

    private fun isOffline(command: Command) = command.name.startsWith("offline", true)
}
