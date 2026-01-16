package me.testaccount666.serversystem.commands.executables.repair

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.repair.CommandRepair.RepairType.*
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.Damageable

@ServerSystemCommand("repair", [], TabCompleterRepair::class)
class CommandRepair : AbstractServerSystemCommand() {

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Repair.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val typeArg = arguments.getOrNull(0) ?: "hand"

        val repairType = RepairType.fromString(typeArg)
        if (repairType == null) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val player = commandSender.getPlayer()!!
        val items = extractItems(player.inventory, repairType)

        val repairedCount = items.count { repairItem(it) }
        if (repairedCount <= 0) {
            command("Repair.NotRepairable", commandSender).build()
            return
        }

        command("Repair.Success", commandSender) {
            postModifier { it.replace("<COUNT>", repairedCount.toString()) }
        }.build()
    }

    enum class RepairType {
        HAND, OFFHAND, ARMOR, INVENTORY, ALL;

        companion object {
            fun fromString(value: String) = when {
                value.equals("hand", true) -> HAND
                value.equals("offhand", true) -> OFFHAND
                value.equals("armor", true) -> ARMOR
                value.equals("inventory", true) -> INVENTORY
                value.equals("all", true) || value == "*" -> ALL
                else -> null
            }
        }
    }

    private fun extractItems(inventory: PlayerInventory, type: RepairType): List<ItemStack> {
        return when (type) {
            HAND -> listOf(inventory.itemInMainHand)
            OFFHAND -> listOf(inventory.itemInOffHand)
            ARMOR -> inventory.armorContents.filterNotNull()
            INVENTORY -> inventory.contents.filterNotNull()
            ALL -> buildList {
                addAll(inventory.contents.filterNotNull())
                addAll(inventory.armorContents.filterNotNull())
                addAll(inventory.extraContents.filterNotNull())
                addAll(inventory.storageContents.filterNotNull())
            }
        }.filterNot { it.isAir() }
    }

    private fun repairItem(item: ItemStack): Boolean {
        val meta = item.itemMeta as? Damageable ?: return false
        if (!meta.hasDamage()) return false

        meta.damage = 0
        item.setItemMeta(meta)
        return true
    }

    override fun getSyntaxPath(command: Command?) = "Repair"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Repair.Use", false)
    }
}
