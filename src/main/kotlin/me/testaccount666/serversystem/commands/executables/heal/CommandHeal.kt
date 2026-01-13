package me.testaccount666.serversystem.commands.executables.heal

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.util.*

@ServerSystemCommand("heal", ["feed"])
class CommandHeal : AbstractServerSystemCommand() {
    private val _badEffectsSet = setOf(
        PotionEffectType.INSTANT_DAMAGE, PotionEffectType.INFESTED,
        PotionEffectType.SLOWNESS, PotionEffectType.BAD_OMEN, PotionEffectType.DARKNESS, PotionEffectType.GLOWING,
        PotionEffectType.UNLUCK, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.MINING_FATIGUE,
        PotionEffectType.NAUSEA, PotionEffectType.OOZING, PotionEffectType.POISON, PotionEffectType.WITHER,
        PotionEffectType.WEAKNESS, PotionEffectType.LEVITATION
    )

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("heal", true)) {
            handleHealCommand(commandSender, command, label, *arguments)
            return
        }

        handleFeedCommand(commandSender, command, label, *arguments)
    }

    private fun handleFeedCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Feed.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Feed.Other", targetPlayer!!.name)) return

        targetPlayer!!.foodLevel = 20
        targetPlayer.saturation = 20f

        val messagePath = if (isSelf) "Feed.Success" else "Feed.SuccessOther"

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("Feed.Success", targetUser) {
            sender(commandSender.getNameSafe())
            target(targetPlayer.name)
        }.build()
    }

    private fun handleHealCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Heal.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "Heal.Other", targetPlayer.name)) return

        targetPlayer.health = targetPlayer.getAttribute(Attribute.MAX_HEALTH)!!.value
        targetPlayer.foodLevel = 20
        targetPlayer.saturation = 20f
        targetPlayer.fireTicks = 0
        _badEffectsSet.forEach { potionEffectType -> targetPlayer.removePotionEffect(potionEffectType) }

        val messagePath = if (isSelf) "Heal.Success" else "Heal.SuccessOther"

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("Heal.Success", targetUser) {
            sender(commandSender.getNameSafe())
            target(targetPlayer.name)
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) error("(CommandHeal;SyntaxPath) Command is null")

        return when (val commandName = command.name.lowercase(Locale.getDefault())) {
            "heal" -> "Heal"
            "feed" -> "Feed"
            else -> error("(CommandHeal;SyntaxPath) Unexpected value: ${commandName}")
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Heal.Use", false)
    }
}
