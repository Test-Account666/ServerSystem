package me.testaccount666.serversystem.commands.executables.repair

import io.papermc.paper.datacomponent.DataComponentTypes
import me.testaccount666.paperktx.extensions.isAir
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.SimpleCompletion
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.executables.repair.CommandRepair.RepairType.*
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

@ServerSystemCommand(
    "repair", simpleCompletions = [
        SimpleCompletion(0, ["all", "*", "armor", "hand", "offhand", "inv", "inventory"])
    ]
)
class CommandRepair : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Repair.Use"
    override fun getSyntaxPath(command: Command?) = "Repair"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val typeArg = arguments.getOrNull(0) ?: "hand"

        val repairType = RepairType.fromString(typeArg) ?: run {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        val player = commandSender.getPlayer()!!
        val items = extractItems(player.inventory, repairType)

        val repairedCount = items.count { repairItem(it) }
        if (repairedCount <= 0) {
            commandSender.commandMsg("Repair.NotRepairable")
            return
        }

        commandSender.commandMsg("Repair.Success") {
            postModifier { it.replace("<COUNT>", repairedCount.toString()) }
        }
    }

    enum class RepairType(vararg val names: String) {
        HAND("hand"),
        OFFHAND("offhand"),
        ARMOR("armor"),
        INVENTORY("inv", "inventory"),
        ALL("all", "*");

        companion object {
            fun fromString(value: String): RepairType? {
                val value = value.lowercase()
                return entries.find { value in it.names }
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
        item.getData(DataComponentTypes.DAMAGE)?.takeIf { it > 0 } ?: return false
        item.setData(DataComponentTypes.DAMAGE, 0)
        return true;
    }
}
