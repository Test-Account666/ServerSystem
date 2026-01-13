package me.testaccount666.serversystem.commands.executables.repair

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.*

@ServerSystemCommand("repair", [], TabCompleterRepair::class)
class CommandRepair : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Repair.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val repairType = if (arguments.isEmpty()) "hand" else arguments[0].lowercase(Locale.getDefault())
        val player = commandSender.getPlayer()!!

        when (repairType) {
            "all", "*" -> {
                var repaired = 0
                repaired += repairInventory(player.inventory.contents)
                repaired += repairInventory(player.inventory.armorContents)
                repaired += repairInventory(player.inventory.extraContents)

                sendSuccessMessage(commandSender, repaired)
            }

            "hand" -> {
                val item = player.inventory.itemInMainHand
                if (repairItem(item)) sendSuccessMessage(commandSender, 1)
                else command("Repair.NotRepairable", commandSender).build()
            }

            "offhand" -> {
                val item = player.inventory.itemInOffHand
                if (repairItem(item)) sendSuccessMessage(commandSender, 1)
                else command("Repair.NotRepairable", commandSender).build()
            }

            "armor" -> {
                val repaired = repairInventory(player.inventory.armorContents)
                sendSuccessMessage(commandSender, repaired)
            }

            "inventory" -> {
                val repaired = repairInventory(player.inventory.contents)
                sendSuccessMessage(commandSender, repaired)
            }

            else -> general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
        }
    }

    private fun sendSuccessMessage(commandSender: User, count: Int) {
        command("Repair.Success", commandSender) {
            postModifier { it.replace("<COUNT>", count.toString()) }
        }.build()
    }

    private fun repairItem(item: ItemStack?): Boolean {
        if (item?.type?.isAir ?: true) return false

        val meta = item.itemMeta
        if (meta !is Damageable) return false
        if (!meta.hasDamage()) return false

        meta.damage = 0
        item.setItemMeta(meta)
        return true
    }

    private fun repairInventory(items: Array<ItemStack?>): Int = items.count { repairItem(it) }

    override fun getSyntaxPath(command: Command?): String = "Repair"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Repair.Use", false)
    }
}