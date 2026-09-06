package me.testaccount666.serversystem.commands.executables.sign

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.paperktx.extensions.*
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.extensions.ComponentExtensions.asString
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@ServerSystemCommand("sign", ["unsign"])
open class CommandSign : AbstractServerSystemCommand() {
    protected val signKey = NamespacedKey(instance, "sign")
    override fun minRequiredArguments(command: Command) = if (command.name.equals("sign", true)) 1 else 0
    override fun getUsagePermission(command: Command) = if (command.name.equals("sign", true)) "Sign.Use" else "Unsign.Use"
    override fun getSyntaxPath(command: Command?): String {
        val name = command?.name ?: "sign"
        return if (name.equals("sign", true)) "Sign" else "Unsign"
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val player = commandSender.getPlayer()!!
        val itemInHand = player.inventory[EquipmentSlot.HAND]
        if (itemInHand.isAir()) {
            commandSender.commandMsg("Sign.NoItemInHand")
            return
        }
        val meta = itemInHand.itemMeta ?: run {
            commandSender.commandMsg("Sign.NoItemMeta")
            return
        }

        if (command.name.equals("sign", true)) {
            executeSign(itemInHand, meta, commandSender, command, label, *arguments)
        } else {
            executeUnsign(itemInHand, meta, commandSender)
        }
    }

    private fun executeUnsign(itemInHand: ItemStack, itemMeta: ItemMeta, commandSender: User) {
        val dataContainer = itemMeta.persistentDataContainer
        if (!dataContainer.has(signKey)) {
            commandSender.commandMsg("Unsign.NotSigned")
            return
        }

        val lore = itemMeta.lore() ?: mutableListOf<Component>()

        lore.removeIf { loreComponent ->
            val strippedLine = loreComponent.asString(true)
            val strippedLore = stripColor(dataContainer.get<String>(signKey))
            strippedLore.split("\n")
                .filter(String::isNotEmpty)
                .any { it.equals(strippedLine, true) }
        }
        itemMeta.lore(lore)

        dataContainer.remove(signKey)

        itemInHand.itemMeta = itemMeta
        commandSender.commandMsg("Unsign.Success")
    }

    private fun executeSign(
        itemInHand: ItemStack,
        itemMeta: ItemMeta,
        commandSender: User,
        command: Command,
        label: String,
        vararg arguments: String,
    ) {
        val dataContainer = itemMeta.persistentDataContainer
        if (dataContainer.has(signKey)) {
            commandSender.commandMsg("Sign.AlreadySigned")
            return
        }

        val lore = itemMeta.lore() ?: mutableListOf<Component>()

        val message = arguments.join().takeIf(String::isNotEmpty) ?: run {
            commandSender.generalMsg("InvalidArguments") {
                syntax(getSyntaxPath(command))
                label(label)
            }
            return
        }

        val parsedDate = parseDate(System.currentTimeMillis(), commandSender)

        val loreMessage =
            commandSender.commandMsg("Sign.Format") {
                prefix(false)
                send(false)
                blankError(true)
                postModifier {
                    it.replace("<MESSAGE>", message)
                        .replace("<DATE>", parsedDate)
                }
            }

        if (loreMessage.isEmpty()) {
            commandSender.generalMsg("ErrorOccurred") { label(label) }
            return
        }

        dataContainer.set(signKey, loreMessage)
        loreMessage.split("\n").filter(String::isNotEmpty).forEach { lore.add(it.asComponent()) }

        itemMeta.lore(lore)
        itemInHand.itemMeta = itemMeta

        commandSender.commandMsg("Sign.Success")
    }
}
