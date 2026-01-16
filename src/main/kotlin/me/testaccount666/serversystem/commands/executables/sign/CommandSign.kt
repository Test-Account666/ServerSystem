package me.testaccount666.serversystem.commands.executables.sign

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.ComponentColor.Companion.componentToString
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

@ServerSystemCommand("sign", ["unsign"])
open class CommandSign : AbstractServerSystemCommand() {
    protected val signKey: NamespacedKey = NamespacedKey(instance, "sign")

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, getCommandPermission(command))) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val player = commandSender.getPlayer()!!
        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand.isAir()) {
            command("Sign.NoItemInHand", commandSender).build()
            return
        }
        val meta = itemInHand.itemMeta
        if (meta == null) {
            command("Sign.NoItemMeta", commandSender).build()
            return
        }

        if (command.name.equals("sign", true)) executeSign(itemInHand, meta, commandSender, command, label, *arguments)
        else executeUnsign(itemInHand, meta, commandSender)
    }

    private fun executeUnsign(itemInHand: ItemStack, itemMeta: ItemMeta, commandSender: User) {
        val dataContainer = itemMeta.persistentDataContainer
        if (!dataContainer.has(signKey)) {
            command("Unsign.NotSigned", commandSender).build()
            return
        }

        var lore = itemMeta.lore()
        if (lore == null) lore = ArrayList<Component?>()

        lore.removeIf { loreComponent: Component? ->
            val strippedLine = stripColor(componentToString(loreComponent))
            val strippedLore = stripColor(dataContainer.get(signKey, PersistentDataType.STRING))
            strippedLore.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                .any { anotherString -> strippedLine.equals(anotherString, true) }
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

        var lore = itemMeta.lore()
        if (lore == null) lore = ArrayList<Component?>()

        val message = arguments.joinToString(" ").trim { it <= ' ' }
        if (message.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val parsedDate = parseDate(System.currentTimeMillis(), commandSender)

        val loreMessage = command("Sign.Format", commandSender) {
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

        dataContainer.set<String?, String?>(signKey, PersistentDataType.STRING, loreMessage)

        for (line in loreMessage.split("\n".toRegex()).dropLastWhile { it.isEmpty() }) {
            val lineComponent = translateToComponent(line)
            lore.add(lineComponent)
        }

        itemMeta.lore(lore)
        itemInHand.setItemMeta(itemMeta)

        command("Sign.Success", commandSender).build()
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) error("(CommandSign;SyntaxPath) Command is null")
        return if (command.name.equals("sign", true)) "Sign" else "Unsign"
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, getCommandPermission(command), false)
    }

    private fun getCommandPermission(command: Command): String {
        return if (command.name.equals("sign", true)) "Sign.Use" else "Unsign.Use"
    }
}
