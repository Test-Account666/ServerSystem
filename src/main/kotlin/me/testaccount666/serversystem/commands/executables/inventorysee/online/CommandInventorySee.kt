package me.testaccount666.serversystem.commands.executables.inventorysee.online

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.inventorysee.offline.CommandOfflineInventorySee
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

@ServerSystemCommand("inventorysee", ["offlineinventorysee"], TabCompleterInventorySee::class)
class CommandInventorySee : AbstractServerSystemCommand() {
    val inventoryCache: MutableMap<Player, Inventory> = HashMap()
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
            general("PlayerNotFound", sender) { target(arguments[0]) }.build()
            return
        }

        if (targetUser === sender) {
            command("InventorySee.CannotSeeSelf", sender).build()
            return
        }

        val ownerPlayer = targetUser.getPlayer()!!
        val viewerPlayer = sender.getPlayer()!!

        val customInventory = inventoryCache.computeIfAbsent(ownerPlayer) { createAndInitializeInventory(it) }
        viewerPlayer.openInventory(customInventory)
    }

    private fun createAndInitializeInventory(ownerPlayer: Player): Inventory {
        val newInventory = Bukkit.createInventory(ownerPlayer, 54, "${ownerPlayer.name}'s Inventory")
        refreshInventoryContents(ownerPlayer, newInventory)
        return newInventory
    }

    fun refreshInventoryContents(owner: Player, displayInventory: Inventory) {
        copyPlayerInventory(owner, displayInventory)
        addSectionDecorators(displayInventory)
    }

    private fun copyPlayerInventory(owner: Player, displayInventory: Inventory) {
        // main inventory and hotbar (slots 0-39)
        for (slot in 0..39) displayInventory.setItem(slot, owner.inventory.getItem(slot))

        displayInventory.setItem(40, owner.inventory.itemInOffHand)
        displayInventory.setItem(41, owner.itemOnCursor)
    }

    fun addSectionDecorators(displayInventory: Inventory) {
        InventorySeeUtils.addSectionDecorators(displayInventory, false)
    }

    fun placeFilledMarkers(inventory: Inventory, material: Material, displayName: String, startSlot: Int, endSlot: Int) {
        InventorySeeUtils.placeFilledMarkers(inventory, material, displayName, startSlot, endSlot)
    }

    fun applyChangesToOwner(owner: Player, displayInventory: Inventory) {
        for (slot in 0..39) owner.inventory.setItem(slot, displayInventory.getItem(slot))

        owner.inventory.setItemInOffHand(displayInventory.getItem(40))
        owner.setItemOnCursor(displayInventory.getItem(41))
        owner.updateInventory()
        dropModifiedItems(owner, displayInventory, 42, 45)
    }

    private fun dropModifiedItems(owner: Player, displayInventory: Inventory, fromSlot: Int, toSlot: Int) {
        for (slot in fromSlot..<toSlot) {
            val item = displayInventory.getItem(slot) ?: continue

            owner.world.dropItem(owner.eyeLocation.add(0.0, -0.33, 0.0), item.clone())
                .velocity = owner.location.direction.multiply(0.35)
            item.amount = 0
        }
    }

    private fun isOffline(command: Command) = command.name.startsWith("offline", true)
}
