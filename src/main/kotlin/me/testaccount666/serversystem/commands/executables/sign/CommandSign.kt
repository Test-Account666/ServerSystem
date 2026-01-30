package me.testaccount666.serversystem.commands.executables.sign

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.ComponentColor.componentToString
import me.testaccount666.serversystem.utils.ComponentColor.translateToComponent
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

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
        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Sign.NoItemInHand", commandSender).build()
            return
        }
        val meta = itemInHand.itemMeta ?: run {
            command("Sign.NoItemMeta", commandSender).build()
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
            command("Unsign.NotSigned", commandSender).build()
            return
        }

        val lore = itemMeta.lore() ?: mutableListOf<Component>()

        lore.removeIf { loreComponent ->
            val strippedLine = stripColor(componentToString(loreComponent))
            val strippedLore = stripColor(dataContainer.get(signKey, PersistentDataType.STRING))
            strippedLore.split("\n")
                .filter(String::isNotEmpty)
                .any { it.equals(strippedLine, true) }
        }
        itemMeta.lore(lore)

        dataContainer.remove(signKey)

        itemInHand.setItemMeta(itemMeta)
        command("Unsign.Success", commandSender).build()
    }

    private fun executeSign(
        itemInHand: ItemStack,
        itemMeta: ItemMeta,
        commandSender: User,
        command: Command,
        label: String,
        vararg arguments: String
    ) {
        val dataContainer = itemMeta.persistentDataContainer
        if (dataContainer.has(signKey)) {
            command("Sign.AlreadySigned", commandSender).build()
            return
        }

        val lore = itemMeta.lore() ?: mutableListOf<Component>()

        val message = arguments.joinToString(" ").trim().takeIf(String::isNotEmpty) ?: run {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val parsedDate = parseDate(System.currentTimeMillis(), commandSender)

        val loreMessage =
            command("Sign.Format", commandSender) {
                prefix(false)
                send(false)
                blankError(true)
                postModifier {
                    it.replace("<MESSAGE>", message)
                        .replace("<DATE>", parsedDate)
                }
            }.build()

        if (loreMessage.isEmpty()) {
            general("ErrorOccurred", commandSender) { label(label) }.build()
            return
        }

        dataContainer.set(signKey, PersistentDataType.STRING, loreMessage)

        loreMessage.split("\n").filter(String::isNotEmpty).forEach { lore.add(translateToComponent(it)) }

        itemMeta.lore(lore)
        itemInHand.setItemMeta(itemMeta)

        command("Sign.Success", commandSender).build()
    }
}
